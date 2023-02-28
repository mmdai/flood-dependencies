package cn.flood.cloud.gateway.filter;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

import cn.flood.base.core.constants.HeaderConstant;
import cn.flood.base.core.trace.MDCTraceUtils;
import cn.flood.cloud.gateway.props.WebSocketProperties;
import com.google.common.base.Stopwatch;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 打印请求和响应简要日志
 *
 * @author mmdai
 * @since 2020-7-16
 */
@Component
@AllArgsConstructor
@EnableConfigurationProperties({WebSocketProperties.class})
public class RequestLogFilter implements GlobalFilter, Ordered {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    Stopwatch stopwatch = Stopwatch.createStarted();
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

    return chain.filter(exchange).then(Mono.fromRunnable(() -> {
      ServerHttpResponse response = exchange.getResponse();

      // 构建成一条长 日志，避免并发下日志错乱
      StringBuilder responseLog = new StringBuilder(300);
      // 日志参数
      List<Object> responseArgs = new ArrayList<>();
      responseLog.append("<=== {} {}: {}: {}");
      // 参数
      responseArgs.add(response.getStatusCode().value());
      responseArgs.add(requestMethod);
      responseArgs.add(requestUrl);
      responseArgs.add(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + "ms");
      // 打印请求头
      HttpHeaders httpHeaders = response.getHeaders();
      //如果是websocket，不需要加入traceId
      URI requestUri = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
      String scheme = requestUri.getScheme();

      if (!"ws".equals(scheme) && !"wss".equals(scheme)) {
        String traceId = exchange.getAttribute(HeaderConstant.REQUEST_ID);
        httpHeaders.add(MDCTraceUtils.TRACE_ID_HEADER, traceId);
      }
      // 打印执行时间
      log.info(responseLog.toString(), responseArgs.toArray());
      MDCTraceUtils.removeTraceId();
    }));
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE - 1;
  }
}
