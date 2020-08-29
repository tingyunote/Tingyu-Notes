package org.distributed.gateway.filter.limit;

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
//		logger.info("限频来访ip: " + ip + ", 可用令牌数量：" + bucket.getAvailableTokens());
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
//			logger.info("通过address方式限流获取到的IP为：" + ip);
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
//			logger.info("通过x-forwarded-for限流获取到的IP，且过滤掉内网后的地址为：" + ip);
			if (ip.indexOf(",") != -1) {
				ip = ip.substring(0, ip.indexOf(","));
				return ip.trim();
			}
			return ip.trim();
		}
	}

}
