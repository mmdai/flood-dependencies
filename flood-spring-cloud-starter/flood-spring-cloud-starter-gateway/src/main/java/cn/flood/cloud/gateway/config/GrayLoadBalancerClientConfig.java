package cn.flood.cloud.gateway.config;


import cn.flood.cloud.gateway.filter.GrayReactiveLoadBalancerClientFilter;
import cn.flood.cloud.gateway.loadbalancer.GrayLoadBalancer;
import cn.flood.cloud.gateway.loadbalancer.VersionGrayLoadBalancer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties;
import org.springframework.cloud.gateway.config.GatewayReactiveLoadBalancerClientAutoConfiguration;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 灰度负载模式自动装配
 *
 * @author madi
 * @date 2021-02-24 13:41
 */
@AutoConfiguration
@EnableConfigurationProperties(GatewayLoadBalancerProperties.class)
@AutoConfigureBefore(GatewayReactiveLoadBalancerClientAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class GrayLoadBalancerClientConfig {

	@Primary
	@Bean
	public ReactiveLoadBalancerClientFilter gatewayLoadBalancerClientFilter(GrayLoadBalancer grayLoadBalancer,
																			GatewayLoadBalancerProperties properties) {
		return new GrayReactiveLoadBalancerClientFilter(properties, grayLoadBalancer);
	}

	@Bean
	public GrayLoadBalancer grayLoadBalancer(DiscoveryClient discoveryClient) {
		return new VersionGrayLoadBalancer(discoveryClient);
	}
}
