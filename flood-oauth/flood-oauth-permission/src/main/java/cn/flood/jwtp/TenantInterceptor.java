package cn.flood.jwtp;

import cn.flood.base.core.Func;
import cn.flood.base.core.UserToken;
import cn.flood.base.core.constants.HeaderConstant;
import cn.flood.base.core.context.TenantContextHolder;
import cn.flood.base.core.http.WebUtil;
import cn.flood.jwtp.util.CheckPermissionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

public class TenantInterceptor implements HandlerInterceptor {

  private final static String options = "OPTIONS";
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  public TenantInterceptor() {
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    // 放行options请求
    if (options.equalsIgnoreCase(request.getMethod().toUpperCase())) {
      CheckPermissionUtil.passOptions(response);
      return false;
    }
    //优先取请求参数中的tenantId值
    String tenantId = request.getHeader(HeaderConstant.TENANT_ID);
    if (Func.isEmpty(tenantId)) {
      UserToken userToken = (UserToken) request.getAttribute(WebUtil.REQUEST_TOKEN_NAME);
      if (Func.isNotEmpty(userToken)) {
        //取token中的tenantId值
        tenantId = userToken.getTenantId();
      }
    }
    log.debug("获取到的租户ID为:{}", tenantId);
    if (Func.isNotBlank(tenantId)) {
      TenantContextHolder.setTenantId(tenantId);
    } else {
      if (Func.isBlank(TenantContextHolder.getTenantId())) {
        TenantContextHolder.setTenantId(HeaderConstant.DEFAULT_TENANT_ID);
      }
    }
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, @Nullable Exception ex) throws Exception {
    TenantContextHolder.clear();
  }
}

