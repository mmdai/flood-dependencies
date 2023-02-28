package cn.flood.cloud.security.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 */
@AutoConfiguration
public class ActuatorSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.httpBasic()
        .and()
        .authorizeRequests()
        .antMatchers("/actuator/**").authenticated()
        .anyRequest().permitAll()
        .and()
        .csrf().disable();
    return http.build();
  }
}
