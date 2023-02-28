package cn.flood.base.aop;

import cn.flood.base.core.lang.StringPool;
import cn.flood.base.core.lang.StringUtils;
import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * * <p>Title: LoggerAspect</p> * <p>Description: 相对具体的业务响应时间</p>
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggerAspect implements LogAspect {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  @Pointcut(
      "@within(org.springframework.web.bind.annotation.RestController) ||" +
          " @within(cn.flood.base.aop.annotation.Logger)"
  )
  public void log() {

  }

  @Around("log()")
  public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
    Stopwatch stopwatch = Stopwatch.createStarted();
    String className = StringUtils
        .subAfter(joinPoint.getSignature().getDeclaringTypeName(), StringPool.DOT, true);
    String methodName = joinPoint.getSignature().getName();
//		if (logger.isDebugEnabled()) {
//			logger.debug("【{}】【{}】【{}】【{}】", className, methodName, before(joinPoint));
//		}
    logger.info("【{}】【{}】 start", className, methodName);

    Object result = joinPoint.proceed();
//		if (logger.isDebugEnabled()) {
//		 	logger.debug("【{}】【{}】【{}】【{}】", className, methodName, after(result));
//		}
    logger.info("【{}】【{}】 end,cost【{}ms】", className, methodName,
        stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
    return result;
  }
}
