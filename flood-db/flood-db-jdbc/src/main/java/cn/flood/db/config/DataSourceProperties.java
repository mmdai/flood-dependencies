package cn.flood.db.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.sql.DataSource;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties {

    private String url;

    private String username;

    private String password;

    private String driverClassName;

    private DataSource datasource;

    /**
     * 是否需要复用druid线程池， 因为Kylin不使用druid
     */
    private boolean threadPool = true;
}
