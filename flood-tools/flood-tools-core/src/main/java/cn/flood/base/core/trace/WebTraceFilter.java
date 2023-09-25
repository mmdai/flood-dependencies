package cn.flood.base.core.trace;

import cn.flood.base.core.Func;
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * web过滤器，生成日志链路追踪id，并赋值MDC
 */
@ConditionalOnClass(value = {HttpServletRequest.class, OncePerRequestFilter.class})
@Order(value = MDCTraceUtils.FILTER_ORDER)
public class WebTraceFilter extends OncePerRequestFilter {

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return false;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {
    try {
      String traceId = request.getHeader(MDCTraceUtils.TRACE_ID_HEADER);
      if (Func.isEmpty(traceId)) {
        MDCTraceUtils.addTraceId();
      } else {
        MDCTraceUtils.putTraceId(traceId);
      }
      filterChain.doFilter(request, response);
    } finally {
      MDCTraceUtils.removeTraceId();
    }
  }

}
