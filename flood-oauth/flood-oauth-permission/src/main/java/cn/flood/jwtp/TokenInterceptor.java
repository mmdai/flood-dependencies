package cn.flood.jwtp;

import cn.flood.UserToken;
import cn.flood.http.WebUtil;
import cn.flood.jwtp.enums.PlatformEnum;
import cn.flood.lang.StringPool;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import cn.flood.jwtp.exception.ErrorTokenException;
import cn.flood.jwtp.exception.ExpiredTokenException;
import cn.flood.jwtp.exception.UnauthorizedException;
import cn.flood.jwtp.perm.UrlPerm;
import cn.flood.jwtp.provider.TokenStore;
import cn.flood.jwtp.util.CheckPermissionUtil;
import cn.flood.jwtp.util.TokenUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 拦截器
 */
public class TokenInterceptor implements HandlerInterceptor {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TokenStore tokenStore;

    private UrlPerm urlPerm;

    private final static String options = "OPTIONS";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行options请求
        if (options.equalsIgnoreCase(request.getMethod().toUpperCase())) {
            CheckPermissionUtil.passOptions(response);
            return false;
        }
        Method method = null;
        if (handler instanceof HandlerMethod) {
            method = ((HandlerMethod) handler).getMethod();
        }
        // 检查是否忽略权限验证
        if (method == null || CheckPermissionUtil.checkIgnore(method)) {
            return true;
        }
        // 获取token
        String access_token = CheckPermissionUtil.takeToken(request);
        if (access_token == null || access_token.trim().isEmpty()) {
            throw new ErrorTokenException("Token不能为空");
        }
        String subject;
        try {
            String tokenKey = tokenStore.getTokenKey();
            logger.debug("ACCESS_TOKEN: {} , TOKEN_KEY: {}", access_token , tokenKey);
            subject = TokenUtil.parseToken(access_token, tokenKey);
        } catch (ExpiredJwtException e) {
            logger.debug("ERROR: ExpiredJwtException");
            throw new ExpiredTokenException();
        } catch (Exception e) {
            throw new ErrorTokenException();
        }
        String[] subjects = subject.split(StringPool.COLON);
        int type = Integer.parseInt(subjects[0]);
        String tenantId = subjects[1];
        String userId = subjects[2];
        // 检查token是否存在系统中
        UserToken userToken = tokenStore.findToken(PlatformEnum.valueOfEnum(type), tenantId, userId, access_token);
        if (userToken == null) {
            logger.debug("ERROR: UserToken Not Found");
            throw new ErrorTokenException();
        }
        // 查询用户的角色和权限
        userToken.setRoles(tokenStore.findRolesByUserId(PlatformEnum.valueOfEnum(type), tenantId, userId, userToken));
        userToken.setPermissions(tokenStore.findPermissionsByUserId(PlatformEnum.valueOfEnum(type), tenantId, userId, userToken));
        // 检查是否直接返回token
        if (CheckPermissionUtil.checkToken(method)) {
            request.setAttribute(WebUtil.REQUEST_TOKEN_NAME, userToken);
            return true;
        }
        // 检查权限
        if (CheckPermissionUtil.isNoPermission(userToken, request, response, handler, urlPerm)) {
            throw new UnauthorizedException();
        }
        request.setAttribute(WebUtil.REQUEST_TOKEN_NAME, userToken);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        request.removeAttribute(WebUtil.REQUEST_TOKEN_NAME);
    }


    public TokenInterceptor() {
    }

    public TokenInterceptor(TokenStore tokenStore) {
        setTokenStore(tokenStore);
    }

    public TokenInterceptor(TokenStore tokenStore, UrlPerm urlPerm) {
        setTokenStore(tokenStore);
        setUrlPerm(urlPerm);
    }

    public TokenStore getTokenStore() {
        return tokenStore;
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    public void setUrlPerm(UrlPerm urlPerm) {
        this.urlPerm = urlPerm;
    }

    public UrlPerm getUrlPerm() {
        return urlPerm;
    }

}
