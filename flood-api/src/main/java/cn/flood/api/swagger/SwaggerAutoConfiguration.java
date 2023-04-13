package cn.flood.api.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.OpenAPIService;
import org.springdoc.core.PropertyResolverUtils;
import org.springdoc.core.SecurityService;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.providers.JavadocProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;

@AutoConfiguration
@ConditionalOnClass({OpenAPI.class})
@ConditionalOnProperty(prefix = "springdoc.api-docs", name = "enabled", havingValue = "true", matchIfMissing = true) // 设置为 false 时，禁用
public class SwaggerAutoConfiguration {

  @Autowired
  private Environment environment;

  private final static String HEADER_TENANT_ID = "tenant_id";
  private final static String HEADER_VERSION = "tenant_id";
  private final static String HEADER_CLIENT_ID = "client_id";
  private final static String HEADER_CAPTCHA_KEY = "captcha_key";
  private final static String HEADER_CAPTCHA_CODE = "captcha_code";
  // ========== 全局 OpenAPI 配置 ==========

  @Bean
  public OpenAPI createApi() {
    Map<String, SecurityScheme> securitySchemas = buildSecuritySchemes();
    OpenAPI openAPI = new OpenAPI()
        // 接口信息
        .info(buildInfo())
        // 接口安全配置
        .components(new Components().securitySchemes(securitySchemas))
        .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION));
    securitySchemas.keySet().forEach(key -> openAPI.addSecurityItem(new SecurityRequirement().addList(key)));
    return openAPI;
  }

  /**
   * API 摘要信息
   */
  private Info buildInfo() {
    return new Info()
        .title(environment.getProperty("spring.application.name") + " .API")
        .description("Flood Cloud API " + SpringBootVersion.getVersion())
        .version(SpringBootVersion.getVersion())
        .contact(new Contact().name("mmdai").url("").email("daiming123.happy@163.com"))
        .license(new License().name("Apache License").url("http://www.apache.org/licenses/"));
  }

  /**
   * 安全模式，这里配置通过请求头 Authorization 传递 token 参数
   */
  private Map<String, SecurityScheme> buildSecuritySchemes() {
    Map<String, SecurityScheme> securitySchemes = new HashMap<>();
    SecurityScheme securityScheme = new SecurityScheme()
        .type(SecurityScheme.Type.APIKEY) // 类型
        .name(HttpHeaders.AUTHORIZATION) // 请求头的 name
        .in(SecurityScheme.In.HEADER); // token 所在位置
    securitySchemes.put(HttpHeaders.AUTHORIZATION, securityScheme);
    return securitySchemes;
  }

  /**
   * 自定义 OpenAPI 处理器
   */
  @Bean
  public OpenAPIService openApiBuilder(Optional<OpenAPI> openAPI,
      SecurityService securityParser,
      SpringDocConfigProperties springDocConfigProperties,
      PropertyResolverUtils propertyResolverUtils,
      Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomizers,
      Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomizers,
      Optional<JavadocProvider> javadocProvider) {

    return new OpenAPIService(openAPI, securityParser, springDocConfigProperties,
        propertyResolverUtils, openApiBuilderCustomizers, serverBaseUrlCustomizers, javadocProvider);
  }

  // ========== 分组 OpenAPI 配置 ==========

  /**
   * 所有模块的 API 分组
   */
  @Bean
  public GroupedOpenApi allGroupedOpenApi() {
    return buildGroupedOpenApi("all", "");
  }

  public static GroupedOpenApi buildGroupedOpenApi(String group) {
    return buildGroupedOpenApi(group, group);
  }

  public static GroupedOpenApi buildGroupedOpenApi(String group, String path) {
    return GroupedOpenApi.builder()
        .group(group)
        .pathsToMatch( path + "/**")
        .addOperationCustomizer((operation, handlerMethod) -> operation
            .addParametersItem(buildTenantHeaderParameter())
            .addParametersItem(buildVersionHeaderParameter())
            .addParametersItem(buildVersionHeaderParameter())
            .addParametersItem(buildSecurityHeaderParameter()))
        .build();
  }


  /**
   * 构建 Tenant 租户编号请求头参数
   *
   * @return 多租户参数
   */
  private static Parameter buildTenantHeaderParameter() {
    return new Parameter()
        .name(HEADER_TENANT_ID) // header 名
        .description("租户编号") // 描述
        .in(String.valueOf(SecurityScheme.In.HEADER)) // 请求 header
        .schema(new IntegerSchema()._default(1L).name(HEADER_TENANT_ID).description("租户编号")); // 默认：使用租户编号为 1
  }

  /**
   * 构建 版本号请求头参数
   *
   * @return 版本号参数
   */
  private static Parameter buildVersionHeaderParameter() {
    return new Parameter()
        .name(HEADER_VERSION) // header 名
        .description("版本号") // 描述
        .in(String.valueOf(SecurityScheme.In.HEADER)) // 请求 header
        .schema(new StringSchema()._default("1.0.0").name(HEADER_VERSION).description("版本号")); // 默认：使用版本号为 1.0.0
  }

  /**
   * 构建 所属系统编号请求头参数
   *
   * @return 系统编号参数
   */
  private static Parameter buildClientHeaderParameter() {
    return new Parameter()
        .name(HEADER_CLIENT_ID) // header 名
        .description("系统编号") // 描述
        .in(String.valueOf(SecurityScheme.In.HEADER)) // 请求 header
        .schema(new StringSchema()._default("ALL").name(HEADER_CLIENT_ID).description("系统编号")); // 默认：使用系统编号为 ALL
  }

  /**
   * 构建 Authorization 认证请求头参数
   *
   * 解决 Knife4j <a href="https://gitee.com/xiaoym/knife4j/issues/I69QBU">Authorize 未生效，请求header里未包含参数</a>
   *
   * @return 认证参数
   */
  private static Parameter buildSecurityHeaderParameter() {
    return new Parameter()
        .name(HttpHeaders.AUTHORIZATION) // header 名
        .description("认证 Token") // 描述
        .in(String.valueOf(SecurityScheme.In.HEADER)) // 请求 header
        .schema(new StringSchema()._default("Bearer test1").name(HEADER_TENANT_ID).description("认证 Token")); // 默认：使用用户编号为 1
  }
}
