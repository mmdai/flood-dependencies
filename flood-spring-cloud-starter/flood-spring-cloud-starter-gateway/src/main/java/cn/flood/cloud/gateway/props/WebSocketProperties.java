package cn.flood.cloud.gateway.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * websocket 配置
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "websocket")
public class WebSocketProperties {

  private String path;
  /**
   * 是否启用网关鉴权模式
   */
  private Integer port;
}
