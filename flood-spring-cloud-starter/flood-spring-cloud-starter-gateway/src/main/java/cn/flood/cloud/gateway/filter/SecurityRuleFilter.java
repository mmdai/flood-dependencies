package cn.flood.cloud.gateway.filter;

import cn.flood.cloud.gateway.service.SafeRuleService;
import cn.flood.lang.StringPool;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 黑名单
 * @author mmdai
 */
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration(proxyBeanMethods = false)
public class SecurityRuleFilter implements WebFilter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String ACCESS_CONTROL_MAX_AGE = "3600L";

    private final SafeRuleService safeRuleService;

    @Override
    @SuppressWarnings("all")
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        /**
         * 是否开启黑名单
         * 从redis里查询黑名单是否存在
         */
        log.debug("进入黑名单模式");
        // 检查黑名单
        Mono<Void> result = safeRuleService.filterBlackList(exchange);
        if (result != null) {
            return result;
        }

        /**
         * 增加CORS(flood-base filter 已经去掉跨域cors)
         * 解决前端登录跨域的问题
         */
        ServerHttpRequest request = exchange.getRequest();
        if (CorsUtils.isCorsRequest(request)) {
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = response.getHeaders();
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, StringPool.ASTERISK);
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, StringPool.ASTERISK);
            headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, ACCESS_CONTROL_MAX_AGE);
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, StringPool.ASTERISK);
            if (request.getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return Mono.empty();
            }
        }
        return chain.filter(exchange);
    }
}
