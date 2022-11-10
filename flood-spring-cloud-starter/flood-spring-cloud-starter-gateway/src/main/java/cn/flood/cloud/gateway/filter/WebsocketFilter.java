package cn.flood.cloud.gateway.filter;

import cn.flood.base.core.Func;
import cn.flood.cloud.gateway.props.WebSocketProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

@Component
@EnableConfigurationProperties({WebSocketProperties.class})
public class WebsocketFilter implements GlobalFilter, Ordered {

    @Autowired
    private WebSocketProperties webSocketProperties;

    /**
     *
     * @param exchange ServerWebExchange是一个HTTP请求-响应交互的契约。提供对HTTP请求和响应的访问，
     *                 并公开额外的 服务器 端处理相关属性和特性，如请求属性
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String upgrade = exchange.getRequest().getHeaders().getUpgrade();

        URI requestUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);

        String scheme = requestUrl.getScheme();

        if (!"ws".equals(scheme) && !"wss".equals(scheme)) {
            return chain.filter(exchange);
//        } else if ((requestUrl.getPath()).contains(webSocketProperties.getPath())) {
        } else {
            String wsScheme = convertWsToHttp(scheme);
            URI wsRequestUrl = UriComponentsBuilder.fromUri(requestUrl).scheme(wsScheme).build().toUri();
            if(Func.isNotEmpty(webSocketProperties.getPort())){
                wsRequestUrl = UriComponentsBuilder.fromUri(requestUrl).scheme(wsScheme).port(webSocketProperties.getPort()).build().toUri();
            }
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, wsRequestUrl);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 2;
    }

    static String convertWsToHttp(String scheme) {
        scheme = scheme.toLowerCase();
        return "ws".equals(scheme) ? "http" : "wss".equals(scheme) ? "https" : scheme;
    }
}
