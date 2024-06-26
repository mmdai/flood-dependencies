package cn.flood.cloud.gateway.service.impl;

import cn.flood.base.core.Func;
import cn.flood.base.core.date.DateTimeUtils;
import cn.flood.base.core.http.IPUtils;
import cn.flood.cloud.gateway.service.SafeRuleService;
import cn.flood.cloud.rule.constant.RuleConstant;
import cn.flood.cloud.rule.entity.BlackList;
import cn.flood.cloud.rule.service.RuleCacheService;
import com.google.common.base.Stopwatch;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 安全规则业务实现类
 *
 * @author mmdai
 */
@Service
@RequiredArgsConstructor
public class SafeRuleServiceImpl implements SafeRuleService {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  @Autowired
  private RuleCacheService ruleCacheService;

  @Override
  public Mono<Void> filterBlackList(ServerWebExchange exchange) {
    Stopwatch stopwatch = Stopwatch.createStarted();
    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();
    try {
      URI originUri = getOriginRequestUri(exchange);
      String requestIp = IPUtils.getServerHttpRequestIpAddress(request);
      String requestMethod = request.getMethod().name();
      AtomicBoolean forbid = new AtomicBoolean(false);
      // 从缓存中获取黑名单信息
      Set<Object> blackLists = ruleCacheService.getBlackList(requestIp);
      blackLists.addAll(ruleCacheService.getBlackList());
      // 检查是否在黑名单中
      checkBlackLists(forbid, blackLists, originUri, requestMethod);
      log.debug("黑名单检查完成 - {}", stopwatch.stop());
      if (forbid.get()) {
        log.info("属于黑名单地址 - {}", originUri.getPath());
        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        String result = "{\"code\":\"S00000\",\"msg\":\"Not Acceptable 已列入黑名单，访问受限\"}";
        DataBuffer dataBuffer = response.bufferFactory().wrap((result).getBytes());
        return response.writeWith(Mono.just(dataBuffer));

      }
    } catch (Exception e) {
      log.error("黑名单检查异常: {} - {}", e.getMessage(), stopwatch.stop());
    }
    return null;
  }

  /**
   * 获取网关请求URI
   *
   * @param exchange ServerWebExchange
   * @return URI
   */
  private URI getOriginRequestUri(ServerWebExchange exchange) {
    return exchange.getRequest().getURI();
  }

  /**
   * 检查是否满足黑名单的条件
   *
   * @param forbid        是否黑名单判断
   * @param blackLists    黑名列表
   * @param uri           资源
   * @param requestMethod 请求方法
   */
  private void checkBlackLists(AtomicBoolean forbid, Set<Object> blackLists, URI uri,
      String requestMethod) {
    for (Object bl : blackLists) {
      BlackList blackList = Func.parse(bl.toString(), BlackList.class);
      if (antPathMatcher.match(blackList.getRequestUri(), uri.getPath())
          && RuleConstant.BLACKLIST_OPEN.equals(blackList.getStatus())) {
        if (RuleConstant.ALL.equalsIgnoreCase(blackList.getRequestMethod())
            || StringUtils.equalsIgnoreCase(requestMethod, blackList.getRequestMethod())) {
          if (Func.isNotBlank(blackList.getStartTime()) && Func
              .isNotBlank(blackList.getEndTime())) {
            if (DateTimeUtils.between(DateTimeUtils
                    .parseLocalTime(blackList.getStartTime(), DateTimeUtils.YYYY_MM_DD_HH_MM_SS),
                DateTimeUtils
                    .parseLocalTime(blackList.getEndTime(), DateTimeUtils.YYYY_MM_DD_HH_MM_SS))) {
              forbid.set(Boolean.TRUE);
            }
          } else {
            forbid.set(Boolean.TRUE);
          }
        }
      }
      if (forbid.get()) {
        break;
      }
    }
  }
}
