package cn.flood.db.redis.idempotent.autoconfigure.aspect;

import cn.flood.db.redis.idempotent.autoconfigure.annotation.Idempotent;
import cn.flood.db.redis.idempotent.autoconfigure.exception.IdempotentException;
import cn.flood.db.redis.idempotent.autoconfigure.expression.KeyResolver;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.Redisson;
import org.redisson.api.RMapCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * The Idempotent Aspect
 *
 * @author daimm
 */
@Aspect
@SuppressWarnings("unchecked")
public class IdempotentAspect {

  private static final String RMAPCACHE_KEY = "idempotent";
  private static final String KEY = "key";
  private static final String DELKEY = "delKey";
  private static final String ERROR_CODE = "S10000";
  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal();
  @Autowired
  private Redisson redisson;

  @Autowired
  private KeyResolver keyResolver;

  @Pointcut("@annotation(cn.flood.db.redis.idempotent.autoconfigure.annotation.Idempotent)")
  public void pointCut() {
  }

  @Before("pointCut()")
  public void beforePointCut(JoinPoint joinPoint) throws Exception {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
        .getRequestAttributes();
    HttpServletRequest request = requestAttributes.getRequest();

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    if (!method.isAnnotationPresent(Idempotent.class)) {
      return;
    }
    Idempotent idempotent = method.getAnnotation(Idempotent.class);

    String key;

    // 若没有配置 幂等 标识编号，则使用 url + 参数列表作为区分
    if (ObjectUtils.isEmpty(idempotent.keys())) {
      String url = request.getRequestURL().toString();
      String argString = Arrays.asList(joinPoint.getArgs()).toString();
      key = url + argString;
    } else {
      // 使用jstl 规则区分
      key = method.getName() + keyResolver.resolver(idempotent, joinPoint);
    }

    long expireTime = idempotent.expireTime();
    String info = idempotent.info();
    TimeUnit timeUnit = idempotent.timeUnit();
    boolean delKey = idempotent.delKey();

    // do not need check null
    RMapCache<String, Object> rMapCache = redisson.getMapCache(RMAPCACHE_KEY);
    String value = LocalDateTime.now().toString().replace("T", " ");
    Object v1;
    if (null != rMapCache.get(key)) {
      // had stored
      throw new IdempotentException(ERROR_CODE, "[idempotent]:" + info);
    }
    synchronized (this) {
      v1 = rMapCache.putIfAbsent(key, value, expireTime, TimeUnit.SECONDS);
      if (null != v1) {
        throw new IdempotentException(ERROR_CODE, "[idempotent]:" + info);
      } else {
        log.info("[idempotent]:has stored key={},value={},expireTime={}{},now={}", key, value,
            expireTime,
            timeUnit, LocalDateTime.now().toString());
      }
    }

    Map<String, Object> map =
        CollectionUtils.isEmpty(threadLocal.get()) ? new HashMap<>(4) : threadLocal.get();
    map.put(KEY, key);
    map.put(DELKEY, delKey);
    threadLocal.set(map);

  }

  @After("pointCut()")
  public void afterPointCut(JoinPoint joinPoint) {
    Map<String, Object> map = threadLocal.get();
    if (CollectionUtils.isEmpty(map)) {
      return;
    }

    RMapCache<Object, Object> mapCache = redisson.getMapCache(RMAPCACHE_KEY);
    if (mapCache.size() == 0) {
      return;
    }

    String key = map.get(KEY).toString();
    boolean delKey = (boolean) map.get(DELKEY);

    if (delKey) {
      mapCache.fastRemove(key);
      log.info("[idempotent]:has removed key={}", key);
    }
    threadLocal.remove();
  }

}
