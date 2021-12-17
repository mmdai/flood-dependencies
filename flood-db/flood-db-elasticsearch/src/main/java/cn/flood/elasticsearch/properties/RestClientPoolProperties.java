package cn.flood.elasticsearch.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * es的httpClient连接池配置
 *
 * @author mmdai
 * @date 2020/3/28
 * <p>
 */
@Data
@ConfigurationProperties(prefix = "flood.es.rest-pool")
@RefreshScope
public class RestClientPoolProperties {
    /**
     * 链接建立超时时间
     */
    private Integer connectTimeOut = 1000;
    /**
     * 等待数据超时时间
     */
    private Integer socketTimeOut = 30000;
    /**
     * 连接池获取连接的超时时间
     */
    private Integer connectionRequestTimeOut = 500;
    /**
     * 最大连接数
     */
    private Integer maxConnectNum = 30;
    /**
     * 最大路由连接数(某一个/每服务每次能并行接收的请求数量)
     */
    private Integer maxConnectPerRoute = 10;

    /**
     * keep_alive_strategy
     */
    private Long keepAliveStrategy = -1L;

}
