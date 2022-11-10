package cn.flood.cloud.grpc.config;

import brave.Tracer;
import cn.flood.cloud.grpc.loadbalancer.RoundRobinWithRequestSeparatedPositionLoadBalancer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.cache.CachesEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.Map;

@SuppressWarnings("unchecked")
@AutoConfiguration
public class DefaultLoadBalancerConfiguration {
    @Autowired
    private ConfigurableApplicationContext context;
    @Autowired(required = false)
    private CachesEndpoint cachesEndpoint;

    @PostConstruct
    public void init() throws NoSuchFieldException, IllegalAccessException {
        if (cachesEndpoint != null) {
            Field cacheManagers = CachesEndpoint.class.getDeclaredField("cacheManagers");
            ReflectionUtils.makeAccessible(cacheManagers);
            Map<String, CacheManager> map = (Map<String, CacheManager>) cacheManagers.get(cachesEndpoint);
            ObjectProvider<LoadBalancerCacheManager> cacheManagerProvider = context
                    .getBeanProvider(LoadBalancerCacheManager.class);
            map.put("LoadBalancerCacheManager", cacheManagerProvider.getIfAvailable());

        }
    }

//    @Bean
//    @ConditionalOnMissingBean
//    public LoadBalancerZoneConfig zoneConfig(Environment environment) {
//        return new LoadBalancerZoneConfig(environment.getProperty("spring.cloud.loadbalancer.zone")==null? "local":
//                environment.getProperty("spring.cloud.loadbalancer.zone"));
//    }
//
//    @Bean
//    public ServiceInstanceListSupplier serviceInstanceListSupplier(
//            DiscoveryClient discoveryClient,
//            Environment env,
//            ConfigurableApplicationContext context,
//            LoadBalancerZoneConfig zoneConfig
//    ) {
//        ObjectProvider<LoadBalancerCacheManager> cacheManagerProvider = context
//                .getBeanProvider(LoadBalancerCacheManager.class);
//        return  //开启服务实例缓存
//                new CachingServiceInstanceListSupplier(
//                        //只能返回同一个 zone 的服务实例
//                        new SameZoneOnlyServiceInstanceListSupplier(
//                                //启用通过 discoveryClient 的服务发现
//                                new DiscoveryClientServiceInstanceListSupplier(
//                                        discoveryClient, env
//                                ),
//                                zoneConfig
//                        )
//                        , cacheManagerProvider.getIfAvailable()
//                );
//    }

    @Bean
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory,
            Tracer tracer
    ) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new RoundRobinWithRequestSeparatedPositionLoadBalancer(
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
                name,
                tracer
        );
    }

}
