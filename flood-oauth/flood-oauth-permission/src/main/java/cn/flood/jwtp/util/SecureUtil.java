package cn.flood.jwtp.util;

import cn.flood.base.core.UserToken;
import cn.flood.base.core.http.WebUtil;
import cn.flood.base.core.lang.StringPool;
import cn.flood.jwtp.annotation.Logical;
import cn.flood.jwtp.enums.PlatformEnum;
import cn.flood.jwtp.provider.TokenStore;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.Collection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 权限检查工具类
 * <p>
 */
public class SecureUtil {

  private static final Logger log = LoggerFactory.getLogger(SecureUtil.class);

  /**
   * 检查是否有指定角色
   *
   * @param userToken UserToken
   * @param roles     角色
   * @param logical   逻辑
   * @return boolean
   */
  public static boolean hasRole(UserToken userToken, String[] roles, Logical logical) {
    if (userToken == null) {
      return false;
    }
    if (roles == null || roles.length <= 0) {
      return true;
    }
    boolean rs = false;
    for (int i = 0; i < roles.length; i++) {
      if (userToken.getRoles() != null) {
        rs = contains(userToken.getRoles(), roles[i]);
      }
      if (logical == (rs ? Logical.OR : Logical.AND)) {
        break;
      }
    }
    return rs;
  }

  /**
   * 检查是否有指定角色
   *
   * @param userToken UserToken
   * @param roles     角色
   * @return boolean
   */
  public static boolean hasRole(UserToken userToken, String roles) {
    return hasRole(userToken, new String[]{roles}, Logical.OR);
  }

  /**
   * 检查是否有指定角色
   *
   * @param request HttpServletRequest
   * @param roles   角色
   * @param logical 逻辑
   * @return boolean
   */
  public static boolean hasRole(HttpServletRequest request, String[] roles, Logical logical) {
    return hasRole(getToken(request), roles, logical);
  }

  /**
   * 检查是否有指定角色
   *
   * @param request HttpServletRequest
   * @param roles   角色
   * @return boolean
   */
  public static boolean hasRole(HttpServletRequest request, String roles) {
    return hasRole(getToken(request), new String[]{roles}, Logical.OR);
  }

  /**
   * 检查是否有指定权限
   *
   * @param userToken   UserToken
   * @param permissions 权限
   * @param logical     逻辑
   * @return boolean
   */
  public static boolean hasPermission(UserToken userToken, String[] permissions, Logical logical) {
    if (userToken == null) {
      return false;
    }
    if (permissions == null || permissions.length <= 0) {
      return true;
    }
    boolean rs = false;
    for (int i = 0; i < permissions.length; i++) {
      if (userToken.getPermissions() != null) {
        rs = contains(userToken.getPermissions(), permissions[i]);
      }
      if (logical == (rs ? Logical.OR : Logical.AND)) {
        break;
      }
    }
    return rs;
  }

  /**
   * 检查是否有指定权限
   *
   * @param userToken   UserToken
   * @param permissions 权限
   * @return boolean
   */
  public static boolean hasPermission(UserToken userToken, String permissions) {
    return hasPermission(userToken, new String[]{permissions}, Logical.OR);
  }

  /**
   * 检查是否有指定权限
   *
   * @param request     HttpServletRequest
   * @param permissions 权限
   * @param logical     逻辑
   * @return boolean
   */
  public static boolean hasPermission(HttpServletRequest request, String[] permissions,
      Logical logical) {
    return hasPermission(getToken(request), permissions, logical);
  }

  /**
   * 检查是否有指定权限
   *
   * @param request     HttpServletRequest
   * @param permissions 权限
   * @return boolean
   */
  public static boolean hasPermission(HttpServletRequest request, String permissions) {
    return hasPermission(getToken(request), new String[]{permissions}, Logical.OR);
  }

  /**
   * 从request中获取token
   *
   * @param request HttpServletRequest
   * @return UserToken
   */
  public static UserToken getToken(HttpServletRequest request) {
    Object token = request.getAttribute(WebUtil.REQUEST_TOKEN_NAME);
    return token == null ? null : (UserToken) token;
  }

  /**
   * 解析token
   *
   * @param request HttpServletRequest
   * @return UserToken
   */
  public static UserToken parseToken(HttpServletRequest request) {
    return parseToken(request, getBean(TokenStore.class));
  }

  /**
   * 解析token
   *
   * @param request    HttpServletRequest
   * @param tokenStore TokenStore
   * @return UserToken
   */
  public static UserToken parseToken(HttpServletRequest request, TokenStore tokenStore) {
    UserToken userToken = getToken(request);
    if (userToken == null && tokenStore != null) {
      // 获取token
      String access_token = CheckPermissionUtil.takeToken(request);
      if (access_token != null && !access_token.trim().isEmpty()) {
        try {
          String tokenKey = tokenStore.getTokenKey();
          String subject = TokenUtil.parseToken(access_token, tokenKey);
          String[] subjects = subject.split(StringPool.COLON);
          int type = Integer.parseInt(subjects[0]);
          String tenantId = subjects[1];
          String userId = subjects[2];
          // 检查token是否存在系统中
          userToken = tokenStore
              .findToken(PlatformEnum.valueOfEnum(type), tenantId, userId, access_token);
          // 查询用户的角色和权限
          if (userToken != null) {
            userToken.setRoles(tokenStore
                .findRolesByUserId(PlatformEnum.valueOfEnum(type), tenantId, userId, userToken));
            userToken.setPermissions(tokenStore
                .findPermissionsByUserId(PlatformEnum.valueOfEnum(type), tenantId, userId,
                    userToken));
          }
        } catch (ExpiredJwtException e) {
          log.error("token已过期");
        } catch (Exception e) {
          log.error(e.getMessage());
        }
      }
    }
    return userToken;
  }


  /**
   * 判断数组是否包含指定元素
   *
   * @param strs 数组
   * @param str  元素
   * @return boolean
   */
  private static boolean contains(String[] strs, String str) {
    for (int i = 0; i < strs.length; i++) {
      if (strs[i].equals(str)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 获取Bean
   */
  public static <T> T getBean(Class<T> clazz) {
    T bean = null;
    try {
      ServletContext servletContext = ContextLoader.getCurrentWebApplicationContext()
          .getServletContext();
      ApplicationContext applicationContext = WebApplicationContextUtils
          .getWebApplicationContext(servletContext);
      Collection<T> beans = applicationContext.getBeansOfType(clazz).values();
      while (beans.iterator().hasNext()) {
        bean = beans.iterator().next();
        if (bean != null) {
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return bean;
  }

}
