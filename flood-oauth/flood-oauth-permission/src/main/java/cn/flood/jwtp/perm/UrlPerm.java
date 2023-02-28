package cn.flood.jwtp.perm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
