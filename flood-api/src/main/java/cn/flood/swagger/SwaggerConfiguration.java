package cn.flood.swagger;

import java.util.ArrayList;
import java.util.List;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;


import io.swagger.annotations.ApiOperation;
import org.springframework.core.env.Profiles;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * SwaggerConfig
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
    private Environment environment;
	
	@Bean
	public Docket swaggerApi() {
		logger.info("===============================SwaggerConfig");
		//添加全局响应状态码
		List<Response> responseMessageList = new ArrayList<>();

		//设置要暴漏接口文档的配置环境
		Profiles profile = Profiles.of("dev", "test");
		boolean flag = environment.acceptsProfiles(profile);
		return new Docket(DocumentationType.OAS_30).
				enable(flag).apiInfo(apiInfo()).
				select().
				apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).
				paths(PathSelectors.any()).
				build().
				securitySchemes(securitySchemes()).
				securityContexts(securityContexts());
	}
	
	private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(environment.getProperty("spring.application.name")+" .API")
                .description("Flood Cloud API v2.5.4")
				.contact(new Contact("mmdai", "nothing", "daiming123.happy@163.com"))
                .termsOfServiceUrl("")
                .version("2.0")
                .build();
    }

	private List<SecurityScheme> securitySchemes() {
		List<SecurityScheme> securitySchemes = new ArrayList<>();
		securitySchemes.add(new ApiKey("Authorization", "Authorization", "header"));
		securitySchemes.add(new ApiKey("access_token", "access_token", "header"));
		securitySchemes.add(new ApiKey("版本号(v1.0.0)", "version", "header"));
		securitySchemes.add(new ApiKey("租户ID", "tenant_id", "header"));
		securitySchemes.add(new ApiKey("平台类型(web,app)", "user_type", "header"));
		securitySchemes.add(new ApiKey("验证码KEY", "captcha_key", "header"));
		securitySchemes.add(new ApiKey("验证码CODE", "captcha_code", "header"));
		return securitySchemes;
	}

	private List<SecurityContext> securityContexts() {
		List<SecurityContext> securityContexts = new ArrayList<>();
		securityContexts.add(SecurityContext.builder()
				.securityReferences(defaultAuth())
				.operationSelector(operationContext -> !operationContext.requestMappingPattern().startsWith("oss") &&
						!operationContext.requestMappingPattern().startsWith("pub"))
				.build());
		return securityContexts;
	}

	private List<SecurityReference> defaultAuth() {
		List<SecurityReference> securityReferences = new ArrayList<>();
		securityReferences.add(new SecurityReference("Authorization", new AuthorizationScope[]{new AuthorizationScope("global", "accessEverything")}));
		securityReferences.add(new SecurityReference("版本号(1.0.0)", new AuthorizationScope[]{new AuthorizationScope("global", "1.0.0")}));
		return securityReferences;
	}


}