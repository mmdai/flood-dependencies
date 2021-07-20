package cn.flood.delay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.delay")
public class DelayProperties {

    private String name;
}
