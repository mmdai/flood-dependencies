package cn.flood.cloud.grpc.config;

import cn.flood.cloud.grpc.fegin.FloodFeignSentinel;
import cn.flood.cloud.grpc.sentinel.FloodBlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import feign.Feign;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;


/**
 * flood cloud 增强配置
 *
 * @author mmdai
 */
@AutoConfiguration
public class FloodCloudAutoConfiguration {

  @Bean
  @Scope("prototype")
  @ConditionalOnMissingBean
  @ConditionalOnProperty(name = "feign.sentinel.enabled")
  public Feign.Builder feignSentinelBuilder() {
    return FloodFeignSentinel.builder();
  }

  /**
   * 用于fegin未写fallback, 限流异常全局处理
   */
  @Bean
  @ConditionalOnMissingBean
  public BlockExceptionHandler blockExceptionHandler() {
    return new FloodBlockExceptionHandler();
  }

}
