package cn.flood.jwtp.perm;

import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * url自动对应权限接口
 */
public interface UrlPerm {

    UrlPermResult getPermission(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler);

    UrlPermResult getRoles(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler);

}
