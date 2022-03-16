package cn.flood.jwtp.util;

import cn.flood.Func;
import cn.flood.UserToken;
import cn.flood.constants.HeaderConstant;
import cn.flood.json.JsonUtils;
import cn.flood.jwtp.annotation.*;
import cn.flood.jwtp.requestWrapper.RequestWrapper;
import cn.flood.lang.StringPool;
import org.springframework.http.HttpHeaders;

import org.springframework.web.method.HandlerMethod;
import cn.flood.jwtp.perm.UrlPerm;
import cn.flood.jwtp.perm.UrlPermResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * CheckPermissionUtil
 */
public class CheckPermissionUtil {

    /**
     * 检查是否忽略权限
     */
    public static boolean checkIgnore(Method method) {
        Ignore annotation = method.getAnnotation(Ignore.class);
        if (annotation == null) {  // 方法上没有注解再检查类上面有没有注解
            annotation = method.getDeclaringClass().getAnnotation(Ignore.class);
            if (annotation == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查是否获取token
     * @param method
     * @return
     */
    public static boolean checkToken(Method method) {
        RequiresToken annotation = method.getAnnotation(RequiresToken.class);
        if (annotation == null) {  // 方法上没有注解再检查类上面有没有注解
            annotation = method.getDeclaringClass().getAnnotation(RequiresToken.class);
            if (annotation == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查权限是否符合
     */
    public static boolean checkPermission(UserToken userToken, HttpServletRequest request, HttpServletResponse response, Object handler, UrlPerm urlPerm) {
        Method method = ((HandlerMethod) handler).getMethod();
        RequiresPermissions annotation = method.getAnnotation(RequiresPermissions.class);
        if (annotation == null) {  // 方法上没有注解再检查类上面有没有注解
            annotation = method.getDeclaringClass().getAnnotation(RequiresPermissions.class);
        }
        String[] requiresPermissions;
        Logical logical;
        if (annotation != null) {
            requiresPermissions = annotation.value();
            logical = annotation.logical();
        } else if (urlPerm != null) {
            UrlPermResult upr = urlPerm.getPermission(request, response, (HandlerMethod) handler);
            requiresPermissions = upr.getValues();
            logical = upr.getLogical();
        } else {
            return true;
        }
        return SecureUtil.hasPermission(userToken, requiresPermissions, logical);
    }

    /**
     * 检查角色是否符合
     */
    public static boolean checkRole(UserToken userToken, HttpServletRequest request, HttpServletResponse response, Object handler, UrlPerm urlPerm) {
        Method method = ((HandlerMethod) handler).getMethod();
        RequiresRoles annotation = method.getAnnotation(RequiresRoles.class);
        if (annotation == null) {  // 方法上没有注解再检查类上面有没有注解
            annotation = method.getDeclaringClass().getAnnotation(RequiresRoles.class);
        }
        String[] requiresRoles;
        Logical logical;
        if (annotation != null) {
            requiresRoles = annotation.value();
            logical = annotation.logical();
        } else if (urlPerm != null) {
            UrlPermResult upr = urlPerm.getRoles(request, response, (HandlerMethod) handler);
            requiresRoles = upr.getValues();
            logical = upr.getLogical();
        } else {
            return true;
        }
        return SecureUtil.hasRole(userToken, requiresRoles, logical);
    }

    /**
     * 检查是否是没有权限或没有角色
     */
    public static boolean isNoPermission(UserToken userToken, HttpServletRequest request, HttpServletResponse response, Object handler, UrlPerm urlPerm) {
        return !checkPermission(userToken, request, response, handler, urlPerm) || !checkRole(userToken, request, response, handler, urlPerm);
    }

    /**
     * 放行options请求
     */
    public static void passOptions(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with, X-Custom-Header, Authorization");
    }

    /**
     * 取出前端传递的token
     */
    public static String takeToken(HttpServletRequest request) {
        String access_token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Func.isNotEmpty(access_token)) {
            access_token = access_token.replaceFirst(TokenUtil.PREFIX, StringPool.EMPTY);
        }else {
            access_token = request.getParameter(HeaderConstant.ACCESS_TOKEN);
            if (access_token == null || access_token.trim().isEmpty()) {
                // 防止流读取一次后就没有了, 所以需要将流继续写出去
                String payload = new RequestWrapper(request).getBodyString(request);
//                log.info("payload:【{}】", payload);
                if(!Func.isEmpty(payload)){
                    Map<String, Object> map = JsonUtils.toMap(payload);
                    access_token = ((String) map.get(HeaderConstant.ACCESS_TOKEN)).replaceFirst(TokenUtil.PREFIX, StringPool.EMPTY);
                }
            }
        }
        return access_token;
    }

}
