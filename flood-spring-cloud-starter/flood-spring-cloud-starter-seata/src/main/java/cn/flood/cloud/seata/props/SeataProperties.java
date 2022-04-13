package cn.flood.cloud.seata.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Seata配置
 * @author mmdai
 * @version 1.0
 * @date 2022/4/13 12:28
 */
@Data
@ConfigurationProperties(prefix = "seata")
public class SeataProperties {

    private String applicationId;

    private String txServiceGroup;
}
