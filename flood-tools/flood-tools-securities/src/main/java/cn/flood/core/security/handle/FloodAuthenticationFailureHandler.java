package cn.flood.core.security.handle;

import cn.flood.Func;
import cn.flood.exception.enums.GlobalErrorCodeEnum;
import cn.flood.http.WebUtil;
import cn.flood.rpc.response.Result;
import cn.flood.rpc.response.ResultWapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录失败的回调
 * @author mmdai
 */
public class FloodAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        Result<?> result = null;
        String username = request.getParameter("username");
        String langContent = request.getHeader(HttpHeaders.CONTENT_LANGUAGE);
        if(Func.isEmpty(langContent)){
            langContent = "zh_CN";
        }
        if (exception instanceof AccountExpiredException) {
            // 账号过期
            log.info("[登录失败] - 用户[{}]账号过期", username);
            result = ResultWapper.wrap(GlobalErrorCodeEnum.USER_ACCOUNT_EXPIRED.getCode(),
                    getResultByLangContent(langContent, GlobalErrorCodeEnum.USER_ACCOUNT_EXPIRED));

        } else if (exception instanceof BadCredentialsException) {
            // 密码错误
            log.info("[登录失败] - 用户[{}]密码错误", username);
            result = ResultWapper.wrap(GlobalErrorCodeEnum.USER_PASSWORD_ERROR.getCode(),
                    getResultByLangContent(langContent, GlobalErrorCodeEnum.USER_PASSWORD_ERROR));

        } else if (exception instanceof CredentialsExpiredException) {
            // 密码过期
            log.info("[登录失败] - 用户[{}]密码过期", username);
            result = ResultWapper.wrap(GlobalErrorCodeEnum.USER_PASSWORD_EXPIRED.getCode(),
                    getResultByLangContent(langContent, GlobalErrorCodeEnum.USER_PASSWORD_EXPIRED));

        } else if (exception instanceof DisabledException) {
            // 用户被禁用
            log.info("[登录失败] - 用户[{}]被禁用", username);
            result = ResultWapper.wrap(GlobalErrorCodeEnum.USER_DISABLED.getCode(),
                    getResultByLangContent(langContent, GlobalErrorCodeEnum.USER_DISABLED));

        } else if (exception instanceof LockedException) {
            // 用户被锁定
            log.info("[登录失败] - 用户[{}]被锁定", username);
            result = ResultWapper.wrap(GlobalErrorCodeEnum.USER_LOCKED.getCode(),
                    getResultByLangContent(langContent, GlobalErrorCodeEnum.USER_LOCKED));

        } else if (exception instanceof InternalAuthenticationServiceException) {
            // 内部错误
            log.error(String.format("[登录失败] - [%s]内部错误", username));
            result = ResultWapper.wrap(GlobalErrorCodeEnum.USER_LOGIN_FAIL.getCode(),
                    getResultByLangContent(langContent, GlobalErrorCodeEnum.USER_LOGIN_FAIL));

        } else {
            // 其他错误
            log.error(String.format("[登录失败] - [%s]其他错误", username), exception);
            result = ResultWapper.wrap(GlobalErrorCodeEnum.USER_LOGIN_FAIL.getCode(),
                    getResultByLangContent(langContent, GlobalErrorCodeEnum.USER_LOGIN_FAIL));
        }
        WebUtil.responseWriter(response, "UTF-8", HttpStatus.UNAUTHORIZED.value(), result);
    }

    /**
     *
     * @param langContent
     * @param errorCode
     * @return
     */
    private String getResultByLangContent(String langContent, GlobalErrorCodeEnum errorCode){
        if(langContent.equalsIgnoreCase(langContent)){
            return errorCode.getZhName();
        }
        return errorCode.getEnName();
    }
}
