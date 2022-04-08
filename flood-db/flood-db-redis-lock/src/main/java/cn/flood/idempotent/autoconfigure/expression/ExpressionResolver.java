package cn.flood.idempotent.autoconfigure.expression;

/**
 * @author lengleng
 * @date 2020/9/25
 */

import cn.flood.idempotent.autoconfigure.annotation.Idempotent;
import cn.flood.lock.autoconfigure.annotation.RlockKey;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author daimm
 * <p>
 * 默认key 抽取， 优先根据 spel 处理
 * @date 2020-09-25
 */
public class ExpressionResolver implements KeyResolver {

	private static final SpelExpressionParser PARSER = new SpelExpressionParser();

	private static final LocalVariableTableParameterNameDiscoverer DISCOVERER = new LocalVariableTableParameterNameDiscoverer();

	@Override
	public String resolver(Idempotent idempotent, JoinPoint point) {
//		Object[] arguments = point.getArgs();
//		String[] params = DISCOVERER.getParameterNames(getMethod(point));
//		StandardEvaluationContext context = new StandardEvaluationContext();
//
//		for (int len = 0; len < params.length; len++) {
//			context.setVariable(params[len], arguments[len]);
//		}
//
//		Expression expression = PARSER.parseExpression(idempotent.key());
//		return expression.getValue(context, String.class);
		List<String> keyList = new ArrayList<>();
		Method method = getMethod(point);
		List<String> definitionKeys = getSpelDefinitionKey(idempotent.keys(), method, point.getArgs());
		keyList.addAll(definitionKeys);
		List<String> parameterKeys = getParameterKey(method.getParameters(), point.getArgs());
		keyList.addAll(parameterKeys);
		return StringUtils.collectionToDelimitedString(keyList,"","-","");
	}

	/**
	 * 根据切点解析方法信息
	 * @param joinPoint 切点信息
	 * @return Method 原信息
	 */
	private Method getMethod(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		if (method.getDeclaringClass().isInterface()) {
			try {
				method = joinPoint.getTarget().getClass().getDeclaredMethod(joinPoint.getSignature().getName(),
						method.getParameterTypes());
			}
			catch (SecurityException | NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		return method;
	}

	private List<String> getSpelDefinitionKey(String[] definitionKeys, Method method, Object[] parameterValues) {
		List<String> definitionKeyList = new ArrayList<>();
		for (String definitionKey : definitionKeys) {
			if (!ObjectUtils.isEmpty(definitionKey)) {
				EvaluationContext context = new MethodBasedEvaluationContext(null, method, parameterValues, DISCOVERER);
				Object objKey = PARSER.parseExpression(definitionKey).getValue(context);
				definitionKeyList.add(ObjectUtils.nullSafeToString(objKey));
			}
		}
		return definitionKeyList;
	}

	private List<String> getParameterKey(Parameter[] parameters, Object[] parameterValues) {
		List<String> parameterKey = new ArrayList<>();
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i].getAnnotation(RlockKey.class) != null) {
				RlockKey keyAnnotation = parameters[i].getAnnotation(RlockKey.class);
				if (keyAnnotation.value().isEmpty()) {
					Object parameterValue = parameterValues[i];
					parameterKey.add(ObjectUtils.nullSafeToString(parameterValue));
				} else {
					StandardEvaluationContext context = new StandardEvaluationContext(parameterValues[i]);
					Object key = PARSER.parseExpression(keyAnnotation.value()).getValue(context);
					parameterKey.add(ObjectUtils.nullSafeToString(key));
				}
			}
		}
		return parameterKey;
	}
}