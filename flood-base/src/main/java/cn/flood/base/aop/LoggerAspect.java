package cn.flood.base.aop;

import com.google.common.base.Stopwatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * * <p>Title: LoggerAspect</p>
 * * <p>Description: 相对具体的业务逻辑服务层</p>
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggerAspect implements LogAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut(
            "@within(cn.flood.base.aop.annotation.Logger))"
    )
    public void log() {

    }

    @Around("log()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String methodName = joinPoint.getSignature().getName();
        logger.info("【service】【{}】 start", methodName);

        Object result = joinPoint.proceed();
        logger.info("【service】【{}】 end,cost【{}ms】", methodName, stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
        return result;
    }
}
