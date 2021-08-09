package cn.flood.cloud.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * 路由限流配置
 *
 * @author mmdai
 * @since 2.3.8
 */
//@Configuration
public class RateLimiterConfig {

//	@Primary
//	@Bean(value = "remoteAddrKeyResolver")
//	public KeyResolver remoteAddrKeyResolver() {
//		return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
//	}
}
