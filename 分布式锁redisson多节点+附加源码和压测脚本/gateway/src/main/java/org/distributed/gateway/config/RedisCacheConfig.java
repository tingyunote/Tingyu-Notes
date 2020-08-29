package org.distributed.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @author lsj
 *
 */
@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport {
	
	private static final Logger log = LoggerFactory.getLogger(RedisCacheConfig.class);

	@Value("${spring.redis.database0}")
	private Integer database0;
	@Value("${spring.redis.host}")
	private String host;
	@Value("${spring.redis.password}")
	private String password;
	@Value("${spring.redis.port}")
	private int port;
//	@Value("${spring.redis.sentinel.master}")
//	private String sentinelMaster;
	@Value("${spring.redis.sentinel.nodes:0.0.0.0}")
	private String sentinelNodes;
	
	public RedisStandaloneConfiguration getRedisStandaloneConfiguration(Integer database) {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
		configuration.setPassword(RedisPassword.of(password));
		configuration.setDatabase(database);
		return configuration;
	}
	
	//多个哨兵
	public RedisSentinelConfiguration getRedisSentinelConfiguration() {
		RedisSentinelConfiguration sentinelconfiguration = new RedisSentinelConfiguration();
		RedisNode masterRedisNode = new RedisNode(host, port);
//		masterRedisNode.setName(sentinelMaster);
		sentinelconfiguration.setMaster(masterRedisNode);
		String[] host = sentinelNodes.split(",");
		for(String redisHost : host){
			String[] item = redisHost.split(":");
			String ip = item[0];
			String port = item[1];
			sentinelconfiguration.addSentinel(new RedisNode(ip, Integer.parseInt(port)));
		}
		return sentinelconfiguration;
	}
	
	@SuppressWarnings("deprecation")
	public JedisConnectionFactory jedisConnectionFactory(Integer database) {
		if (sentinelNodes.equals("0.0.0.0")) {
			RedisStandaloneConfiguration configuration = getRedisStandaloneConfiguration(database);
			log.info("Create JedisConnectionFactory no Sentinel successful");
			return new JedisConnectionFactory(configuration);
		} else {
			RedisSentinelConfiguration sentinelConfiguration = getRedisSentinelConfiguration();
			JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(sentinelConfiguration,this.getPoolConfig());
			jedisConnectionFactory.setHostName(host);
			jedisConnectionFactory.setPort(port);
			jedisConnectionFactory.setDatabase(database);
			jedisConnectionFactory.setPassword(password);
			jedisConnectionFactory.afterPropertiesSet();
			log.info("Create JedisConnectionFactory Sentinel successful");
			return jedisConnectionFactory;
		}
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		// 配置redisTemplate
		JedisConnectionFactory jedisConnectionFactory = jedisConnectionFactory(database0);
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		RedisSerializer<?> stringSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer); // key序列化
		redisTemplate.setValueSerializer(stringSerializer); // value序列化
		redisTemplate.setHashKeySerializer(stringSerializer); // Hash key序列化
		redisTemplate.setHashValueSerializer(stringSerializer); // Hash value序列化
		redisTemplate.afterPropertiesSet();
		log.info("Create redisTemplate successful");
		return redisTemplate;
	}

	public JedisPoolConfig getPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(1024);
		jedisPoolConfig.setMaxIdle(100);
		jedisPoolConfig.setMinEvictableIdleTimeMillis(60000);
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
		jedisPoolConfig.setNumTestsPerEvictionRun(-1);
		jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(10000);
		jedisPoolConfig.setMaxWaitMillis(1000);
		jedisPoolConfig.setTestOnBorrow(true);
		jedisPoolConfig.setTestWhileIdle(true);
		jedisPoolConfig.setTestOnReturn(false);
		jedisPoolConfig.setJmxEnabled(true);
		jedisPoolConfig.setBlockWhenExhausted(false);
		return jedisPoolConfig;
	}
}
