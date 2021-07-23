package cn.flood.cloud.gateway.filter;

import cn.flood.constants.HeaderConstants;
import cn.flood.trace.MDCTraceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 给请求增加IP地址和TraceId
 *
 * @author mmdai
 * @since 2020-7-13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PreRequestFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 开启traceId追踪ID生成
        String traceId = MDCTraceUtils.createTraceId();

        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
                .headers(h -> h.add(MDCTraceUtils.TRACE_ID_HEADER, traceId))
                .build();
        exchange.getAttributes().put(HeaderConstants.REQUEST_ID, traceId);
        ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
        return chain.filter(build);

    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
