package redisson.config;//package org.bifu.coin.aide.config;
//
//import org.redisson.spring.starter.RedissonProperties;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cache.Cache;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.CachingConfigurerSupport;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.interceptor.CacheErrorHandler;
//import org.springframework.cache.interceptor.KeyGenerator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Scope;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisPassword;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
////@Configuration
////@EnableCaching
//public class RedisConfiguration extends CachingConfigurerSupport {
//
//	private static final Logger logger = LoggerFactory.getLogger(RedisConfiguration.class);
//
//	@Autowired
//	private JedisConnectionFactory jedisConnectionFactory;
//
//	@Autowired
//	private RedissonProperties redissonProperties;
//
//	@Value("${spring.redis.database0}")
//	private Integer database0;
//	@Value("${spring.redis.host}")
//	private String host;
//	@Value("${spring.redis.password}")
//	private String password;
//	@Value("${spring.redis.port}")
//	private int port;
//	@Value("${spring.redis.timeout}")
//	private int timeout;
//	@Value("${spring.redis.jedis.pool.max-idle}")
//	private int maxIdle;
//	@Value("${spring.redis.jedis.pool.max-wait}")
//	private long maxWaitMillis;
//
//	public RedisStandaloneConfiguration getRedisStandaloneConfiguration(Integer database) {
//		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration("47.104.130.105", 6379);
//		configuration.setPassword(RedisPassword.of("123456"));
//		configuration.setDatabase(database);
//		return configuration;
//	}
//
//	@Scope(scopeName = "prototype")
//	public JedisConnectionFactory jedisConnectionFactory(Integer database) {
//		logger.info("Create JedisConnectionFactory successful");
//		RedisStandaloneConfiguration configuration = getRedisStandaloneConfiguration(database);
//		return new JedisConnectionFactory(configuration);
//	}
//
//	@Bean
//	@Override
//	public KeyGenerator keyGenerator() {
//		// 设置自动key的生成规则，配置spring boot的注解，进行方法级别的缓存
//		// 使用：进行分割，可以很多显示出层级关系
//		// 这里其实就是new了一个KeyGenerator对象，采用lambda表达式的写法
//		return (target, method, params) -> {
//			StringBuilder sb = new StringBuilder();
//			sb.append(target.getClass().getName());
//			sb.append(":");
//			sb.append(method.getName());
//			for (Object obj : params) {
//				sb.append(":" + String.valueOf(obj));
//			}
//			String rsToUse = String.valueOf(sb);
//			logger.info("自动生成Redis Key -> [{}]", rsToUse);
//			return rsToUse;
//		};
//	}
//
//	@Bean
//	@Override
//	public CacheManager cacheManager() {
//		// 初始化缓存管理器，在这里我们可以缓存的整体过期时间什么的，这里默认没有配置
//		logger.info("初始化 -> [{}]", "CacheManager RedisCacheManager Start");
//		RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
//				.fromConnectionFactory(jedisConnectionFactory);
//		return builder.build();
//	}
//
//	@Bean
//	public RedisTemplate<String, Object> redisTemplate() {
//		// 配置redisTemplate
//		JedisConnectionFactory jedisConnectionFactory = jedisConnectionFactory(database0);
//		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
//		redisTemplate.setConnectionFactory(jedisConnectionFactory);
//		RedisSerializer<?> stringSerializer = new StringRedisSerializer();
//		redisTemplate.setKeySerializer(stringSerializer); // key序列化
//		redisTemplate.setValueSerializer(stringSerializer); // value序列化
//		redisTemplate.setHashKeySerializer(stringSerializer); // Hash key序列化
//		redisTemplate.setHashValueSerializer(stringSerializer); // Hash value序列化
//		redisTemplate.afterPropertiesSet();
//		return redisTemplate;
//	}
//
//	@Override
//	@Bean
//	public CacheErrorHandler errorHandler() {
//		// 异常处理，当Redis发生异常时，打印日志，但是程序正常走
//		logger.info("初始化 -> [{}]", "Redis CacheErrorHandler");
//		CacheErrorHandler cacheErrorHandler = new CacheErrorHandler() {
//			@Override
//			public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
//				logger.error("Redis occur handleCacheGetError：key -> [{}]", key, e);
//			}
//
//			@Override
//			public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
//				logger.error("Redis occur handleCachePutError：key -> [{}]；value -> [{}]", key, value, e);
//			}
//
//			@Override
//			public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
//				logger.error("Redis occur handleCacheEvictError：key -> [{}]", key, e);
//			}
//
//			@Override
//			public void handleCacheClearError(RuntimeException e, Cache cache) {
//				logger.error("Redis occur handleCacheClearError：", e);
//			}
//		};
//		return cacheErrorHandler;
//	}
//
//}
