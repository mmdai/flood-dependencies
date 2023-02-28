package cn.flood.jwtp.perm;

import cn.flood.jwtp.annotation.Logical;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;

/**
 * url自动对应权限 - 简易模式
 *
 * @author mmdai
 */
public class SimpleUrlPerm implements UrlPerm {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public UrlPermResult getPermission(HttpServletRequest request, HttpServletResponse response,
      HandlerMethod handler) {
    String perm = request.getRequestURI();
    logger.debug("Generate Permissions: {}", perm);
    return new UrlPermResult(new String[]{perm}, Logical.OR);
  }

  @Override
  public UrlPermResult getRoles(HttpServletRequest request, HttpServletResponse response,
      HandlerMethod handler) {
    return new UrlPermResult(new String[0], Logical.OR);
  }

}
