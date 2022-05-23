package cn.flood.cloud.gateway.filter;

import cn.flood.constants.HeaderConstant;
import cn.flood.trace.MDCTraceUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

/**
 * 给请求增加IP地址和TraceId
 *
 * @author mmdai
 * @since 2020-7-13
 */
@Component
@RequiredArgsConstructor
public class PreRequestFilter implements GlobalFilter, Ordered {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 开启traceId追踪ID生成
        String traceId = MDCTraceUtils.createTraceId();
        exchange.getAttributes().put(HeaderConstant.REQUEST_ID, traceId);
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
