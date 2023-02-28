package cn.flood.cloud.nacos;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ObjectUtils;

/**
 * 在服务注册至nacos客户端时，在服务详情中增加相应的元数据，增加服务注册时间,版本号header
 *
 * @author mmdai
 * @version 1.0
 * @date 2022/4/28 15:57
 */
@AutoConfiguration
@ConditionalOnNacosDiscoveryEnabled
@AutoConfigureBefore({SimpleDiscoveryClientAutoConfiguration.class,
    CommonsClientAutoConfiguration.class})
public class NacosDiscoveryClientAutoConfiguration {

  @Value("${spring.application.version}")
  private String version;

  public NacosDiscoveryClientAutoConfiguration() {
  }

  @Bean
  @ConditionalOnMissingBean
  public NacosDiscoveryProperties nacosProperties() {
    return new NacosDiscoveryProperties();
  }

//    @Bean
//    @ConditionalOnMissingBean
//    public DiscoveryClient nacosDiscoveryClient(NacosServiceDiscovery nacosServiceDiscovery) {
//        return new NacosDiscoveryClient(nacosServiceDiscovery);
//    }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(value = {
      "spring.cloud.nacos.discovery.watch.enabled"}, matchIfMissing = true)
  public NacosWatch nacosWatch(NacosServiceManager nacosServiceManager,
      NacosDiscoveryProperties nacosDiscoveryProperties) {
    //更改服务详情中的元数据，增加服务注册时间
    nacosDiscoveryProperties.getMetadata().put("startup.time",
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
    nacosDiscoveryProperties.getMetadata()
        .put("version", ObjectUtils.isEmpty(version) ? "1.0.0" : version);
    return new NacosWatch(nacosServiceManager, nacosDiscoveryProperties);
  }
}
