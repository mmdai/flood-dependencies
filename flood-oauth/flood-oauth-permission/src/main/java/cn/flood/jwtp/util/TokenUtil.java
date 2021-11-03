package cn.flood.jwtp.util;

import cn.flood.UserToken;
import cn.flood.context.SpringContextManager;
import cn.flood.http.WebUtil;
import cn.flood.jwtp.exception.ExpiredTokenException;
import cn.flood.jwtp.provider.TokenStore;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

/**
 * Token工具类
 * <p>
 */
public class TokenUtil {
    public static final long DEFAULT_EXPIRE = 2 * 60 * 60;  // 默认token过期时长,单位秒
    public static final long DEFAULT_EXPIRE_REFRESH_TOKEN = 60 * 60 * 24 * 30 * 3;  // 默认refresh_token过期时长,单位秒

    public static final String PREFIX = "Bearer ";//默认头
    /**
     * 生成token
     *
     * @param subject 载体
     * @return UserToken
     */
    public static UserToken buildToken(String subject) {
        return buildToken(subject, DEFAULT_EXPIRE);
    }

    /**
     * 生成token
     *
     * @param subject 载体
     * @param expire  token过期时间，单位秒
     * @return UserToken
     */
    public static UserToken buildToken(String subject, long expire) {
        return buildToken(subject, expire, DEFAULT_EXPIRE_REFRESH_TOKEN);
    }

    /**
     * 生成token
     *
     * @param subject  载体
     * @param expire   token过期时间，单位秒
     * @param rtExpire refresh_token过期时间，单位秒
     * @return
     */
    public static UserToken buildToken(String subject, long expire, long rtExpire) {
        return buildToken(subject, expire, rtExpire, getKey());
    }

    /**
     * 生成token
     *
     * @param subject  载体
     * @param expire   token过期时间，单位秒
     * @param rtExpire refresh_token过期时间，单位秒
     * @param key      密钥
     * @return UserToken
     */
    public static UserToken buildToken(String subject, Long expire, Long rtExpire, Key key) {
        return buildToken(subject, expire, rtExpire, key, true);
    }

    /**
     * 生成token
     *
     * @param subject  载体
     * @param expire   token过期时间，单位秒
     * @param rtExpire refresh_token过期时间，单位秒
     * @param key      密钥
     * @param needRt   是否生成refresh_token
     * @return UserToken
     */
    public static UserToken buildToken(String subject, Long expire, Long rtExpire, Key key, boolean needRt) {
        Date expireDate = new Date(new Date().getTime() + 1000 * expire);
        // 生成access_token
        String access_token = Jwts.builder().setSubject(subject).signWith(key).setExpiration(expireDate).compact();
        // 构建Token对象
        UserToken userToken = new UserToken();
        userToken.setUserId(subject);
        userToken.setAccessToken(access_token);
        userToken.setExpireTime(expireDate);
        // 生成refresh_token
        if (needRt) {
            Date refreshExpireDate = new Date(new Date().getTime() + 1000 * rtExpire);
            String refresh_token = Jwts.builder().setSubject(subject).signWith(key).setExpiration(refreshExpireDate).compact();
            userToken.setRefreshToken(refresh_token);
            userToken.setRefreshTokenExpireTime(refreshExpireDate);
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
     * 解析token
     *
     * @param request HttpServletRequest
     * @return Token
     */
    public static UserToken parseToken(HttpServletRequest request) {
        TokenStore bean = SpringContextManager.getBean(TokenStore.class);
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
                    String userId = TokenUtil.parseToken(access_token, tokenKey);
                    // 检查token是否存在系统中
                    token = tokenStore.findToken(userId, access_token);
                    // 查询用户的角色和权限
                    if (token != null) {
                        token.setRoles(tokenStore.findRolesByUserId(userId, token));
                        token.setPermissions(tokenStore.findPermissionsByUserId(userId, token));
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
     * 判断数组是否包含指定元素
     *
     * @param strs 数组
     * @param str  元素
     * @return boolean
     */
    private static boolean contains(String[] strs, String str) {
        for (int i = 0; i < strs.length; i++) {
            // 处理空指针
            String str1 = strs[i];
            if (StringUtils.hasText(str1)) {
                if (str1.equals(str)) {
                    return true;
                }
            } else {
                continue;
            }
        }
        return false;
    }

}
