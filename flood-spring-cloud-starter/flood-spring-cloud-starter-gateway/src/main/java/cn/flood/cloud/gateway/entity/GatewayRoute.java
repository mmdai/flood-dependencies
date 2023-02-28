package cn.flood.cloud.gateway.entity;

import java.util.List;
import lombok.Data;
import org.springframework.cloud.gateway.route.RouteDefinition;

/**
 * 网关路由实例
 *
 * @author mmdai
 * @since 2.3.8
 */
@Data
public class GatewayRoute {

  private static final long serialVersionUID = 1L;

  List<RouteDefinition> routes;
}
