package org.distributed.gateway.filter.url;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

import reactor.core.publisher.Mono;

/**
 * 资产受保护url过滤器
 * 
 * @author lsj
 *
 */
@Component
public class SafeUrlFilter implements GatewayFilter, Ordered {

	private final static Logger logger = LoggerFactory.getLogger(SafeUrlFilter.class);
	
	@Value(value = "${safe.url.assetsweb}")
	private String safeUrls;
	
	@Value(value = "${ip.internal}")
	private String internalIps;
	
	public SafeUrlFilter() {
	}
	
	public SafeUrlFilter(String safeUrls, String internalIps) {
		this.safeUrls = safeUrls;
		this.internalIps = internalIps;
	}
	
	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		if (StringUtils.isEmpty(this.safeUrls)) {
			return chain.filter(exchange);
		}
		ServerHttpRequest request = exchange.getRequest();
		String url = request.getURI().getPath();
		// 将url的字符串转换成数组
		String[] urls = this.safeUrls.split(",");
		// 内部通行ip地址转换成数组
		String[] ips = this.internalIps.split(",");
		// 获取来访ip地址x-forwarded-for
		String ip1 = request.getHeaders().getFirst("x-forwarded-for");
		// 获取nginx转发ip
		String ip2 = request.getRemoteAddress().getAddress().getHostAddress();
		logger.info("x-forwarded-for获取的ip:" + ip1 + ", hostAddress获取的ip:" + ip2 + "; 判断结果：" + Arrays.asList(ips).contains(ip2));
		// 判断
		if (Arrays.asList(urls).contains(url)) {
			if (StringUtils.isEmpty(ip1) && Arrays.asList(ips).contains(ip2)) {
				// 成功继续下一链条
				return chain.filter(exchange);
			}
			// 不合法(响应ip不正确的异常)
			ServerHttpResponse response = exchange.getResponse();
			// 设置headers
			HttpHeaders httpHeaders = response.getHeaders();
			httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
			httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
			// 设置body
			String warningStr = "{\"code\":\"500\",\"message\":\"请求地址不被允许\"}";
			DataBuffer bodyDataBuffer = response.bufferFactory().wrap(warningStr.getBytes());
			return response.writeWith(Mono.just(bodyDataBuffer));
		} else {
			// 成功继续下一链条
			return chain.filter(exchange);
		}
	}

}
