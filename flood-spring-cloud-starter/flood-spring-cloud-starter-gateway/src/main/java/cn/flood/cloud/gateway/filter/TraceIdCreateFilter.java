package cn.flood.cloud.gateway.filter;

import cn.flood.base.core.trace.MDCTraceUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 给请求 create TraceId
 *
 * @author mmdai
 * @since 2020-7-13
 */
@Component
@RequiredArgsConstructor
public class TraceIdCreateFilter implements GlobalFilter, Ordered {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 开启traceId追踪ID生成
        String traceId = MDCTraceUtils.createTraceId();
        ServerHttpRequest newRequest = exchange.getRequest().mutate().header(MDCTraceUtils.TRACE_ID_HEADER, traceId).build();
        ServerWebExchange build = exchange.mutate().request(newRequest).build();
        MDCTraceUtils.putTraceId(traceId);
        return chain.filter(build);
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
