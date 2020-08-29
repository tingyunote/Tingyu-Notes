package org.distributed.gateway.filter.global;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * @author ransong
 * @version 1.0
 * @date 2020/7/16 0016 9:11
 */
@Component
public class OrginGlobalFilter implements GlobalFilter, Ordered {

    private final static Logger logger = LoggerFactory.getLogger(OrginGlobalFilter.class);

    private static final String ALL = "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN, token, username, client, devicetype, userId";
    private static final String MAX_AGE = "18000L";

    @Override
    public int getOrder() {
        return -300;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
		HttpHeaders headers = response.getHeaders();
        logger.info("处理跨域开始");
		headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, PUT, OPTIONS, DELETE, PATCH");
		headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
		headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
		headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALL);
		headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
        if (request.getMethod() == HttpMethod.OPTIONS) {
            logger.info("处理options");
            response.setStatusCode(HttpStatus.NO_CONTENT);
            return Mono.empty();
        }
        logger.info("处理跨域完成");
        return chain.filter(exchange);
    }
}
