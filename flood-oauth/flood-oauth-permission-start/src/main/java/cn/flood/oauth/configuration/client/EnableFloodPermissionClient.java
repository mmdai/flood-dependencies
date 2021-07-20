package cn.flood.oauth.configuration.client;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启自动配置
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
@Import({FloodPermissionClientConfiguration.class})
public @interface EnableFloodPermissionClient {
}
