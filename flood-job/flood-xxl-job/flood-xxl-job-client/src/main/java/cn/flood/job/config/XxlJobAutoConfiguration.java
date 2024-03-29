package cn.flood.job.config;

import cn.flood.job.core.executor.impl.XxlJobSpringExecutor;
import cn.flood.job.properties.XxlExecutorProperties;
import cn.flood.job.properties.XxlJobProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

/**
 * xxl-job 自动封装
 * @author mmdai
 * @version 1.0
 * @date 2021/12/23 11:44
 */

@AutoConfiguration
@EnableConfigurationProperties(XxlJobProperties.class)
public class XxlJobAutoConfiguration {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * 服务名称 包含 XXL_JOB_ADMIN 则说明是 Admin
     */
    private static final String XXL_JOB_ADMIN = "xxl-job-admin";

    /**
     * 配置xxl-job 执行器，提供自动发现 xxl-job-admin 能力
     * @param xxlJobProperties xxl 配置
     * @param environment 环境变量
     * @param discoveryClient 注册发现客户端
     * @return
     */
    @Bean
    public XxlJobSpringExecutor xxlJobSpringExecutor(XxlJobProperties xxlJobProperties, Environment environment,
                                                     DiscoveryClient discoveryClient) {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        XxlExecutorProperties executor = xxlJobProperties.getExecutor();
        // 应用名默认为服务名
        String appName = executor.getAppname();
        if (!StringUtils.hasText(appName)) {
            appName = environment.getProperty("spring.application.name");
        }
        xxlJobSpringExecutor.setAppname(appName);
        xxlJobSpringExecutor.setAddress(executor.getAddress());
        xxlJobSpringExecutor.setIp(executor.getIp());
        xxlJobSpringExecutor.setPort(executor.getPort());
        xxlJobSpringExecutor.setAccessToken(executor.getAccessToken());
        xxlJobSpringExecutor.setLogPath(executor.getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(executor.getLogRetentionDays());

        // 如果配置为空则获取注册中心的服务列表 "http://pig-xxl:9080/xxl-job-admin"
        if (!StringUtils.hasText(xxlJobProperties.getAdmin().getAddresses())) {
            String serverList = discoveryClient.getServices().stream().filter(s -> s.contains(XXL_JOB_ADMIN))
                    .flatMap(s -> discoveryClient.getInstances(s).stream()).map(instance -> String
                            .format("http://%s:%s/%s", instance.getHost(), instance.getPort(), XXL_JOB_ADMIN))
                    .collect(Collectors.joining(","));
            log.info("=========xxlJobAdminAddress: {}", serverList);
            xxlJobSpringExecutor.setAdminAddresses(serverList);
        }
        else {
            xxlJobSpringExecutor.setAdminAddresses(xxlJobProperties.getAdmin().getAddresses());
        }

        return xxlJobSpringExecutor;
    }

}
