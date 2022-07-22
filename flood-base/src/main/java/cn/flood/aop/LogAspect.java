package cn.flood.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public interface LogAspect {

    /**
     *
     * @param joinPoint
     * @return
     */
    default String before(ProceedingJoinPoint joinPoint){
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 参数名
        String[] paramNames = methodSignature.getParameterNames();
        Map<String, Object> params = null;
        if (paramNames != null && paramNames.length > 0) {
            // 参数值
            Object[] args = joinPoint.getArgs();
            params = new HashMap<>();
            for (int i = 0; i < paramNames.length; i++) {
                if(paramNames[i] instanceof Serializable){
                    params.put(paramNames[i], args[i]);
                }
            }
        }
        return params==null ? "" : params.toString();
    }

    /**
     *
     * @param result
     * @return
     */
    default String after (Object result) {
        return result==null ? "" : result.toString();
    }


}
