



## 一.如何对同一IP访问同一接口进行每秒、每分钟、每小时频率限制

{% fancybox %}

![solong](https://ransongblog.oss-cn-hangzhou.aliyuncs.com/rocketMq/solongSan.jpg)

{% endfancybox %}

话不多说，直接开干，首先写一个注解类

<!--more-->

\```

```java
import java.lang.annotation.*;

/**
 * 接口限流
 * @author rs
 *
 */
@Inherited
@Documented
@Target({ElementType.FIELD,ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VisitLimit {
    //标识 指定sec时间段内的访问次数限制
    int limit() default 5;  
    //标识 时间段
    long sec() default 5;
}
```

\```

**使用注解的原因是：我们使用拦截器在请求处理之前，检查某个请求接口是否有该注解，如果有该注解，获取访问次数和时间段（比如：在1s中只能访问一次）。接下来我们就来写一个拦截器**

\```

```java
import org.test.annotation.VisitLimit;
import org.test.exception.BusinessException;
import org.test.redis.RedisCache;
import org.test.service.redis.RedisService;
import org.test.util.IPUtils;
import org.test.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

@Component
public class VisitLimitInterceptor extends HandlerInterceptorAdapter {
	 
    @Autowired
	private RedisUtils redisService;

    /**
     * 处理请求之前被调用
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            if (!method.isAnnotationPresent(VisitLimit.class)) {
                return true;
            }
            VisitLimit accessLimit = method.getAnnotation(VisitLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int limit = accessLimit.limit();
            long sec = accessLimit.sec();
            String key = IPUtils.getIpAddr(request) + request.getRequestURI();
            Integer maxLimit =null;
            Object value =redisService.get(key);
            if(value!=null && !value.equals("")) {
            	maxLimit = Integer.valueOf(String.valueOf(value));
            }
            if (maxLimit == null) {
            	redisService.set(key, "1", sec);
            } else if (maxLimit < limit) {
            	Integer i = maxLimit+1;
            	redisService.set(key, i.toString(), sec);
            } else {
//              output(response, "请求太频繁!");
//            	return false;
                throw new BusinessException(500,"请求太频繁!");
            }
        }
        return true;
    }
 
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
 
    }
 
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
 
    }
```

\```

这里用到了redis,解释一下redis的key（IP+URL）记录了某个**ip访问某个接口**，value存的是**访问的次数**，加上一个过期时间，过期时间就是我们在注解上赋值的值。

这里的redis的部分代码也贴出来

\```

```java
@Service
public class RedisUtils {
    @Resource
    private RedisTemplate redisTemplate;

  /**
     * 写入缓存设置时效时间
     *
     * @param key
     * @param value
     * @param expireTime 有效时间，单位秒
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
```

\```

\#怎么获取用户的**真实IP**呢???如下

\```

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * IP Utils
 * @author rs
 *
 */
public class IPUtils {
	private static Logger logger = LoggerFactory.getLogger(IPUtils.class);

	/**
	 * 获取IP地址
	 * 
	 * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
	 * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
	 */
	public static String getIpAddr(HttpServletRequest request) {
    	String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
	         if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
	             // 多次反向代理后会有多个ip值，第一个ip才是真实ip
	             if( ip.indexOf(",")!=-1 ){
	                 ip = ip.split(",")[0];
	             }
	         }
	         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	             ip = request.getHeader("Proxy-Client-IP");
	         }
	         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	             ip = request.getHeader("WL-Proxy-Client-IP");
	         }
	         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	             ip = request.getHeader("HTTP_CLIENT_IP");
	         }
	         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	             ip = request.getHeader("HTTP_X_FORWARDED_FOR");
	         }
	         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	             ip = request.getHeader("X-Real-IP");
	         }
	         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	             ip = request.getRemoteAddr();
	         }
        } catch (Exception e) {
        	logger.error("IPUtils ERROR ", e);
        }
        
        return ip;
    }
	
}
```

\```

下面来正式使用一下

\```

```java
@VisitLimit(limit = 1, sec = 1)
@RequestMapping(value = "/close", method = RequestMethod.POST)
```

\```

这种方式不能很好的应对突发请求，需要对这一类情形平滑处理，比如200ms处理一个请求，下面就到**令牌桶**出场了！



## 二、令牌桶实战介绍

2.1 先来个总结吧，让大家分清什么时候用令牌桶，什么时候用漏桶

- 令牌桶：生产一个令牌消费一个
- 漏桶： 处理大流量，并且以固定的速度平滑处理

{% fancybox %}

![img](https://pic3.zhimg.com/80/v2-10884f6a0aa783af076d2c2f5ab5c7ba_720w.png)

{% endfancybox %}

 

使用场景：geteway网关

Bucket4j是基于令牌桶算法实现

```java
package org.test.gateway.filter.limit;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import reactor.core.publisher.Mono;

@Component
public class RateLimitByIpFilter implements GatewayFilter, Ordered {

	private final static Logger logger = LoggerFactory.getLogger(RateLimitByIpFilter.class);

	private int capacity;

	private int refillTokens;

	private Duration refillDuration;

	private static final Map<String, Bucket> CACHE = new ConcurrentHashMap<>();

	public RateLimitByIpFilter() {
		
	}
	
	public RateLimitByIpFilter(int capacity, int refillTokens, Duration refillDuration) {
		this.capacity = capacity;
		this.refillTokens = refillTokens;
		this.refillDuration = refillDuration;
	}
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String ip = getIpAddr(exchange.getRequest());
		if (ip.indexOf("192.168") != -1 || ip.indexOf("172.31.202") != -1) {
			return chain.filter(exchange);
		}
		Bucket bucket = CACHE.computeIfAbsent(ip, k -> createNewBucket());
		logger.info("限频来访ip: " + ip + ", 可用令牌数量：" + bucket.getAvailableTokens());
		if (bucket.tryConsume(1)) {
			return chain.filter(exchange);
		} else {
			// 不合法(超过限流)
			ServerHttpResponse response = exchange.getResponse();
			// 设置headers
			HttpHeaders httpHeaders = response.getHeaders();
			httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
			httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
			// 设置body
			String warningStr = "{\"code\":\"500\",\"message\":\"超过限流\"}";
			DataBuffer bodyDataBuffer = response.bufferFactory().wrap(warningStr.getBytes());
			return response.writeWith(Mono.just(bodyDataBuffer));
		}
	}

	@Override
	public int getOrder() {
		return -1000;
	}

	private Bucket createNewBucket() {
		Refill refill = Refill.of(refillTokens, refillDuration);
		Bandwidth limit = Bandwidth.classic(capacity, refill);
		return Bucket4j.builder().addLimit(limit).build();
	}

	public static String getIpAddr(ServerHttpRequest request) {
		String ip = "";
		String str = request.getHeaders().getFirst("x-forwarded-for");
		if (StringUtils.isEmpty(str)) {
			ip = request.getRemoteAddress().getAddress().getHostAddress();
			logger.info("通过address方式限流获取到的IP为：" + ip);
			return ip;
		} else {
			String[] ips = str.split(",");
			for (String s : ips) {
				if (s.indexOf("192.168") != -1 || s.indexOf("172.31.202") != -1) {
					continue;
				}
				ip = ip + s + ",";
			}
			ip = ip.substring(0, ip.length() - 1);
			logger.info("通过x-forwarded-for限流获取到的IP，且过滤掉内网后的地址为：" + ip);
			if (ip.indexOf(",") != -1) {
				ip = ip.substring(0, ip.indexOf(","));
				return ip.trim();
			}
			return ip.trim();
		}
	}

}

```

配置route: TEST-AUTH/**的api接口都会路由到TEST-AUTH服务

```java
@SpringBootApplication
public class GateWayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GateWayApplication.class, args);
	}

	@Bean
	public RouteLocator routeLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				// 认证中心
				.route(r -> r.path("/TEST-AUTH/**")
						.filters(f -> f.stripPrefix(1).filter(new RateLimitByIpFilter(1, 1, Duration.ofSeconds(1))))
						.uri("lb://TEST-AUTH").id("TEST-AUTH"))
				.build();
	}

}
```

## 三、分布式限流（这里摘自京东抢购业务）

使用Redis+Lua的方式来实现

```lua
local key = "rate.limit:" .. KEYS[1] --限流KEY
local limit = tonumber(ARGV[1])        --限流大小
local current = tonumber(redis.call('get', key) or "0")
if current + 1 > limit then --如果超出限流大小
  return 0
else  --请求数+1，并设置1秒过期
  redis.call("INCRBY", key,"1")
   redis.call("expire", key,"1")
   return  1
end

```

```java
public static boolean accquire() throws IOException, URISyntaxException {
    Jedis jedis = new Jedis("127.0.0.1");
    File luaFile = new File(RedisLimitRateWithLUA.class.getResource("/").toURI().getPath() + "limit.lua");
    String luaScript = FileUtils.readFileToString(luaFile);

    String key = "ip:" + System.currentTimeMillis()/1000; // 当前秒
    String limit = "5"; // 最大限制
    List<String> keys = new ArrayList<String>();
    keys.add(key);
    List<String> args = new ArrayList<String>();
    args.add(limit);
    Long result = (Long)(jedis.eval(luaScript, keys, args)); // 执行lua脚本，传入参数
    return result == 1;
}
```

简单说明一下：redis k = rate.limit:ip:当前秒  V: 5 



欢迎指正交流哦！！ 



欢迎关注我的微信公众号，会首发一些最新文章哦！

{% fancybox %}

![solong](https://ransongblog.oss-cn-hangzhou.aliyuncs.com/wechat_public_for_tingyu.jpg)

{% endfancybox %}