package cn.flood.log.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 日志数据源配置
 * logType=db时生效(非必须)，如果不配置则使用当前数据源
 *
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "spring.audit-log.datasource")
public class LogDbProperties {

    private String url;

    private String username;

    private String password;

    private String driverClassName;

}
