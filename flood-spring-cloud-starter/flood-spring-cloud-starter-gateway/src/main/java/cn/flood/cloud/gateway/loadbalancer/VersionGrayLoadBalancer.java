package cn.flood.cloud.gateway.loadbalancer;

import brave.Span;
import brave.Tracing;
import cn.flood.base.core.Func;
import cn.flood.base.core.constants.HeaderConstant;
import cn.flood.cloud.gateway.loadbalancer.props.GrayLoadBalancerProperties;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.PatternMatchUtils;

/**
 * 基于客户端版本号灰度路由
 *
 * @author L.cm
 * @author madi
 * @date 2021-02-24 13:41
 */
public class VersionGrayLoadBalancer implements GrayLoadBalancer {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private final GrayLoadBalancerProperties loadBalancerProperties;
  //每次请求算上重试不会超过1分钟
  //对于超过1分钟的，这种请求肯定比较重，不应该重试
  private final LoadingCache<Long, AtomicInteger> positionCache = Caffeine.newBuilder()
      .expireAfterWrite(1, TimeUnit.MINUTES)
      //随机初始值，防止每次都是从第一个开始调用
      .build(k -> new AtomicInteger(ThreadLocalRandom.current().nextInt(0, 1000)));
  private DiscoveryClient discoveryClient;
  private Tracing tracing = Tracing.newBuilder().build();

  public VersionGrayLoadBalancer(DiscoveryClient discoveryClient,
      GrayLoadBalancerProperties loadBalancerProperties) {
    this.discoveryClient = discoveryClient;
    this.loadBalancerProperties = loadBalancerProperties;
  }

  /**
   * 根据serviceId 筛选可用服务
   *
   * @param serviceId 服务ID
   * @param request   当前请求
   * @return
   */
  @Override
  public ServiceInstance choose(String serviceId, ServerHttpRequest request) {
    List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceId);

    //注册中心无实例 抛出异常
    if (Func.isEmpty(serviceInstances)) {
      log.warn("No instance available for {}", serviceId);
      throw new NotFoundException(String.format("No instance available for %s", serviceId));
    }

    List<ServiceInstance> serviceInstancesList = serviceInstances;
    //开启灰度ip
    if (loadBalancerProperties.isEnabled()) {
      // 指定ip则返回满足ip的服务
      List<String> priorIpPattern = loadBalancerProperties.getPriorIpPattern();
      if (!priorIpPattern.isEmpty()) {
        String[] priorIpPatterns = priorIpPattern.toArray(new String[0]);
        List<ServiceInstance> priorIpInstances = serviceInstances.stream().filter(
            (i -> PatternMatchUtils.simpleMatch(priorIpPatterns, i.getHost()))
        ).collect(Collectors.toList());
        if (!priorIpInstances.isEmpty()) {
          serviceInstancesList = priorIpInstances;
        }
      }
    }

    // 获取请求version，无则随机返回可用实例
    List<String> headers = request.getHeaders().get(HeaderConstant.HEADER_VERSION);
    // 获取请求header灰度版本号
    if (Func.isNotEmpty(headers)) {
      String reqVersion = headers.get(0);
      //serviceInstances Flux在subscribe时才会执行map代码，所以我们只能将容错代码写在map内
      List<ServiceInstance> versionInstancesList = serviceInstancesList.stream().filter(instance ->
          reqVersion.equals(instance.getMetadata().get("version")))
          .collect(Collectors.toList());
      //容错，如header无匹配的则返回正常列表轮询
      if (!Func.isEmpty(versionInstancesList)) {
        serviceInstancesList = versionInstancesList;
      }
    }
    //为了解决原始算法不同调用并发可能导致一个请求重试相同的实例
    Span currentSpan = tracing.tracer().newTrace();
    long l = currentSpan.context().traceId();
    AtomicInteger seed = positionCache.get(l);
    int s = seed.getAndIncrement();
    int c = serviceInstancesList.size();
    int pos = s % c;
//		log.debug("position {}, seed: {}, instances count: {}", pos, s, c);
    ServiceInstance instance = serviceInstancesList.stream()
        //实例返回列表顺序可能不同，为了保持一致，先排序再取
        .sorted(Comparator.comparing(ServiceInstance::getUri))
        .collect(Collectors.toList()).get(pos);
    log.info("serviceId: {} , instances: {}", serviceId, instance.getUri());
    return instance;
  }
}
