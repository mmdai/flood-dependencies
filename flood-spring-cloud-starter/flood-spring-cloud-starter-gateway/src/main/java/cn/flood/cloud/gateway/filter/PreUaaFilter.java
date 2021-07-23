package cn.flood.cloud.gateway.filter;

import cn.flood.Func;
import cn.flood.cloud.gateway.props.FloodApiProperties;
import cn.flood.cloud.gateway.result.Result;
import cn.flood.cloud.gateway.result.ResultWapper;
import cn.flood.lang.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关统一的token验证
 */
@Slf4j
@Component
@EnableConfigurationProperties({FloodApiProperties.class})
public class PreUaaFilter implements GlobalFilter, Ordered {

    /**
     * 权限认证的排序
     */
    public static final int MATE_UAA_FILTER_ORDER = -200;

    @Autowired
    private FloodApiProperties floodApiProperties;

    /**
     * 路径前缀以/flood开头，如flood-system
     */
    public static final String PATH_PREFIX = "/flood";

    /**
     * 索引自1开头检索，跳过第一个字符就是检索的字符的问题
     */
    public static final int FROM_INDEX = 1;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 如果未启用网关验证，则跳过
        if (!floodApiProperties.getEnable()) {
            return chain.filter(exchange);
        }

        //　如果在忽略的url里，则跳过
        String path = replacePrefix(exchange.getRequest().getURI().getPath());
        String requestUrl = exchange.getRequest().getURI().getRawPath();
        if (ignore(path) || ignore(requestUrl)) {
            return chain.filter(exchange);
        }

        // 验证token是否有效
        ServerHttpResponse resp = exchange.getResponse();
        String headerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (headerToken == null) {
            return unauthorized(resp, "没有携带Token信息！");
        }
        return chain.filter(exchange);
    }

    /**
     * 检查是否忽略url
     * @param path 路径
     * @return boolean
     */
    private boolean ignore(String path) {
        return floodApiProperties.getExcludePath().stream()
                .map(url -> url.replace("/**", ""))
                .anyMatch(path::startsWith);
    }

    /**
     * 移除模块前缀
     * @param path 路径
     * @return String
     */
    private String replacePrefix(String path) {
        if (path.startsWith(PATH_PREFIX)) {
            return path.substring(path.indexOf(StringPool.SLASH, FROM_INDEX));
        }
        return path;
    }

    private Mono<Void> unauthorized(ServerHttpResponse resp, String msg) {
        resp.setStatusCode(HttpStatus.UNAUTHORIZED);
        resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        Result<?> result = ResultWapper.wrap(String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                HttpStatus.UNAUTHORIZED.getReasonPhrase());
        DataBuffer dataBuffer = resp.bufferFactory().wrap(Func.toJson(result).getBytes());
        return resp.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return MATE_UAA_FILTER_ORDER;
    }
}
