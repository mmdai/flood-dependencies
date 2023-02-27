package cn.flood.job.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * xxl-job 配置
 * @author mmdai
 * @version 1.0
 * @date 2021/12/23 11:46
 */
@Data
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {

    @NestedConfigurationProperty
    private XxlAdminProperties admin = new XxlAdminProperties();

    @NestedConfigurationProperty
    private XxlExecutorProperties executor = new XxlExecutorProperties();
}
