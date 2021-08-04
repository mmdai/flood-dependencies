package cn.flood.jwtp.client;

import cn.flood.Func;
import cn.flood.UserToken;
import cn.flood.context.SpringContextManager;
import cn.flood.http.WebUtil;
import cn.flood.json.JsonUtils;
import cn.flood.lang.ReflectBeans;
import cn.flood.okhttp.HttpClient;
import cn.flood.rpc.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import cn.flood.jwtp.exception.ErrorTokenException;
import cn.flood.jwtp.exception.ExpiredTokenException;
import cn.flood.jwtp.exception.UnauthorizedException;
import cn.flood.jwtp.perm.UrlPerm;
import cn.flood.jwtp.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 拦截器
 */
public class ClientInterceptor implements HandlerInterceptor {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private UrlPerm urlPerm;

    private String authCenterUrl;

    private static final String options = "OPTIONS";
    //client key
    private static final String REDIS_KEY = "flood:oauth_client:";
    //存在5分钟
    private static final long REDIS_TIME = 5;

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
        if (authCenterUrl == null) {
            throw new RuntimeException("请配置authCenterUrl");
        }
        UserToken userToken = null;
        StringRedisTemplate stringRedisTemplate = SpringContextManager.getBean(StringRedisTemplate.class);
        //加入redis 缓存
        if(stringRedisTemplate != null){
            String userTokenStr = stringRedisTemplate.opsForValue().get(REDIS_KEY + access_token);
            if(Func.isEmpty(userTokenStr)){
                userToken =  getRestUrlToken(access_token);
                if(userToken != null){
                    stringRedisTemplate.opsForValue().set(REDIS_KEY + access_token, JsonUtils.toJSONString(userToken),
                            REDIS_TIME, TimeUnit.MINUTES);
                }
            }else{
                userToken = JsonUtils.toJavaObject(userTokenStr, UserToken.class);
            }
        }else{
            userToken = getRestUrlToken(access_token);
        }
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

    /**
     * 获取远程token
     * @param access_token
     * @return
     * @throws ExpiredTokenException
     * @throws ErrorTokenException
     */
    public UserToken getRestUrlToken(String access_token) throws ExpiredTokenException, ErrorTokenException {
        String url = authCenterUrl + "/authentication";
        logger.info("========userToken start");
        Result result = HttpClient.get(url).
                queryString("access_token", access_token).asBean(Result.class);
        logger.info("========userToken end: {}", result);
        if(result == null){
            throw new RuntimeException("'" + authCenterUrl + "/authentication' return null");
        }
        if (Result.ERROR_CODE.equals(result.get_code())) {
            throw new ExpiredTokenException();
        } else if (!Result.SUCCESS_CODE.equals(result.get_code())) {
            throw new ErrorTokenException();
        }
        Map<String, Object> dataMap = (Map)result.get_data();
        List<String> roles = Func.isNotEmpty(dataMap.get("roles")) ? (List) dataMap.get("roles") : Collections.EMPTY_LIST;
        dataMap.put("roles", roles.stream().toArray(String[]::new));
        List<String> permissions = Func.isNotEmpty(dataMap.get("permissions")) ? (List) dataMap.get("permissions") : Collections.EMPTY_LIST;
        dataMap.put("permissions", permissions.stream().toArray(String[]::new));
        UserToken userToken = ReflectBeans.convert(UserToken.class,dataMap);
        return userToken;
    }


    public ClientInterceptor() {
    }

    public ClientInterceptor(UrlPerm urlPerm) {
        setUrlPerm(urlPerm);
    }

    public ClientInterceptor(String authCenterUrl, UrlPerm urlPerm) {
        setAuthCenterUrl(authCenterUrl);
        setUrlPerm(urlPerm);
    }

    public void setUrlPerm(UrlPerm urlPerm) {
        this.urlPerm = urlPerm;
    }

    public UrlPerm getUrlPerm() {
        return urlPerm;
    }

    public String getAuthCenterUrl() {
        return authCenterUrl;
    }

    public void setAuthCenterUrl(String authCenterUrl) {
        this.authCenterUrl = authCenterUrl;
    }

}
