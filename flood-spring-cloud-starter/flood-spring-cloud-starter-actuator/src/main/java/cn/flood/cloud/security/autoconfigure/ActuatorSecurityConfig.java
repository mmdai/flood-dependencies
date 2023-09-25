package cn.flood.cloud.security.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.OPTIONS;

/**
 *
 */
@AutoConfiguration
public class ActuatorSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable()
            .authorizeHttpRequests((requests) -> requests
                    .requestMatchers(OPTIONS).permitAll() // allow CORS option calls for Swagger UI
                    .requestMatchers("/actuator/**").permitAll()
                    .anyRequest().authenticated())
            .httpBasic();
    return http.build();
  }
}
