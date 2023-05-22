package cn.flood.jwtp.perm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;

/**
 * url自动对应权限接口
 *
 * @author mmdai
 */
public interface UrlPerm {

  UrlPermResult getPermission(HttpServletRequest request, HttpServletResponse response,
      HandlerMethod handler);

  UrlPermResult getRoles(HttpServletRequest request, HttpServletResponse response,
      HandlerMethod handler);

}
