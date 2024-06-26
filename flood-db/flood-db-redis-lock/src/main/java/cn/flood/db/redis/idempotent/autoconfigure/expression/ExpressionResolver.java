package cn.flood.db.redis.idempotent.autoconfigure.expression;

/**
 * @author lengleng
 * @date 2020/9/25
 */

import cn.flood.db.redis.idempotent.autoconfigure.annotation.Idempotent;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @author daimm
 * <p>
 * 默认key 抽取， 优先根据 spel 处理
 * @date 2020-09-25
 */
public class ExpressionResolver implements KeyResolver {

  private static final SpelExpressionParser PARSER = new SpelExpressionParser();

  private static final StandardReflectionParameterNameDiscoverer DISCOVERER = new StandardReflectionParameterNameDiscoverer();

  @Override
  public String resolver(Idempotent idempotent, JoinPoint point) {
    List<String> keyList = new ArrayList<>();
    Method method = getMethod(point);
    List<String> definitionKeys = getSpelDefinitionKey(idempotent.keys(), method, point.getArgs());
    if (!CollectionUtils.isEmpty(definitionKeys)) {
      keyList.addAll(definitionKeys);
    } else {
      List<String> parameterKeys = getParameterKey(method.getParameters(), point.getArgs());
      keyList.addAll(parameterKeys);
    }
    return StringUtils.collectionToDelimitedString(keyList, "", "-", "");
  }

  /**
   * 根据切点解析方法信息
   *
   * @param joinPoint 切点信息
   * @return Method 原信息
   */
  private Method getMethod(JoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    if (method.getDeclaringClass().isInterface()) {
      try {
        method = joinPoint.getTarget().getClass()
            .getDeclaredMethod(joinPoint.getSignature().getName(),
                method.getParameterTypes());
      } catch (SecurityException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }
    return method;
  }

  private List<String> getSpelDefinitionKey(String[] definitionKeys, Method method,
      Object[] parameterValues) {
    List<String> definitionKeyList = new ArrayList<>();
    for (String definitionKey : definitionKeys) {
      if (!ObjectUtils.isEmpty(definitionKey)) {
        EvaluationContext context = new MethodBasedEvaluationContext(null, method, parameterValues,
            DISCOVERER);
        Object objKey = PARSER.parseExpression(definitionKey).getValue(context);
        definitionKeyList.add(ObjectUtils.nullSafeToString(objKey));
      }
    }
    return definitionKeyList;
  }

  private List<String> getParameterKey(Parameter[] parameters, Object[] parameterValues) {
    List<String> parameterKey = new ArrayList<>();
    for (int i = 0; i < parameters.length; i++) {
      Object parameterValue = parameterValues[i];
      parameterKey.add(ObjectUtils.nullSafeToString(parameterValue));
    }
    return parameterKey;
  }
}