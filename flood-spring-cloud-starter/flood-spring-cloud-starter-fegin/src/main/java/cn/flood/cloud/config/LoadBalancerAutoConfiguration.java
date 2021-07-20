package cn.flood.cloud.config;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@LoadBalancerClients(defaultConfiguration = DefaultLoadBalancerConfiguration.class)
public class LoadBalancerAutoConfiguration {

}
