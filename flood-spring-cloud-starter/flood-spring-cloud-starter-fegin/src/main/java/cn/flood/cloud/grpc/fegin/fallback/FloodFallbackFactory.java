package cn.flood.cloud.grpc.fegin.fallback;

import feign.Target;
import lombok.AllArgsConstructor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 默认 Fallback，避免写过多fallback类
 *
 * @param <T> 泛型标记
 * @author mmdai
 */
@AllArgsConstructor
public class FloodFallbackFactory<T> implements FallbackFactory<T> {

  private final Target<T> target;

  @Override
  @SuppressWarnings("unchecked")
  public T create(Throwable cause) {
    final Class<T> targetType = target.type();
    final String targetName = target.name();
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(targetType);
    enhancer.setUseCache(true);
    enhancer.setCallback(new FloodFeignFallback<>(targetType, targetName, cause));
    return (T) enhancer.create();
  }
}
