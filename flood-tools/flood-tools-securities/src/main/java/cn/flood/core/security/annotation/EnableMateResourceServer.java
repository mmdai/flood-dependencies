package cn.flood.core.security.annotation;

import cn.flood.core.security.config.FloodResourceServerConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 资源服务注解
 *
 * @author pangu
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FloodResourceServerConfig.class)
public @interface EnableMateResourceServer {
}
