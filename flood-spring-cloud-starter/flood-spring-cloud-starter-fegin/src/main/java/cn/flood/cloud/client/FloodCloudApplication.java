package cn.flood.cloud.client;

import cn.flood.cloud.fegin.EnableFloodFeign;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.lang.annotation.*;

/**
 * Cloud启动注解配置
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableDiscoveryClient
@EnableFloodFeign
//@SpringBootApplication(exclude = RibbonAutoConfiguration.class)
@SpringBootApplication
public @interface FloodCloudApplication {

}
