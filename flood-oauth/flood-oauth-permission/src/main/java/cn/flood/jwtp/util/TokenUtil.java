package cn.flood.jwtp.util;

import cn.flood.base.core.Func;
import cn.flood.base.core.UserToken;
import cn.flood.base.core.constants.HeaderConstant;
import cn.flood.base.core.context.SpringBeanManager;
import cn.flood.base.core.http.WebUtil;
import cn.flood.jwtp.enums.PlatformEnum;
import cn.flood.jwtp.exception.ExpiredTokenException;
import cn.flood.jwtp.provider.TokenStore;
import cn.flood.base.core.lang.StringPool;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

/**
 * Token工具类
 * <p>
 */
public class TokenUtil {
    // 默认token过期时长,单位秒(2h)
    public static final long DEFAULT_EXPIRE = 2 * 60 * 60;
    // 默认refresh_token过期时长,单位秒(30天)
    public static final long DEFAULT_EXPIRE_REFRESH_TOKEN = 60 * 60 * 24 * 30;

    //默认头
    public static final String PREFIX = "Bearer ";

    public static final String CAPTCHA_HEADER_KEY = "captcha_key";

    public static final String CAPTCHA_HEADER_CODE = "captcha_code";
    /**
     * 生成token
     *
     * @param platform 平台
     * @param tenantId 租户id
     * @param subject  载体
     * @param needRt   是否生成refresh_token
     * @return UserToken
     */
    public static UserToken buildToken(PlatformEnum platform, String tenantId, String subject, boolean needRt) {
        return buildToken(platform, tenantId, subject, DEFAULT_EXPIRE, needRt);
    }

    /**
     * 生成token
     *
     * @param platform 平台
     * @param tenantId 租户id
     * @param subject  载体
     * @param expire   token过期时间，单位秒
     * @param needRt   是否生成refresh_token
     * @return UserToken
     */
    public static UserToken buildToken(PlatformEnum platform, String tenantId, String subject, long expire, boolean needRt) {
        return buildToken(platform, tenantId, subject, expire, DEFAULT_EXPIRE_REFRESH_TOKEN, getKey(), needRt);
    }

    /**
     * 生成token
     *
     * @param platform 平台
     * @param tenantId 租户id
     * @param subject  载体
     * @param expire   token过期时间，单位秒
     * @param rtExpire refresh_token过期时间，单位秒
     * @return
     */
    public static UserToken buildToken(PlatformEnum platform, String tenantId, String subject, long expire, long rtExpire) {
        return buildToken(platform, tenantId, subject, expire, rtExpire, getKey());
    }

    /**
     * 生成token
     *
     * @param platform 平台
     * @param tenantId 租户id
     * @param subject  载体
     * @param expire   token过期时间，单位秒
     * @param rtExpire refresh_token过期时间，单位秒
     * @param key      密钥
     * @return UserToken
     */
    public static UserToken buildToken(PlatformEnum platform, String tenantId, String subject, Long expire, Long rtExpire, Key key) {
        return buildToken(platform, tenantId, subject, expire, rtExpire, key, true);
    }

    /**
     * 生成token
     *
     * @param platform 平台
     * @param tenantId 租户id
     * @param subject  载体
     * @param expire   token过期时间，单位秒
     * @param rtExpire refresh_token过期时间，单位秒
     * @param key      密钥
     * @param needRt   是否生成refresh_token
     * @return UserToken
     */
    public static UserToken buildToken(PlatformEnum platform, String tenantId, String subject, Long expire, Long rtExpire, Key key, boolean needRt) {
        tenantId = Func.toStr(tenantId, HeaderConstant.DEFAULT_TENANT_ID);
        String platformSubject = platform.getType() + StringPool.COLON + tenantId + StringPool.COLON+ subject;
        Date expireDate = new Date(System.currentTimeMillis() + 1000 * expire);
        // 生成access_token
        String access_token = Jwts.builder().setSubject(platformSubject).signWith(key).setExpiration(expireDate).compact();
        // 构建Token对象
        UserToken userToken = new UserToken();
        userToken.setTenantId(tenantId);
        userToken.setUserId(subject);
        userToken.setAccessToken(access_token);
        userToken.setExpireTime(expireDate);
        userToken.setExpireSecond(expire);
        // 生成refresh_token
        if (needRt) {
            Date refreshExpireDate = new Date(System.currentTimeMillis() + 1000 * rtExpire);
            String refresh_token = Jwts.builder().setSubject(platformSubject).signWith(key).setExpiration(refreshExpireDate).compact();
            userToken.setRefreshToken(refresh_token);
            userToken.setRefreshTokenExpireTime(refreshExpireDate);
            userToken.setRefreshTokenExpireSecond(rtExpire);
        }
        return userToken;
    }

    /**
     * 解析token
     *
     * @param token  token
     * @param hexKey 16进制密钥
     * @return 载体
     */
    public static String parseToken(String token, String hexKey) {
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(parseHexKey(hexKey)).build().parseClaimsJws(token);
        return claimsJws.getBody().getSubject();
    }

    /**
     * 生成密钥Key
     *
     * @return Key
     */
    public static Key getKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    /**
     * 生成16进制的key
     *
     * @return hexKey
     */
    public static String getHexKey() {
        return getHexKey(getKey());
    }

    /**
     * 生成16进制的key
     *
     * @param key 密钥Key
     * @return hexKey
     */
    public static String getHexKey(Key key) {
        return Hex.encodeToString(key.getEncoded());
    }

    /**
     * 把16进制的key解析成Key
     *
     * @param hexKey 16进制key
     * @return Key
     */
    public static Key parseHexKey(String hexKey) {
        if (hexKey == null || hexKey.trim().isEmpty()) {
            return null;
        }
        SecretKey key = Keys.hmacShaKeyFor(Hex.decode(hexKey));
        return key;
    }

    /**
     * 从request中获取token
     *
     * @param request HttpServletRequest
     * @return UserToken
     */
    public static UserToken getToken(HttpServletRequest request) {
        Object token = request.getAttribute(WebUtil.REQUEST_TOKEN_NAME);
        return token == null ? null : (UserToken) token;
    }

    /**
     * 获取登录用户信息
     */
    public static UserToken getLoginToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return (UserToken) request.getAttribute(WebUtil.REQUEST_TOKEN_NAME);

    }

    /**
     * 解析token
     *
     * @param request HttpServletRequest
     * @return Token
     */
    public static UserToken parseToken(HttpServletRequest request) {
        TokenStore bean = SpringBeanManager.getBean(TokenStore.class);
        return parseToken(request, bean);
    }

    /**
     * 解析token
     *
     * @param request    HttpServletRequest
     * @param tokenStore TokenStore
     * @return Token
     */
    public static UserToken parseToken(HttpServletRequest request, TokenStore tokenStore) {
        UserToken token = getToken(request);
        if (token == null && tokenStore != null) {
            // 获取token
            String access_token = CheckPermissionUtil.takeToken(request);
            if (access_token != null && !access_token.trim().isEmpty()) {
                try {
                    String tokenKey = tokenStore.getTokenKey();
                    String subject = TokenUtil.parseToken(access_token, tokenKey);
                    String[] subjects = subject.split(StringPool.COLON);
                    int type = Integer.parseInt(subjects[0]);
                    String tenantId = subjects[1];
                    String userId = subjects[2];
                    // 检查token是否存在系统中
                    token = tokenStore.findToken(PlatformEnum.valueOfEnum(type), tenantId, userId, access_token);
                    // 查询用户的角色和权限
                    if (token != null) {
                        token.setRoles(tokenStore.findRolesByUserId(PlatformEnum.valueOfEnum(type), tenantId, userId, token));
                        token.setPermissions(tokenStore.findPermissionsByUserId(PlatformEnum.valueOfEnum(type), tenantId, userId, token));
                    }
                } catch (ExpiredJwtException e) {
                    throw new ExpiredTokenException();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        return token;
    }


    /**
     * 解析token
     *
     * @param access_token
     * @return Token
     */
    public static UserToken parseToken(String access_token) {
        TokenStore bean = SpringBeanManager.getBean(TokenStore.class);
        return parseToken(access_token, bean);
    }

    /**
     * 解析token
     *
     * @param access_token
     * @param tokenStore TokenStore
     * @return Token
     */
    public static UserToken parseToken(String access_token, TokenStore tokenStore) {
        UserToken token = null;
        if (tokenStore != null && !ObjectUtils.isEmpty(access_token)) {
            try {
                String tokenKey = tokenStore.getTokenKey();
                String subject = TokenUtil.parseToken(access_token, tokenKey);
                String[] subjects = subject.split(StringPool.COLON);
                int type = Integer.parseInt(subjects[0]);
                String tenantId = subjects[1];
                String userId = subjects[2];
                // 检查token是否存在系统中
                token = tokenStore.findToken(PlatformEnum.valueOfEnum(type), tenantId, userId, access_token);
                // 查询用户的角色和权限
                if (token != null) {
                    token.setRoles(tokenStore.findRolesByUserId(PlatformEnum.valueOfEnum(type), tenantId, userId, token));
                    token.setPermissions(tokenStore.findPermissionsByUserId(PlatformEnum.valueOfEnum(type), tenantId, userId, token));
                }
            } catch (ExpiredJwtException e) {
                throw new ExpiredTokenException();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

        }
        return token;
    }

}
