package cn.flood.jwtp;

import cn.flood.Func;
import cn.flood.UserToken;
import cn.flood.constants.TenantConstant;
import cn.flood.context.TenantContextHolder;
import cn.flood.http.WebUtil;
import cn.flood.jwtp.util.CheckPermissionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TenantInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static String options = "OPTIONS";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行options请求
        if (options.equalsIgnoreCase(request.getMethod().toUpperCase())) {
            CheckPermissionUtil.passOptions(response);
            return false;
        }
        //优先取请求参数中的tenantId值
        String tenantId = request.getHeader(TenantConstant.FLOOD_TENANT_ID);
        if (Func.isEmpty(tenantId)) {
            UserToken userToken = (UserToken) request.getAttribute(WebUtil.REQUEST_TOKEN_NAME);
            if (Func.isNotEmpty(userToken)) {
                //取token中的tenantId值
                tenantId = String.valueOf(userToken.getTenantId());
            }
        }
        log.info("获取到的租户ID为:{}", tenantId);
        if (Func.isNotBlank(tenantId)) {
            TenantContextHolder.setTenantId(tenantId);
        } else {
            if (Func.isBlank(TenantContextHolder.getTenantId())) {
                TenantContextHolder.setTenantId(TenantConstant.TENANT_ID_DEFAULT);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        TenantContextHolder.clear();
    }


    public TenantInterceptor() {
    }
}

