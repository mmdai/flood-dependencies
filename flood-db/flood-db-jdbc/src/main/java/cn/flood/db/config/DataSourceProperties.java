package cn.flood.db.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.sql.DataSource;

@Data
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties {

    private String url;

    private String username;

    private String password;

    private String driverClassName;

    private DataSource datasource;
}
