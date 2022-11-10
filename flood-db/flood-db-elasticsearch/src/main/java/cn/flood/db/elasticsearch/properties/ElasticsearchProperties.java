package cn.flood.db.elasticsearch.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * ES 配置信息
 */
@Data
@ConfigurationProperties(prefix = "spring.elasticsearch")
@RefreshScope
public class ElasticsearchProperties {


    private String hostNames;
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 索引后后缀配置
     */
    private String suffix = "";

}