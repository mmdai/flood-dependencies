package cn.flood.swagger;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

import io.swagger.annotations.ApiOperation;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * SwaggerConfig
 */
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
    private Environment environment;
	
	@Bean
	public Docket swaggerApi() {
		logger.info("===============================SwaggerConfig");
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
        .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
        .paths(PathSelectors.any())

        .build().securityContexts(Lists.newArrayList(securityContext(),securityContext1())).securitySchemes(Lists.<SecurityScheme>newArrayList(apiKey(),apiKey1()));

	}
	
	private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(environment.getProperty("spring.application.name")+" .API")
                .description("Service API v2.0")
                .termsOfServiceUrl("")
                .version("2.0")
                .build();
    }

    private ApiKey apiKey() {
	        return new ApiKey("Authorization", "Authorization", "header");
	    }

	private ApiKey apiKey1() {
	        return new ApiKey("access_token", "access_token", "header");
	    }

	private SecurityContext securityContext() {
		return SecurityContext.builder()
				.securityReferences(defaultAuth())
				.forPaths(PathSelectors.regex("/.*"))
				.build();
	}

	private SecurityContext securityContext1() {
		return SecurityContext.builder()
				.securityReferences(defaultAuth1())
				.forPaths(PathSelectors.regex("/.*"))
				.build();
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Lists.newArrayList(new SecurityReference("access_token", authorizationScopes));
	}

	List<SecurityReference> defaultAuth1() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Lists.newArrayList(new SecurityReference("access_token", authorizationScopes));
	}


}