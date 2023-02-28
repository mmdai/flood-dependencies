package cn.flood.cloud.log.aspect;

import cn.flood.base.core.Func;
import cn.flood.base.core.UserToken;
import cn.flood.base.core.http.CustomSystemUtils;
import cn.flood.base.core.http.IPUtils;
import cn.flood.base.core.http.WebUtil;
import cn.flood.base.core.json.JsonUtils;
import cn.flood.cloud.log.annotation.AuditLog;
import cn.flood.cloud.log.model.Audit;
import cn.flood.cloud.log.properties.AuditLogProperties;
import cn.flood.cloud.log.services.IAuditService;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 审计日志切面
 */
@Aspect
@ConditionalOnClass({HttpServletRequest.class, RequestContextHolder.class})
public class AuditLogAspect {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Value("${spring.application.name}")
  private String applicationName;

  private AuditLogProperties auditLogProperties;
  private IAuditService auditService;
  /**
   * 用于SpEL表达式解析.
   */
  private SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
  /**
   * 用于获取方法参数定义名字.
   */
  private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
  public AuditLogAspect(AuditLogProperties auditLogProperties, IAuditService auditService) {
    this.auditLogProperties = auditLogProperties;
    this.auditService = auditService;
    log.info("================ AuditLogAspect ======================");
  }

  @Before("@within(auditLog) || @annotation(auditLog)")
  public void beforeMethod(JoinPoint joinPoint, AuditLog auditLog) {
    //判断功能是否开启
    if (auditLogProperties.getEnabled()) {
      if (auditService == null) {
        log.warn("AuditLogAspect - auditService is null");
        return;
      }
      if (auditLog == null) {
        // 获取类上的注解
        auditLog = joinPoint.getTarget().getClass().getDeclaredAnnotation(AuditLog.class);
      }
      Audit audit = getAudit(auditLog, joinPoint);
      auditService.save(audit);
    }
  }

  /**
   * 解析spEL表达式
   */
  private String getValBySpEL(String spEL, MethodSignature methodSignature, Object[] args) {
    //获取方法形参名数组
    String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
    if (paramNames != null && paramNames.length > 0) {
      Expression expression = spelExpressionParser.parseExpression(spEL);
      // spring的表达式上下文对象
      EvaluationContext context = new StandardEvaluationContext();
      // 给上下文赋值
      for (int i = 0; i < args.length; i++) {
        context.setVariable(paramNames[i], args[i]);
      }
      return expression.getValue(context).toString();
    }
    return null;
  }

  /**
   * <p>Title: getParam</p>
   * <p>Description: </p>
   * //     * @param joinPoint //     * @return
   */
  private Map<String, Object> getParam(JoinPoint joinPoint) {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String[] paramNames = methodSignature.getParameterNames();// 参数名
    Map<String, Object> params = null;
    if (paramNames != null && paramNames.length > 0) {
      Object[] args = joinPoint.getArgs();// 参数值
      params = new HashMap<>(16);
      for (int i = 0; i < paramNames.length; i++) {
        if (paramNames[i] instanceof Serializable) {
          params.put(paramNames[i], args[i]);
        }
      }
    }
    return params;
  }

  /**
   * 构建审计对象
   */
  private Audit getAudit(AuditLog auditLog, JoinPoint joinPoint) {
    Audit audit = new Audit();
    audit.setTimestamp(LocalDateTime.now());
    audit.setApplicationName(applicationName);

    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    audit.setClassName(methodSignature.getDeclaringTypeName());
    audit.setMethodName(methodSignature.getName());

    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
        .getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    audit.setActionType(auditLog.actionType());
    //获取请求参数
    String param = "";
    Map<String, Object> paramsMap = null;
    if (auditLog.isRequestParam()) {
      paramsMap = getParam(joinPoint);
      if (paramsMap != null) {
        param = JsonUtils.toJSONString(paramsMap);
      }
    }
    audit.setParam(param);
    audit.setRequestIP(IPUtils.getRemoteAddr(request));
    audit.setHostIP(CustomSystemUtils.INTERNET_IP);
    UserToken userToken = (UserToken) request.getAttribute(WebUtil.REQUEST_TOKEN_NAME);
    if (Func.isNotEmpty(userToken)) {
      audit.setUserId(userToken.getUserId());
      audit.setUserName(userToken.getUserInfo());
    } else {
      if (paramsMap != null) {
        audit.setUserId(paramsMap.get("userId").toString());
      }
    }
    String clientId = request.getHeader(WebUtil.USER_AGENT_HEADER);
    audit.setClientId(clientId);
    String operation = auditLog.operation();
    if (operation.contains("#")) {
      //获取方法参数值
      Object[] args = joinPoint.getArgs();
      operation = getValBySpEL(operation, methodSignature, args);
    }
    audit.setOperation(operation);

    return audit;
  }
}
