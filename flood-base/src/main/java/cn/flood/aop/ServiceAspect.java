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
* <p>Title: ServiceAspect</p>  
* <p>Description: 相对具体的业务逻辑服务层</p>  
* @author mmdai  
* @date 2018年10月11日
 */
@Aspect
@Component
@Order(-9)
public class ServiceAspect implements LogAspect {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

//	@Pointcut("execution(* cn..*.*.service..*.*(..))")
	@Pointcut(
			"@within(org.springframework.stereotype.Service))"
	)
	public void log() {
		
	}
	
	@Around("log()") 
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = Clock.systemDefaultZone().millis();
//		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//		HttpServletRequest request = attributes.getRequest();
//		String uri=request.getRequestURI();
		 
		String methodName = joinPoint.getSignature().getName();
		logger.info("【service】【{}】 start", methodName);
		logger.debug("【service】【{}】【{}】",  methodName, before(joinPoint));

		Object result = joinPoint.proceed();

		logger.debug("【service】【{}】【{}】", methodName, after(result));
		logger.info("【service】【{}】 end,cost【{}ms】", methodName, (Clock.systemDefaultZone().millis()-start));
		return result;
	}
}
