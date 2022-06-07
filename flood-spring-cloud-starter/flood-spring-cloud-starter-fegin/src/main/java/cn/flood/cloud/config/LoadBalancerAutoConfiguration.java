package cn.flood.cloud.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

@AutoConfiguration
@LoadBalancerClients(defaultConfiguration = DefaultLoadBalancerConfiguration.class)
public class LoadBalancerAutoConfiguration {

}
