package cn.flood.oauth.configuration.client;

import cn.flood.oauth.configuration.client.restTempate.HttpClientProperties;
import cn.flood.oauth.configuration.client.restTempate.RestTemplateConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启自动配置
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
@Import({HttpClientProperties.class, RestTemplateConfiguration.class, FloodPermissionClientConfiguration.class})
public @interface EnableFloodPermissionClient {
}
