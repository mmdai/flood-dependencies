package cn.flood.cloud.nacos.controller;


import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/6/20 9:23
 */
@RestController
@Slf4j
public class DeregisterController {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Autowired
    private NacosServiceManager nacosServiceManager;

    @GetMapping(value = "/api/nacos/shutdown")
    public String deregisterInstance(){
        String serviceName = nacosDiscoveryProperties.getService();
        String groupName = nacosDiscoveryProperties.getGroup();
        String clusterName = nacosDiscoveryProperties.getClusterName();

        String ip = nacosDiscoveryProperties.getIp();
        int port = nacosDiscoveryProperties.getPort();
        try {
            log.info("deregister from nacos, serviceName:{}, groupName:{}, clusterName:{}, ip:{}, port:{}", serviceName, groupName, clusterName, ip, port);
            nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties()).deregisterInstance(serviceName, groupName, ip, port, clusterName);
        } catch (NacosException e) {
            log.error("deregister from nacos error", e);
            return "error";
        }
        return "success";
    }

}
