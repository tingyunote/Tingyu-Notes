package org.distributed.gateway.util;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IpUtil {

	private IpUtil() {
	}

	private static class IpUtilHolder {
		private final static IpUtil instance = new IpUtil();
	}

	public static IpUtil getInstance() {
		return IpUtilHolder.instance;
	}

	// 获取用户真实IP
	public String getIpAddr(ServerHttpRequest request) {
		String ip = "";
		String ips = request.getHeaders().getFirst("x-forwarded-for");
		log.info("IpUtil get header ips : {}",ips);
		if (StringUtils.isEmpty(ips)) {
			ip = request.getRemoteAddress().getAddress().getHostAddress();
			log.info("IpUtil get request ip : {}",ip);
			return ip;
		} else {
			return ips.indexOf(",") != -1 ? ips.substring(0, ips.indexOf(",")) : ips;
		}
	}

}
