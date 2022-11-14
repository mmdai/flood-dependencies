package cn.flood.cloud.grpc.loadbalancer;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.config.LoadBalancerZoneConfig;
import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 只返回与当前实例同一个 Zone 的服务实例，不同 zone 之间的服务不互相调用
 * @author mmdai
 */
public class SameZoneOnlyServiceInstanceListSupplier extends DelegatingServiceInstanceListSupplier {

    private final String ZONE = "zone";

    private final LoadBalancerZoneConfig zoneConfig;

    private String zone;

    public SameZoneOnlyServiceInstanceListSupplier(ServiceInstanceListSupplier delegate,
                                                   LoadBalancerZoneConfig zoneConfig) {
        super(delegate);
        this.zoneConfig = zoneConfig;
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return getDelegate().get().map(this::filteredByZone);
    }

    private List<ServiceInstance> filteredByZone(List<ServiceInstance> serviceInstances) {
        if (zone == null) {
            zone = zoneConfig.getZone();
        }
        if (zone != null) {
            List<ServiceInstance> filteredInstances = new ArrayList<>();
            for (ServiceInstance serviceInstance : serviceInstances) {
                String instanceZone = getZone(serviceInstance);
                if (zone.equalsIgnoreCase(instanceZone)) {
                    filteredInstances.add(serviceInstance);
                }
            }
            if (filteredInstances.size() > 0) {
                return filteredInstances;
            }
        }
        //如果没有zone设置, 则返回所有实例
        return serviceInstances;
    }

    private String getZone(ServiceInstance serviceInstance) {
        Map<String, String> metadata = serviceInstance.getMetadata();
        if (metadata != null) {
            return metadata.get(ZONE);
        }
        return null;
    }

}
