package cn.flood.cloud.gateway.filter;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

import cn.flood.base.core.trace.MDCTraceUtils;
import cn.flood.cloud.gateway.props.WebSocketProperties;
import com.google.common.base.Stopwatch;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;

/**
 * 给请求 create TraceId
 *
 * @author mmdai
 * @since 2020-7-13
 */
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties({WebSocketProperties.class})
public class TraceIdLogFilter implements GlobalFilter, Ordered {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    Stopwatch stopwatch = Stopwatch.createStarted();
    // 开启traceId追踪ID生成
    String traceId = MDCTraceUtils.createTraceId();
    ServerHttpRequest newRequest = exchange.getRequest().mutate().headers(httpHeaders -> {
      httpHeaders.add(MDCTraceUtils.TRACE_ID_HEADER, traceId);
    }).build();
    exchange.mutate().request(newRequest);
    MDCTraceUtils.putTraceId(traceId);
    String requestUrl = exchange.getRequest().getURI().getRawPath();
    // 构建成一条长 日志，避免并发下日志错乱
    StringBuilder beforeReqLog = new StringBuilder(300);
    // 日志参数
    List<Object> beforeReqArgs = new ArrayList<>();
    beforeReqLog.append("===> {}: {}");
    // 参数
    String requestMethod = exchange.getRequest().getMethodValue();
    beforeReqArgs.add(requestMethod);
    beforeReqArgs.add(requestUrl);

    // 打印执行时间
    log.info(beforeReqLog.toString(), beforeReqArgs.toArray());

    return chain.filter(exchange).doOnEach(logOnEach(r -> {
      // 构建成一条长 日志，避免并发下日志错乱
      StringBuilder responseLog = new StringBuilder(300);
      // 日志参数
      List<Object> responseArgs = new ArrayList<>();
      responseLog.append("<=== {} {}: {}: {}");
      // 参数
      responseArgs.add(exchange.getResponse().getStatusCode().value());
      responseArgs.add(requestMethod);
      responseArgs.add(requestUrl);
      responseArgs.add(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + "ms");
      // 打印执行时间
      log.info(responseLog.toString(), responseArgs.toArray());
    })).contextWrite(Context.of(MDCTraceUtils.CONTEXT_KEY, traceId));
  }


  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }

  public static <T> Consumer<Signal<T>> logOnEach(Consumer<T> logStatement) {
    return signal -> {
      String contextValue = signal.getContextView().get(MDCTraceUtils.CONTEXT_KEY);
      try (MDC.MDCCloseable cMdc = MDC.putCloseable(MDCTraceUtils.TRACE_ID_HEADER, contextValue)) {
        logStatement.accept(signal.get());
      }
    };
  }
}
