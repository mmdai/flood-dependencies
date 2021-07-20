package cn.flood.log.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * ES 配置信息
 */
@Data
@ConfigurationProperties(prefix = "spring.audit-log.es")
@RefreshScope
public class LogElasticsearchProperties {


    private String hostNames;
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}