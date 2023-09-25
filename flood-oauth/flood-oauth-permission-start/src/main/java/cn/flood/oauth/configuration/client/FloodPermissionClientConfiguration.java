package cn.flood.oauth.configuration.client;

import cn.flood.jwtp.TenantInterceptor;
import cn.flood.jwtp.client.ClientInterceptor;
import cn.flood.jwtp.perm.RestUrlPerm;
import cn.flood.jwtp.perm.SimpleUrlPerm;
import cn.flood.jwtp.perm.UrlPerm;
import cn.flood.oauth.configuration.FloodPermissionProperties;
import java.util.Collection;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableConfigurationProperties(FloodPermissionProperties.class)
@ConditionalOnBean(RestTemplate.class)
public class FloodPermissionClientConfiguration implements WebMvcConfigurer,
    ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Autowired
  private FloodPermissionProperties properties;

  @Autowired
  private RestTemplate restTemplate;

  /**
   * 注入simpleUrlPerm
   */
  @ConditionalOnProperty(name = "fpermission.url-perm-type", havingValue = "0")
  @Bean
  public UrlPerm simpleUrlPerm() {
    return new SimpleUrlPerm();
  }

  /**
   * 注入restUrlPerm
   */
  @ConditionalOnProperty(name = "fpermission.url-perm-type", havingValue = "1")
  @Bean
  public UrlPerm restUrlPerm() {
    return new RestUrlPerm();
  }

  /**
   * 添加拦截器
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 获取UrlPerm
    UrlPerm urlPerm = getBean(UrlPerm.class);
    // 获取拦截路径
    String[] path = properties.getPath();
    // 获取排除路径
    String[] excludePath = properties.getExcludePath();
    ClientInterceptor interceptor = new ClientInterceptor(properties.getAuthCenterUrl(), urlPerm,
        restTemplate);
    registry.addInterceptor(interceptor).addPathPatterns(path).excludePathPatterns(excludePath);
    TenantInterceptor tenantInterceptor = new TenantInterceptor();
    registry.addInterceptor(tenantInterceptor);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  /**
   * 获取Bean
   */
  private <T> T getBean(Class<T> clazz) {
    T bean = null;
    Collection<T> beans = applicationContext.getBeansOfType(clazz).values();
    while (beans.iterator().hasNext()) {
      bean = beans.iterator().next();
      if (bean != null) {
        break;
      }
    }
    return bean;
  }

}
