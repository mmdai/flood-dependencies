package cn.flood.aop;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
* <p>Title: ManagerLogAspect</p>  
* <p>Description: 通用业务处理层，它有如下特征：
*  1） 对第三方平台封装的层，预处理返回结果及转化异常信息；
*  2） 对Service层通用能力的下沉，如缓存方案、中间件通用处理； 
*  3） 与DAO层交互，对DAO的业务通用能力的封装</p>   
* @author mmdai  
* @date 2018年11月2日
 */
//@Aspect
//@Component
//@Order(-9)
public class ManagerAspect implements LogAspect {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Pointcut(
			"@within(org.springframework.stereotype.Component))"
	)
	public void log() {
		
	}
	
	@Around("log()") 
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		Stopwatch stopwatch = Stopwatch.createStarted();
		String methodName = joinPoint.getSignature().getName();
		logger.info("【manager】【{}】 start", methodName);
//		if (logger.isDebugEnabled()) {
//			logger.debug("【manager】【{}】【{}】", methodName, before(joinPoint));
//		}

		Object result = joinPoint.proceed();
//		if (logger.isDebugEnabled()) {
//			logger.debug("【manager】【{}】【{}】", methodName, after(result));
//		}
		logger.info("【manager】【{}】 end,cost【{}ms】", methodName, stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
		return result;
	}

}
