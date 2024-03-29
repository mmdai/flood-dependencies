package cn.flood.base.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 配置i18n
 */
@AutoConfiguration
@ComponentScan(basePackages = {"cn.flood.base.aop"})
public class InternationalConfig {

  @Value("${spring.messages.basename:i18n/messages}")
  private String basename;

  @Value("${spring.messages.cache-duration:3600}")
  private int cacheDuration;

  @Value("${spring.messages.encoding:UTF-8}")
  private String encoding;

  @Primary
  @Bean(name = "floodMessageSource")
  public ResourceBundleMessageSource getMessageResource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename(basename);
    messageSource.setCacheSeconds(cacheDuration);
    messageSource.setDefaultEncoding(encoding);
    return messageSource;
  }

  @Bean(name = "localeParser")
  public LocaleParser getLocaleParser(
      @Autowired @Qualifier("floodMessageSource") MessageSource messageSource) {
    LocaleParser localeParser = new LocaleParser(messageSource);
    return localeParser;
  }
}
