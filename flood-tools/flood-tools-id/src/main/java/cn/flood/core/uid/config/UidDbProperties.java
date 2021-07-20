package cn.flood.core.uid.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 日志数据源配置,如果不配置则使用当前数据源
 *
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "spring.uid.datasource")
public class UidDbProperties {

    private String url;

    private String username;

    private String password;

    private String driverClassName;

}
