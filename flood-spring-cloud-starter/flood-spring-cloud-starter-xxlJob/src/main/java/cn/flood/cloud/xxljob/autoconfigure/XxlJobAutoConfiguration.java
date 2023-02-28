package cn.flood.cloud.xxljob.autoconfigure;

import cn.flood.job.core.executor.impl.XxlJobSpringExecutor;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ObjectUtils;

/**
 * <p>Title: XxlJobAutoConfiguration</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2020</p>
 *
 * @author mmdai
 * @version 1.0
 * @date 2020/12/23
 */
@EnableConfigurationProperties(XxlJobProperties.class)
@AutoConfiguration
public class XxlJobAutoConfiguration {

  /**
   * 服务名称 包含 XXL_JOB_ADMIN 则说明是 Admin
   */
  private static final String XXL_JOB_ADMIN = "xxl-job-admin";
  private Logger logger = LoggerFactory.getLogger(XxlJobAutoConfiguration.class);
  @Autowired
  private XxlJobProperties xxlJobProperties;

  private DiscoveryClient discoveryClient;

  /******* 接入 RedisDelayQueue  *******/

  @Autowired(required = false)
  public void setDiscoveryClient(DiscoveryClient discoveryClient) {
    this.discoveryClient = discoveryClient;
  }


  @Bean
  public XxlJobSpringExecutor xxlJobExecutor() {
    logger.info(">>>>>>>>>>> xxl-job config init.");
    XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
    xxlJobSpringExecutor.setAppname(xxlJobProperties.getAppname());
    xxlJobSpringExecutor.setAddress(xxlJobProperties.getAddress());
    xxlJobSpringExecutor.setIp(xxlJobProperties.getIp());
    xxlJobSpringExecutor.setPort(xxlJobProperties.getPort());
    xxlJobSpringExecutor.setAccessToken(xxlJobProperties.getAccessToken());
    xxlJobSpringExecutor.setLogPath(xxlJobProperties.getLogpath());
    xxlJobSpringExecutor.setLogRetentionDays(xxlJobProperties.getLogretentiondays());
    // 如果配置为空则获取注册中心的服务列表 "http://ip:port/xxl-job-admin"
    if (ObjectUtils.isEmpty(xxlJobProperties.getAddresses())) {
      String serverList = discoveryClient.getServices().stream()
          .filter(s -> s.contains(XXL_JOB_ADMIN))
          .flatMap(s -> discoveryClient.getInstances(s).stream()).map(instance -> String
              .format("http://%s:%s/%s", instance.getHost(), instance.getPort(), XXL_JOB_ADMIN))
          .collect(Collectors.joining(","));
      xxlJobSpringExecutor.setAdminAddresses(serverList);
    } else {
      xxlJobSpringExecutor.setAdminAddresses(xxlJobProperties.getAddresses());
    }
    return xxlJobSpringExecutor;
  }

  /**
   * 针对多网卡、容器内部署等情况，可借助 "spring-cloud-commons" 提供的 "InetUtils" 组件灵活定制注册IP；
   *
   *      1、引入依赖：
   *          <dependency>
   *             <groupId>org.springframework.cloud</groupId>
   *             <artifactId>spring-cloud-commons</artifactId>
   *             <version>${version}</version>
   *         </dependency>
   *
   *      2、配置文件，或者容器启动变量
   *          spring.cloud.inetutils.preferred-networks: 'xxx.xxx.xxx.'
   *
   *      3、获取IP
   *          String ip_ = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
   */


}