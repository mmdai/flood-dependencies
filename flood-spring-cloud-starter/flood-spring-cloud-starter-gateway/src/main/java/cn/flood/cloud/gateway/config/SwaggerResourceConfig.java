//package cn.flood.cloud.gateway.config;

//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cloud.gateway.config.GatewayProperties;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.support.NameUtils;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Component;
//import springfox.documentation.swagger.web.SwaggerResource;
//import springfox.documentation.swagger.web.SwaggerResourcesProvider;
//
///**
// * swagger配置
// *
// * @author mmdai
// */
//@Component
//@Primary
//public class SwaggerResourceConfig implements SwaggerResourcesProvider {

//  public static final String API_URI = "v2/api-docs";
//  public static final String OAS_URI = "v3/api-docs";
//  private final Logger log = LoggerFactory.getLogger(this.getClass());
//  private final RouteLocator routeLocator;
//  private final GatewayProperties gatewayProperties;
//
//  public SwaggerResourceConfig(RouteLocator routeLocator, GatewayProperties gatewayProperties) {
//    this.routeLocator = routeLocator;
//    this.gatewayProperties = gatewayProperties;
//  }
//
//  @Override
//  public List<SwaggerResource> get() {
//    List<SwaggerResource> resources = new ArrayList<>();
//    List<String> routes = new ArrayList<>();
//    //获取所有路由的ID
//    routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));
//    // 记录已经添加过的server，存在同一个应用注册了多个服务在nacos上
//    Set<String> repeated = new HashSet<>();
//    //过滤出配置文件中定义的路由->过滤出Path Route Predicate->根据路径拼接成api-docs路径->生成SwaggerResource
//    gatewayProperties.getRoutes().stream()
//        .filter(routeDefinition -> routes.contains(routeDefinition.getId())).forEach(route ->
//        route.getPredicates().stream()
//            .filter(predicateDefinition -> ("Path").equalsIgnoreCase(predicateDefinition.getName()))
//            .forEach(predicateDefinition -> {
//              if (!repeated.contains(route.getId())) {
//                repeated.add(route.getId());
//                resources.add(swaggerResource(route.getId(),
//                    predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0")
//                        .replace("**", API_URI)));
//              }
//            }));
//    return resources;
//  }
//
//  private SwaggerResource swaggerResource(String name, String location) {
//    SwaggerResource swaggerResource = new SwaggerResource();
//    swaggerResource.setName(name);
//    swaggerResource.setLocation(location);
//    swaggerResource.setSwaggerVersion("2.0");
//    return swaggerResource;
//  }
//}
