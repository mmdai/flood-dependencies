package cn.flood.cloud.seata.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/4/25 10:33
 */
@ConditionalOnWebApplication
public class SeataHandlerInterceptorConfiguration implements WebMvcConfigurer {

  public SeataHandlerInterceptorConfiguration() {
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new SeataHandlerInterceptor()).addPathPatterns(new String[]{"/**"});
  }
}
