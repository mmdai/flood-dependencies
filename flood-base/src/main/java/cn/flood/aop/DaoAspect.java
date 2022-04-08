package cn.flood.aop;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
* <p>Title: DaoAspect</p>  
* <p>Description: 数据访问层，与底层MySQL、Oracle、Hbase进行数据交互。</p>  
* @author mmdai  
* @date 2018年10月11日
 */

//@Aspect
//@Component
//@Order(-9)
public class DaoAspect implements LogAspect {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());


//	@Pointcut("execution(* cn..*.*.mapper..*.*(..))")
	public void log() {
		
	}
	
	@Around("log()") 
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		Stopwatch stopwatch = Stopwatch.createStarted();
		String methodName = joinPoint.getSignature().getName();
		logger.info("【dao】【{}】 start", methodName);
		if (logger.isDebugEnabled()) {
			logger.debug("【dao】【{}】【{}】", methodName, before(joinPoint));
		}
		Object result = joinPoint.proceed();
		if (logger.isDebugEnabled()) {
			logger.debug("【dao】【{}】【{}】", methodName, after(result));
		}
		logger.info("【dao】【{}】 end,cost【{}ms】", methodName, stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
		return result;
	}
}

