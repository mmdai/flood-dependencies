package cn.flood.oauth.configuration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author mmdai
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
@Import({FloodPermissionConfiguration.class})
public @interface EnableFloodPermission {

}
