package cn.flood.tools;


import cn.flood.tools.captcha.TokenFactory;
import cn.flood.tools.captcha.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class CaptchaServiceConfiguration {

  @Bean
  public TokenFactory getTokenFactory() {
    return new TokenFactory();
  }

  @Bean
  public TokenService getTokenService(@Autowired TokenFactory tokenFactory) {
    return new TokenService(tokenFactory);
  }

}
