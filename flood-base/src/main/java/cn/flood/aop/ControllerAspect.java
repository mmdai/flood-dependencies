package cn.flood.aop;

import java.time.Clock;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 
* <p>Title: ControllerAspect</p>  
* <p>Description: 主要是对访问控制进行转发，各类基本参数校验，或者不复用的业务简单处理等</p>  
* @author mmdai  
* @date 2018年10月11日
 */

@Aspect
@Component
@Order(-9)
public class ControllerAspect implements LogAspect {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Pointcut("execution(* cn..*.*.controller..*.*(..))")
	public void log() {
		
	}
	
	@Around("log()") 
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		 long start = Clock.systemDefaultZone().millis();
		 ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		 HttpServletRequest request = attributes.getRequest();
		 String uri = request.getRequestURI();
		 String methodName = joinPoint.getSignature().getName();
		 logger.info("【controller】【{}】【{}】 start", uri, methodName);
		 logger.debug("【controller】【{}】【{}】【{}】", uri, methodName, before(joinPoint));

		 Object result=joinPoint.proceed();

		 logger.debug("【controller】【{}】【{}】【{}】", uri, methodName, after(result));
		 logger.info("【controller】【{}】【{}】 end, cost【{}ms】", uri, methodName, (Clock.systemDefaultZone().millis()-start));
		 return result;
	}


	
}

