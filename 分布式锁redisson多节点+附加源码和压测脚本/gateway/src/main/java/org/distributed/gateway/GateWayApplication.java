package org.distributed.gateway;

import java.time.Duration;

import org.distributed.gateway.filter.limit.RateLimitByIpFilter;
import org.distributed.gateway.filter.url.SafeUrlFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * API网关
 * 
 * @author ybc
 * @since 2018-9-6
 */
@SpringBootApplication
public class GateWayApplication {

	@Value(value = "${ip.internal}")
	private String internalIps;

	@Value(value = "${safe.url.assetsweb}")
	private String assetswebSafeUrls;

	public static void main(String[] args) {
		SpringApplication.run(GateWayApplication.class, args);
	}

	@Bean
	public RouteLocator routeLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				// 认证中心
				.route(r -> r.path("/BIFU-AUTH/**")
						.filters(f -> f.stripPrefix(1).filter(new RateLimitByIpFilter(1000, 50, Duration.ofSeconds(1))))
						.uri("lb://BIFU-AUTH").id("BIFU-AUTH"))

				// 用户中心
				.route(r -> r.path("/BIFU-USERCENTER/**")
						.filters(f -> f.stripPrefix(1).filter(new RateLimitByIpFilter(1000, 50, Duration.ofSeconds(1))))
						.uri("lb://BIFU-USERCENTER").id("BIFU-USERCENTER"))

				// 市场
				.route(r -> r.path("/MARKET/api/small/**")
						.filters(f -> f.stripPrefix(1).filter(new RateLimitByIpFilter(1000, 20, Duration.ofSeconds(1))))
						.uri("lb://MARKET").id("MARKET"))
				.route(r -> r.path("/MARKET/**")
						.filters(f -> f.stripPrefix(1).filter(new RateLimitByIpFilter(1000, 50, Duration.ofSeconds(1))))
						.uri("lb://MARKET").id("MARKET"))

				// 资产
				.route(r -> r.path("/ASSETSWEB/**")
						.filters(f -> f.stripPrefix(1)
								.filter(new SafeUrlFilter(this.assetswebSafeUrls, this.internalIps))
								.filter(new RateLimitByIpFilter(1000, 50, Duration.ofSeconds(1))))
						.uri("lb://ASSETSWEB").id("ASSETSWEB"))

				.build();
	}

}
