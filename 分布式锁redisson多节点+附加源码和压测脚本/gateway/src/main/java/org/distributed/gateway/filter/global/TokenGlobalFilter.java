package org.distributed.gateway.filter.global;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.distributed.gateway.bean.vo.ResultVO;
import org.distributed.gateway.contants.Codes;
import org.distributed.gateway.contants.Datas;
import org.distributed.gateway.contants.Messages;
import org.distributed.gateway.util.IpUtil;
import org.distributed.gateway.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.fastjson.JSONObject;

import reactor.core.publisher.Mono;

@Component
public class TokenGlobalFilter implements GlobalFilter, Ordered {

	private final static Logger logger = LoggerFactory.getLogger(TokenGlobalFilter.class);

	@Autowired
	private RedisUtil redisUtil;

	@Value("${route.hold.routenames}")
	private String routenames;

	@Override
	public int getOrder() {
		return 10101;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpResponse response = exchange.getResponse();
		// 路由判断
		boolean b = validateRoute(exchange);
		if (b) {
		    return chain.filter(exchange);
		}
		// token验证
		b = validateToken(exchange);
		if (b) {
		    return chain.filter(exchange);
		}
		// 用户冻结验证
		String result = validateFreeze(exchange);
		if (result != null) {
			return response.writeWith(Mono.just(getDataBuffer(response, result)));
		}
		// 用户token验证
		result = validateUserToken(exchange);
		if (result != null) {
			return response.writeWith(Mono.just(getDataBuffer(response, result)));
		}
		// 全部通过
		return chain.filter(exchange);
	}

	private boolean validateRoute(ServerWebExchange exchange) {
		Route route = (Route) exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
		String routeName = route.getId().indexOf(Datas.EUREKA_SUB_PFIX) == -1 ? route.getId()
				: route.getId().substring(Datas.EUREKA_SUB_PFIX.length());
		logger.info("来访的路由地址和连接T：" + routeName + "/" + exchange.getRequest().getURI());
		// 路由模块
		List<String> routes = Arrays.asList(this.routenames.split(","));
		if (routes.contains(routeName)) {
			return false;
		}
		return true;
	}

	private boolean validateToken(ServerWebExchange exchange) {
		List<String> tokens = exchange.getRequest().getHeaders().get(Datas.AUTHORIZATION);
		List<String> userIds = exchange.getRequest().getHeaders().get(Datas.USERID);
		// 前端约定，header没有携带token和userId的请求，则在token是否本人生成过滤器中放行，由后续过滤器验证是否真正为放行资源和token的正确性
		if ((tokens == null || tokens.size() <= 0) && (userIds == null || userIds.size() <= 0)) {
			return true;
		}
		return false;
	}

	private String validateFreeze(ServerWebExchange exchange) {
		String userId = exchange.getRequest().getHeaders().get(Datas.USERID).get(0);
		// 验证用户是否在redis中存在禁用信息
		String freeze = this.redisUtil.getCache(Datas.FREEZE_MAIN + userId);
		if (StringUtils.isNotEmpty(freeze)) {
			logger.info("请求用户被冻结，用户userId为：" + userId);
			return JSONObject.toJSONString(new ResultVO<>(Codes.CODE_501, Messages.USER_FREEZE));
		}
		return null;
	}

	private String validateUserToken(ServerWebExchange exchange) {
		String userId = exchange.getRequest().getHeaders().get(Datas.USERID).get(0);
		String deviceType = exchange.getRequest().getHeaders().get(Datas.DEVICETYPE).get(0);
		// 获取redisToken
		String redisToken = this.redisUtil.getCache(Datas.TOKEN_SAFE + deviceType + Datas.CABLE + userId);
		if (StringUtils.isEmpty(redisToken)) {
			return JSONObject.toJSONString(new ResultVO<>(Codes.CODE_502, Messages.PLEASE_LOGIN));
		}
		// 截取redisToken
		String tokenString = redisToken.substring(0, redisToken.lastIndexOf(Datas.CABLE));
		// 获取用户token
		String token = exchange.getRequest().getHeaders().get(Datas.AUTHORIZATION).get(0);
		logger.info("前端传送的token'：" + token);
		logger.info("截取到的token：" + tokenString);
		if (!token.equals(tokenString)) {
			// 截取此次登录ip
			String ipString = redisToken.substring(redisToken.lastIndexOf(Datas.CABLE) + 1, redisToken.length());
			String ip = IpUtil.getInstance().getIpAddr(exchange.getRequest());
			if (ip.equals(ipString)) {
				return JSONObject.toJSONString(new ResultVO<>(Codes.CODE_503, Messages.LOING_INVALID));
			} else {
				return JSONObject.toJSONString(new ResultVO<>(Codes.CODE_504, Messages.INVALID_ACCOUNT_LOGIN));
			}
		}
		return null;
	}

	private DataBuffer getDataBuffer(ServerHttpResponse response, String result) {
		return response.bufferFactory().wrap(result.getBytes());
	}

}
