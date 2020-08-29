package redisson.redis;

//@Component
public class RedisCache {

//	@Autowired
//	private RedisTemplate<String, Object> redisTemplate;

//	/**
//	 * 获取指定键的值（String）
//	 *
//	 * @param key
//	 * @return
//	 */
//	public String getForValue(String key) {
//		Object obj = this.redisTemplate.opsForValue().get(key);
//		return obj == null ? "" : obj.toString();
//	}
//
//	/**
//	 * 设置指定键的值（String）
//	 *
//	 * @param key
//	 * @param value
//	 */
//	public void putForValue(String key, String value) {
//		this.redisTemplate.opsForValue().set(key, value);
//	}
//
//	/**
//	 * 设置指定键的值带过期时间（String）
//	 *
//	 * @param key
//	 * @param value
//	 */
//	public void putForValue(String key, String value, long timeout, TimeUnit unit) {
//		this.redisTemplate.opsForValue().set(key, value, timeout, unit);
//	}
//
//	/**
//	 * 根据指定键删除值（String）
//	 *
//	 * @param key
//	 * @return
//	 */
//	public boolean deleteForValue(String key) {
//		return this.redisTemplate.delete(key);
//	}
//
//	/**
//	 * 获取哈希结构指定键的值（Hash）
//	 *
//	 * @param key
//	 * @param field
//	 * @return
//	 */
//	public String getForHash(String key, String field) {
//		return this.redisTemplate.opsForHash().get(key, field).toString();
//	}
//
//	/**
//	 * 设置哈希结构指定键的值（Hash）
//	 *
//	 * @param redisKey
//	 * @param key
//	 * @param value
//	 */
//	public void putForHash(String redisKey, String key, String value) {
//		this.redisTemplate.opsForHash().put(redisKey, key, value);
//	}
//
//	/**
//	 * 获取哈希结构指定键的值集合（Hash）
//	 *
//	 * @param key
//	 * @return
//	 */
//	public Map<Object, Object> getForHash(String key) {
//		return this.redisTemplate.opsForHash().entries(key);
//	}
//
//	/**
//	 * 根据key删除一个或多个哈希结构的值（Hash）
//	 *
//	 * @param key
//	 * @param fields
//	 * @return
//	 */
//	public Long deleteForHash(String redisKey, Object... keys) {
//		return this.redisTemplate.opsForHash().delete(redisKey, keys);
//	}
//
//	/**
//	 * 根据一个Set结构的key判断值是否在其中（Set）
//	 *
//	 * @param key
//	 * @param value
//	 * @return
//	 */
//	public boolean isMemberForSet(String key, String value) {
//		return this.redisTemplate.opsForSet().isMember(key, value);
//	}
//
//	/**
//	 * 根据一个Set结构的key设置值（Set）
//	 *
//	 * @param key
//	 * @param values
//	 * @return
//	 */
//	public Long putForSet(String key, Object... values) {
//		return this.redisTemplate.opsForSet().add(key, values);
//	}
//
//	/**
//	 * 加锁
//	 *
//	 * @return
//	 */
//	public String lock(String key, String value, long expire) {
//		String result = this.redisTemplate.execute(new RedisCallback<String>() {
//			@Override
//			public String doInRedis(RedisConnection connection) throws DataAccessException {
//				JedisCommands commands = (JedisCommands) connection.getNativeConnection();
//				return commands.set(key, value, "NX", "EX", expire);
//			}
//		});
//		return result;
//	}
//
//	/**
//	 * 存储在list头部
//	 *
//	 * @param key
//	 * @param value
//	 * @return
//	 */
//	public Long leftPushForList(String key, Object value) {
//		return redisTemplate.opsForList().leftPush(key, value);
//	}
//
//	/**
//	 * 设置过期时间
//	 *
//	 * @param key
//	 * @param timeout
//	 * @param unit
//	 * @return
//	 */
//	public Boolean expire(String key, long timeout, TimeUnit unit) {
//		return redisTemplate.expire(key, timeout, unit);
//	}
//
//	/**
//	 * 增加(自增长), 负数则为自减
//	 *
//	 * @param key
//	 * @return
//	 */
//	public Long incrementForValue(String key, long increment) {
//		return redisTemplate.opsForValue().increment(key, increment);
//	}
//
//	/**
//	 * 为哈希表 key 中的指定字段的整数值加上增量 increment
//	 *
//	 * @param key
//	 * @param field
//	 * @param delta
//	 * @return
//	 */
//	public Double incrementForHash(String key, Object field, double delta) {
//		return redisTemplate.opsForHash().increment(key, field, delta);
//	}
//
//	/**
//	 * 获取列表长度
//	 *
//	 * @param key
//	 * @return
//	 */
//	public Long sizeForList(String key) {
//		return redisTemplate.opsForList().size(key);
//	}
//
//	/**
//	 * 移除并获取列表最后一个元素
//	 *
//	 * @param key
//	 * @return 删除的元素
//	 */
//	public Object rightPopForList(String key) {
//		return redisTemplate.opsForList().rightPop(key);
//	}
//
//	/**
//	 * 获取集合的元素, 从小到大排序
//	 *
//	 * @param key
//	 * @param start 开始位置
//	 * @param end   结束位置, -1查询所有
//	 * @return
//	 */
//	public List<Object> rangeForList(String key, long start, long end) {
//		return redisTemplate.opsForList().range(key, start, end);
//	}
//
//	/**
//	 * 获取哈希表中所有值
//	 *
//	 * @param key
//	 * @return
//	 */
//	public List<Object> getValuesForHash(String key) {
//		return redisTemplate.opsForHash().values(key);
//	}
//
//	public <T> void putForHashAll(String key, Map<T, T> map) {
//		redisTemplate.opsForHash().putAll(key, map);
//	}
	
}
