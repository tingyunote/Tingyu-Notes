package org.distributed.gateway.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * redis工具类
 * 
 * @author lsj
 *
 */
@Component
public class RedisUtil {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public <T> boolean putCache(String key, T obj) {
		final byte[] bkey = key.getBytes();
		final byte[] bvalue = ProtoStuffSerializerUtil.serialize(obj);
		boolean result = this.redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.setNX(bkey, bvalue);
			}
		});
		return result;
	}

	public <T> void putCacheWithExpireTime(String key, T obj, final long expireTime) {
		final byte[] bkey = key.getBytes();
		final byte[] bvalue = ProtoStuffSerializerUtil.serialize(obj);
		this.redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				connection.setEx(bkey, expireTime, bvalue);
				return true;
			}
		});
	}

	public void putMemberCache(String key, String value, final long offset) {
		this.redisTemplate.opsForValue().set(key, value, offset);
	}

	public <T> boolean putListCache(String key, List<T> objList) {
		final byte[] bkey = key.getBytes();
		final byte[] bvalue = ProtoStuffSerializerUtil.serializeList(objList);
		boolean result = this.redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.setNX(bkey, bvalue);
			}
		});
		return result;
	}

	// 存入消息
	public <T> boolean putHashCache(String key, HashMap<String, T> object) {
		final byte[] bkey = key.getBytes();
		final HashMap<byte[], byte[]> ham = new HashMap<>();
		for (Entry<String, T> entry : object.entrySet()) {
			ham.put(ProtoStuffSerializerUtil.serialize(entry.getKey()),
					ProtoStuffSerializerUtil.serialize(entry.getValue()));
		}
		boolean result = this.redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				connection.hMSet(bkey, ham);
				return true;
			}
		});
		return result;
	}

	// 获取消息
	public <T> HashMap<String, T> getHashCache(final String key, Class<T> targetClass) {
		final byte[] bkey = key.getBytes();
		HashMap<String, T> ham = new HashMap<>();
		Map<byte[], byte[]> result = this.redisTemplate.execute(new RedisCallback<Map<byte[], byte[]>>() {
			@Override
			public Map<byte[], byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.hGetAll(bkey);
			}
		});
		if (result == null) {
			return null;
		}
		for (Entry<byte[], byte[]> entry : result.entrySet()) {
			ham.put(ProtoStuffSerializerUtil.deserialize(entry.getKey(), String.class),
					ProtoStuffSerializerUtil.deserialize(entry.getValue(), targetClass));
		}
		return ham;
	}

	// 删除消息
	public Long deleteHashCache(final String key, final String field) {
		final byte[] bkey = key.getBytes();
		return this.redisTemplate.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				Map<byte[], byte[]> result = connection.hGetAll(bkey);
				for (Entry<byte[], byte[]> entry : result.entrySet()) {
					if (field.equals(ProtoStuffSerializerUtil.deserialize(entry.getKey(), String.class))) {
						connection.hDel(bkey, entry.getKey());
					}
				}
				return 0L;
			}
		});
	}

	public <T> boolean putListCacheWithExpireTime(String key, List<T> objList, final long expireTime) {
		final byte[] bkey = key.getBytes();
		final byte[] bvalue = ProtoStuffSerializerUtil.serializeList(objList);
		boolean result = this.redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				connection.setEx(bkey, expireTime, bvalue);
				return true;
			}
		});
		return result;
	}

	public <T> T getCache(final String key, Class<T> targetClass) {
		byte[] result = this.redisTemplate.execute(new RedisCallback<byte[]>() {
			@Override
			public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.get(key.getBytes());
			}
		});
		if (result == null) {
			return null;
		}
		return ProtoStuffSerializerUtil.deserialize(result, targetClass);
	}
	
	public String getCache(final String key) {
		//如果返回null，说明key不存在；如果返回""，说明key存在，值为""----业务上需要判断
		return redisTemplate.opsForValue().get(key)==null ? "":redisTemplate.opsForValue().get(key).toString();
	}

	public <T> List<T> getListCache(final String key, Class<T> targetClass) {
		byte[] result = this.redisTemplate.execute(new RedisCallback<byte[]>() {
			@Override
			public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.get(key.getBytes());
			}
		});
		if (result == null) {
			return null;
		}
		return ProtoStuffSerializerUtil.deserializeList(result, targetClass);
	}

	/**
	 * 精确删除key
	 * 
	 * @param key
	 */
	public void deleteCache(String key) {
		this.redisTemplate.delete(key);
	}

	/**
	 * 模糊删除key
	 * 
	 * @param pattern
	 */
	public void deleteCacheWithPattern(String pattern) {
		Set<String> keys = this.redisTemplate.keys(pattern);
		this.redisTemplate.delete(keys);
	}

	/**
	 * 清空所有缓存
	 */
	public void clearCache() {
		deleteCacheWithPattern("user:|*");
	}

}
