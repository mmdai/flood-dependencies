package cn.flood.aop;

import java.time.Clock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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


	@Pointcut("execution(* cn..*.*.mapper..*.*(..))")
	public void log() {
		
	}
	
	@Around("log()") 
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = Clock.systemDefaultZone().millis();
		/*ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		String uri=request.getRequestURI();*/
		String methodName = joinPoint.getSignature().getName();
		logger.info("【dao】【{}】 start", methodName);
		logger.debug("【dao】【{}】【{}】",  methodName, before(joinPoint));

		Object result = joinPoint.proceed();

		logger.debug("【dao】【{}】【{}】", methodName, after(result));
		long end = Clock.systemDefaultZone().millis();
		logger.info("【dao】【{}】 end,cost【{}ms】", methodName, (Clock.systemDefaultZone().millis()-start));
		return result;
	}
}

