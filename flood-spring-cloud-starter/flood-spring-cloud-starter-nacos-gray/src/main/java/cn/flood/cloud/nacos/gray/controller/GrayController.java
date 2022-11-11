package cn.flood.cloud.nacos.gray.controller;


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
public class GrayController {

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
            nacosServiceManager.getNamingService().deregisterInstance(serviceName, groupName, ip, port, clusterName);
        } catch (NacosException e) {
            log.error("deregister from nacos error", e);
            return "error";
        }
        return "success";
    }

    @GetMapping(value = "/api/nacos/startup")
    public String  registerInstance(){
        String serviceName = nacosDiscoveryProperties.getService();
        String groupName = nacosDiscoveryProperties.getGroup();
        String clusterName = nacosDiscoveryProperties.getClusterName();

        String ip = nacosDiscoveryProperties.getIp();
        int port = nacosDiscoveryProperties.getPort();
        try {
            log.info("register from nacos, serviceName:{}, groupName:{}, clusterName:{}, ip:{}, port:{}", serviceName, groupName, clusterName, ip, port);
            nacosServiceManager.getNamingService().registerInstance(serviceName, groupName, ip, port, clusterName);
        } catch (NacosException e) {
            log.error("register from nacos error", e);
            return "error";
        }
        return "success";
    }

}
