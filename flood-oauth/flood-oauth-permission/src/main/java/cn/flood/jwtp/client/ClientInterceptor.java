package cn.flood.jwtp.client;

import cn.flood.base.core.Func;
import cn.flood.base.core.UserToken;
import cn.flood.base.core.constants.HeaderConstant;
import cn.flood.base.core.context.SpringBeanManager;
import cn.flood.base.core.http.WebUtil;
import cn.flood.jwtp.constants.TokenConstant;
import cn.flood.base.core.rpc.response.Result;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 拦截器
 */
public class ClientInterceptor implements HandlerInterceptor {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String COMMA = ",";

    private UrlPerm urlPerm;

    private String authCenterUrl;

    private RestTemplate restTemplate;

    private static final String OPTIONS = "OPTIONS";
    //client key
    private static final String REDIS_KEY = "flood:oauth_client:";
    //存在5分钟
    private static final long REDIS_TIME = 5;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS请求
        if (OPTIONS.equalsIgnoreCase(request.getMethod().toUpperCase())) {
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
        StringRedisTemplate stringRedisTemplate = SpringBeanManager.getBean(StringRedisTemplate.class);
        //加入redis 缓存
        if(stringRedisTemplate != null){
            String userTokenStr = stringRedisTemplate.opsForValue().get(REDIS_KEY + access_token);
            if(Func.isEmpty(userTokenStr)){
                userToken =  getRestUrlToken(access_token);
                if(userToken != null){
                    stringRedisTemplate.opsForValue().set(REDIS_KEY + access_token, Func.toJson(userToken),
                            REDIS_TIME, TimeUnit.MINUTES);
                }
            }else{
                userToken = Func.parse(userTokenStr, UserToken.class);
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
        // 多个地址 任意取一个
        String centerUrl = authCenterUrl;
        if (authCenterUrl.contains(COMMA)) {
            String[] split = authCenterUrl.split(COMMA);
            Random random = new Random();
            int round = random.nextInt(split.length);
            centerUrl = split[round];
        }
        StringBuilder url = new StringBuilder(centerUrl);
        url.append(TokenConstant.URL.AUTH);
        HttpHeaders headers = new HttpHeaders();
        // 封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add(HeaderConstant.ACCESS_TOKEN, access_token);
        HttpEntity requestEntity = new HttpEntity<>(paramMap, headers);
        Stopwatch stopwatch = Stopwatch.createStarted();
        logger.info("【auth http】【{}】 start", centerUrl);
        Result result = restTemplate.postForObject(url.toString(), requestEntity, Result.class);
        logger.info("【auth http】【{}】 end, cost【{}ms】", centerUrl, stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
        if(result == null){
            throw new RuntimeException("'" + authCenterUrl + "/authentication' return null");
        }
        if (Result.ERROR_CODE.equals(result.get_code())) {
            throw new ExpiredTokenException();
        } else if (!Result.SUCCESS_CODE.equals(result.get_code())) {
            throw new ErrorTokenException();
        }
        UserToken userToken = (UserToken) result.get_data();
        return userToken;
    }

    public ClientInterceptor() {
    }

    public ClientInterceptor(UrlPerm urlPerm) {
        setUrlPerm(urlPerm);
    }

    public ClientInterceptor(String authCenterUrl, UrlPerm urlPerm, RestTemplate restTemplate) {
        setAuthCenterUrl(authCenterUrl);
        setUrlPerm(urlPerm);
        setRestTemplate(restTemplate);
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

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
