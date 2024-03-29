package cn.flood.cloud.grpc.loadbalancer;

import brave.Span;
import brave.Tracing;
import cn.flood.base.core.Func;
import cn.flood.base.core.constants.HeaderConstant;
import cn.flood.cloud.grpc.loadbalancer.props.GrayLoadBalancerProperties;
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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.util.PatternMatchUtils;
import reactor.core.publisher.Mono;


/**
 * 一定必须是实现ReactorServiceInstanceLoadBalancer 而不是ReactorLoadBalancer<ServiceInstance>
 * 因为注册的时候是ReactorServiceInstanceLoadBalancer
 *
 * @author mmdai
 */
public class RoundRobinWithRequestSeparatedPositionLoadBalancer implements
    ReactorServiceInstanceLoadBalancer {

  private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplier;
  /**
   * 每次请求算上重试不会超过1分钟 对于超过1分钟的，这种请求肯定比较重，不应该重试
   */
  private final LoadingCache<Long, AtomicInteger> positionCache = Caffeine.newBuilder()
      .expireAfterWrite(1, TimeUnit.MINUTES)
      //随机初始值，防止每次都是从第一个开始调用
      .build(k -> new AtomicInteger(ThreadLocalRandom.current().nextInt(0, 1000)));
  private final String serviceId;
  private final Tracing tracing;
  private final GrayLoadBalancerProperties loadBalancerProperties;
  private Logger log = LoggerFactory.getLogger(this.getClass());


  public RoundRobinWithRequestSeparatedPositionLoadBalancer(
      ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplier, String serviceId,
      Tracing tracing,
      GrayLoadBalancerProperties loadBalancerProperties) {
    this.serviceInstanceListSupplier = serviceInstanceListSupplier;
    this.serviceId = serviceId;
    this.tracing = tracing;
    this.loadBalancerProperties = loadBalancerProperties;
  }

  @Override
  public Mono<Response<ServiceInstance>> choose(Request request) {
    ServiceInstanceListSupplier supplier = serviceInstanceListSupplier
        .getIfAvailable(NoopServiceInstanceListSupplier::new);
    final String requestVersion = ((RequestDataContext) request.getContext()).getClientRequest()
        .getHeaders().getFirst(HeaderConstant.HEADER_VERSION);
    return supplier.get(request).next()
        .map(serviceInstances -> getInstanceResponse(serviceInstances, requestVersion));
  }

  private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> serviceInstances,
      String requestVersion) {
    if (serviceInstances.isEmpty()) {
      log.warn("No servers available for service: {}", this.serviceId);
      return new EmptyResponse();
    }
    return getInstanceResponseByRoundRobin(serviceInstances, requestVersion);
  }

  private Response<ServiceInstance> getInstanceResponseByRoundRobin(
      List<ServiceInstance> serviceInstances, String requestVersion) {
    if (serviceInstances.isEmpty()) {
      log.warn("No servers available for service: {}", this.serviceId);
      return new EmptyResponse();
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

    // 获取请求header灰度版本号
    if (Func.isNotEmpty(requestVersion)) {
      //serviceInstances Flux在subscribe时才会执行map代码，所以我们只能将容错代码写在map内
      List<ServiceInstance> versionInstancesList = serviceInstancesList.stream().filter(instance ->
          requestVersion.equalsIgnoreCase(instance.getMetadata().get("version")))
          .collect(Collectors.toList());
      //容错，如header无匹配的则返回正常列表轮询
      if (!Func.isEmpty(versionInstancesList)) {
        serviceInstancesList = versionInstancesList;
      }
    }

    //为了解决原始算法不同调用并发可能导致一个请求重试相同的实例
    Span currentSpan = tracing.tracer().currentSpan();
    if (currentSpan == null) {
      currentSpan = tracing.tracer().newTrace();
    }
    long l = currentSpan.context().traceId();
    AtomicInteger seed = positionCache.get(l);
    int s = seed.getAndIncrement();
    int c = serviceInstancesList.size();
    int pos = s % c;
    log.info("position {}, seed: {}, instances count: {}", pos, s, c);
    return new DefaultResponse(serviceInstancesList.stream()
        //实例返回列表顺序可能不同，为了保持一致，先排序再取
        .sorted(Comparator.comparing(ServiceInstance::getUri))
        .collect(Collectors.toList()).get(pos));
  }
}
