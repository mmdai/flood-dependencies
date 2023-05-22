package cn.flood.api.swagger;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.Response;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.RequestAttribute;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * SwaggerConfig
 */
@AutoConfiguration
@EnableSwagger2WebMvc
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {

  /**
   * Swagger忽略的参数类型
   */
  private final Class[] ignoredParameterTypes = new Class[]{
      ServletRequest.class,
      ServletResponse.class,
      HttpServletRequest.class,
      HttpServletResponse.class,
      RequestAttribute.class,
      HttpSession.class,
      ApiIgnore.class,
      Principal.class,
      Map.class
  };
  private Logger logger = LoggerFactory.getLogger(this.getClass());
  @Autowired
  private Environment environment;

//  /**
//   * 解决与knife4j有兼容问题
//   *
//   * @return
//   * @see https://github.com/xiaoymin/swagger-bootstrap-ui/issues/396
//   * @see https://github.com/springfox/springfox/issues/3462
//   */
//  @Bean
//  public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
//    return new BeanPostProcessor() {
//
//      @Override
//      public Object postProcessAfterInitialization(Object bean, String beanName)
//          throws BeansException {
//        if (bean instanceof WebMvcRequestHandlerProvider) {
//          customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
//        }
//        return bean;
//      }
//
//      private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(
//          List<T> mappings) {
//        List<T> copy = mappings.stream()
//            .filter(mapping -> mapping.getPatternParser() == null)
//            .collect(Collectors.toList());
//        mappings.clear();
//        mappings.addAll(copy);
//      }
//
//      @SuppressWarnings("unchecked")
//      private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
//        try {
//          Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
//          field.setAccessible(true);
//          return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
//        } catch (IllegalArgumentException | IllegalAccessException e) {
//          throw new IllegalStateException(e);
//        }
//      }
//    };
//  }

  @Bean
  public Docket swaggerApi() {
    logger.info("SwaggerConfig");
    //添加全局响应状态码
    List<Response> responseMessageList = new ArrayList<>();

    //设置要暴漏接口文档的配置环境
    Profiles profile = Profiles.of("dev", "test");
    boolean flag = environment.acceptsProfiles(profile);
    return new Docket(DocumentationType.SWAGGER_2).
        enable(flag).apiInfo(apiInfo()).
        select().
        apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).
        paths(PathSelectors.any()).
        build().
        ignoredParameterTypes(ignoredParameterTypes).
        securitySchemes(securitySchemes()).
        securityContexts(securityContexts());
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title(environment.getProperty("spring.application.name") + " .API")
        .description("Flood Cloud API " + SpringBootVersion.getVersion())
        .contact(new Contact("mmdai", "nothing", "daiming123.happy@163.com"))
        .termsOfServiceUrl("")
        .version("2.0")
        .build();
  }

  private List<SecurityScheme> securitySchemes() {
    List<SecurityScheme> securitySchemes = new ArrayList<>();
    securitySchemes.add(new ApiKey("Authorization", "Authorization", "header"));
    securitySchemes.add(new ApiKey("access_token", "access_token", "header"));
    securitySchemes.add(new ApiKey("version", "version", "header"));
    securitySchemes.add(new ApiKey("tenant_id", "tenant_id", "header"));
    securitySchemes.add(new ApiKey("client_id", "client_id", "header"));
    securitySchemes.add(new ApiKey("captcha_key", "captcha_key", "header"));
    securitySchemes.add(new ApiKey("captcha_code", "captcha_code", "header"));
//		securitySchemes.add(new ApiKey("版本号(1.0.0)", "version", "header"));
//		securitySchemes.add(new ApiKey("租户ID", "tenant_id", "header"));
//		securitySchemes.add(new ApiKey("平台类型(web,app)", "client_id", "header"));
//		securitySchemes.add(new ApiKey("验证码KEY", "captcha_key", "header"));
//		securitySchemes.add(new ApiKey("验证码CODE", "captcha_code", "header"));
    return securitySchemes;
  }

  private List<SecurityContext> securityContexts() {
    List<SecurityContext> securityContexts = new ArrayList<>();
    securityContexts.add(SecurityContext.builder()
        .securityReferences(defaultAuth())
//				.forPaths(PathSelectors.regex("^(?!(oss!pub)).*$"))
        //配置只是该请求头的显示
//        .operationSelector(o -> o.requestMappingPattern().matches("/.*"))
        .build());
    return securityContexts;
  }

  private List<SecurityReference> defaultAuth() {
    List<SecurityReference> securityReferences = new ArrayList<>();
    securityReferences.add(new SecurityReference("Authorization",
        new AuthorizationScope[]{new AuthorizationScope("global", "accessEverything")}));
    securityReferences.add(new SecurityReference("version",
        new AuthorizationScope[]{new AuthorizationScope("global", "1.0.0")}));
    securityReferences.add(new SecurityReference("client_id",
        new AuthorizationScope[]{new AuthorizationScope("global", "all")}));
    securityReferences.add(new SecurityReference("tenant_id",
        new AuthorizationScope[]{new AuthorizationScope("global", "0")}));
//		securityReferences.add(new SecurityReference("Authorization", new AuthorizationScope[]{new AuthorizationScope("global", "accessEverything")}));
//		securityReferences.add(new SecurityReference("版本号(1.0.0)", new AuthorizationScope[]{new AuthorizationScope("global", "1.0.0")}));
//		securityReferences.add(new SecurityReference("平台类型(web,app)", new AuthorizationScope[]{new AuthorizationScope("global", "all")}));
//		securityReferences.add(new SecurityReference("租户ID", new AuthorizationScope[]{new AuthorizationScope("global", "0")}));
    return securityReferences;
  }
}