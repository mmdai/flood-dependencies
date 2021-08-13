package cn.flood.jwtp.provider;

import cn.flood.Func;
import cn.flood.UserToken;
import io.jsonwebtoken.ExpiredJwtException;
import cn.flood.jwtp.exception.ErrorTokenException;
import cn.flood.jwtp.exception.ExpiredTokenException;
import cn.flood.jwtp.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 操作token的接口
 */
public abstract class TokenStoreAbstract implements TokenStore {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Integer maxToken = -1;  // 单个用户最大的token数量

    private String findRolesSql;  // 查询用户角色的sql

    private String findPermissionsSql;  // 查询用户权限的sql

    public static String mTokenKey;  // 生成token用的Key

    @Override
    public UserToken createNewToken(String userId) {
        return createNewToken(userId, null, null, null);
    }

    @Override
    public UserToken createNewToken(String userId, String userInfo) {
        return createNewToken(userId, userInfo,null, null);
    }

    @Override
    public UserToken createNewToken(String userId, String userInfo, long expire) {
        return createNewToken(userId, userInfo, null, null, expire);
    }

    @Override
    public UserToken createNewToken(String userId, String userInfo, long expire, long rtExpire) {
        return createNewToken(userId, userInfo, null, null, expire, rtExpire);
    }

    @Override
    public UserToken createNewToken(String userId, String userInfo, String[] permissions, String[] roles) {
        return createNewToken(userId, userInfo, permissions, roles, TokenUtil.DEFAULT_EXPIRE);
    }

    @Override
    public UserToken createNewToken(String userId, String userInfo, String[] permissions, String[] roles, long expire) {
        return createNewToken(userId, userInfo, permissions, roles, expire, TokenUtil.DEFAULT_EXPIRE_REFRESH_TOKEN);
    }

    @Override
    public UserToken createNewToken(String userId, String userInfo, String[] permissions, String[] roles, long expire, long rtExpire) {
        String tokenKey = getTokenKey();
        logger.debug("TOKEN_KEY: " + tokenKey);
        UserToken userToken = TokenUtil.buildToken(userId, expire, rtExpire, TokenUtil.parseHexKey(tokenKey));
        userToken.setRoles(roles);
        userToken.setPermissions(permissions);
        if(Func.isNotEmpty(userInfo)){
            userToken.setUserInfo(userInfo);
        }
        if (storeToken(userToken) > 0) {
            return userToken;
        }
        return null;
    }

    @Override
    public UserToken refreshToken(String refresh_token) throws CoreException  {
        return refreshToken(refresh_token, null, TokenUtil.DEFAULT_EXPIRE);
    }

    @Override
    public UserToken refreshToken(String refresh_token, String userInfo) throws CoreException  {
        return refreshToken(refresh_token, userInfo, TokenUtil.DEFAULT_EXPIRE);
    }

    @Override
    public UserToken refreshToken(String refresh_token, String userInfo, long expire) throws CoreException {
        return refreshToken(refresh_token, userInfo, null, null, expire);
    }

    @Override
    public UserToken refreshToken(String refresh_token, String userInfo, String[] permissions, String[] roles, long expire) throws CoreException {
        String tokenKey = getTokenKey();
        logger.debug("TOKEN_KEY: " + tokenKey);
        String userId;
        try {
            userId = TokenUtil.parseToken(refresh_token, tokenKey);
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (Exception e) {
            throw new ErrorTokenException();
        }
        if (userId != null) {
            // 检查token是否存在系统中
            UserToken refreshUserToken = findRefreshToken(userId, refresh_token);
            if (refreshUserToken == null) {
                throw new ErrorTokenException();
            }
            // 生成新的token
            UserToken userToken = TokenUtil.buildToken(userId, expire, null, TokenUtil.parseHexKey(tokenKey), false);
            userToken.setRoles(roles);
            if(Func.isNotEmpty(userInfo)){
                userToken.setUserInfo(userInfo);
            }
            userToken.setPermissions(permissions);
            userToken.setRefreshToken(refresh_token);
            userToken.setRefreshTokenExpireTime(refreshUserToken.getRefreshTokenExpireTime());
            if (storeToken(userToken) > 0) {
                return userToken;
            }
        }
        return null;
    }

    @Override
    public void setMaxToken(Integer maxToken) {
        this.maxToken = maxToken;
    }

    @Override
    public void setFindRolesSql(String findRolesSql) {
        this.findRolesSql = findRolesSql;
    }

    @Override
    public void setFindPermissionsSql(String findPermissionsSql) {
        this.findPermissionsSql = findPermissionsSql;
    }

    @Override
    public Integer getMaxToken() {
        return maxToken;
    }

    @Override
    public String getFindRolesSql() {
        return findRolesSql;
    }

    @Override
    public String getFindPermissionsSql() {
        return findPermissionsSql;
    }

}
