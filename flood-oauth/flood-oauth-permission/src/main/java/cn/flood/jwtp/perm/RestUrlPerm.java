package cn.flood.jwtp.perm;

import cn.flood.jwtp.annotation.Logical;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

/**
 * url自动对应权限 - RESTful模式
 *
 * @author mmdai
 */
public class RestUrlPerm implements UrlPerm {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public UrlPermResult getPermission(HttpServletRequest request, HttpServletResponse response,
      HandlerMethod handler) {
    Method method = handler.getMethod();
    List<String> perms = new ArrayList<String>();
    // 获取Method上的注解
    String[] urls, types, urlPres;
    if (method.getAnnotation(GetMapping.class) != null) {
      urls = method.getAnnotation(GetMapping.class).value();
      types = new String[]{"get"};
    } else if (method.getAnnotation(PostMapping.class) != null) {
      urls = method.getAnnotation(PostMapping.class).value();
      types = new String[]{"post"};
    } else if (method.getAnnotation(PutMapping.class) != null) {
      urls = method.getAnnotation(PutMapping.class).value();
      types = new String[]{"put"};
    } else if (method.getAnnotation(DeleteMapping.class) != null) {
      urls = method.getAnnotation(DeleteMapping.class).value();
      types = new String[]{"delete"};
    } else if (method.getAnnotation(RequestMapping.class) != null) {
      urls = method.getAnnotation(RequestMapping.class).value();
      types = new String[]{"get", "post", "put", "delete"};
    } else {
      urls = new String[0];
      types = new String[0];
    }
    // 获取Class上的注解
    Class<?> clazz = method.getDeclaringClass();
    if (clazz.getAnnotation(GetMapping.class) != null) {
      urlPres = clazz.getAnnotation(GetMapping.class).value();
    } else if (clazz.getAnnotation(PostMapping.class) != null) {
      urlPres = clazz.getAnnotation(PostMapping.class).value();
    } else if (clazz.getAnnotation(PutMapping.class) != null) {
      urlPres = clazz.getAnnotation(PutMapping.class).value();
    } else if (clazz.getAnnotation(DeleteMapping.class) != null) {
      urlPres = clazz.getAnnotation(DeleteMapping.class).value();
    } else if (clazz.getAnnotation(RequestMapping.class) != null) {
      urlPres = clazz.getAnnotation(RequestMapping.class).value();
    } else {
      urlPres = new String[0];
    }
    // 生成权限
    for (String type : types) {
      for (String url : urls) {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        sb.append(":");
        if (urlPres != null && urlPres.length > 0) {
          if (!urlPres[0].startsWith("/")) {
            sb.append("/");
          }
          sb.append(urlPres[0]);
        }
        if (!url.startsWith("/")) {
          sb.append("/");
        }
        sb.append(url);
        perms.add(sb.toString());
      }
    }
    String[] arrays = perms.toArray(new String[perms.size()]);
    logger.debug("Generate Permissions: " + Arrays.toString(arrays));
    return new UrlPermResult(arrays, Logical.OR);
  }

  @Override
  public UrlPermResult getRoles(HttpServletRequest request, HttpServletResponse response,
      HandlerMethod handler) {
    return new UrlPermResult(new String[0], Logical.OR);
  }

}
