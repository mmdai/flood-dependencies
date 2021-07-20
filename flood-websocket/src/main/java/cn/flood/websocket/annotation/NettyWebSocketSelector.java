package cn.flood.websocket.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cn.flood.websocket.standard.ServerEndpointExporter;

@ConditionalOnMissingBean(ServerEndpointExporter.class)
@Configuration
public class NettyWebSocketSelector {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
