package cn.flood.jwtp.provider;

import cn.flood.Func;
import cn.flood.UserToken;
import cn.flood.constants.HeaderConstant;
import cn.flood.exception.CoreException;
import cn.flood.jwtp.enums.PlatformEnum;
import cn.flood.lang.StringPool;
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

    /**
     *
     * @param platform  平台类型
     * @param tenantId  租户id
     * @param userId    用户id
     * @return
     */
    @Override
    public UserToken createNewToken(PlatformEnum platform, String tenantId, String userId) {
        return createNewToken(platform, tenantId, userId, null, null, null);
    }

    /**
     *
     * @param platform  平台类型
     * @param tenantId  租户id
     * @param userId    用户id
     * @param userInfo  用户信息
     * @return
     */
    @Override
    public UserToken createNewToken(PlatformEnum platform, String tenantId, String userId, String userInfo) {
        return createNewToken(platform, tenantId, userId, userInfo,null, null);
    }

    /**
     *
     * @param platform  平台类型
     * @param tenantId  租户id
     * @param userId    用户id
     * @param userInfo  用户信息
     * @param expire    token过期时间,单位秒
     * @return
     */
    @Override
    public UserToken createNewToken(PlatformEnum platform, String tenantId, String userId, String userInfo, long expire) {
        return createNewToken(platform, tenantId, userId, userInfo, null, null, expire);
    }

    /**
     *
     * @param platform  平台类型
     * @param tenantId  租户id
     * @param userId    用户id
     * @param userInfo  用户信息
     * @param expire    token过期时间,单位秒
     * @param rtExpire  refresh_token过期时间,单位秒
     * @return
     */
    @Override
    public UserToken createNewToken(PlatformEnum platform, String tenantId, String userId, String userInfo, long expire, long rtExpire) {
        return createNewToken(platform, tenantId, userId, userInfo, null, null, expire, rtExpire);
    }

    /**
     *
     * @param platform    平台类型
     * @param tenantId    租户id
     * @param userId      用户id
     * @param userInfo    用户信息
     * @param permissions 权限
     * @param roles       角色
     * @return
     */
    @Override
    public UserToken createNewToken(PlatformEnum platform, String tenantId, String userId, String userInfo, String[] permissions, String[] roles) {
        return createNewToken(platform, tenantId, userId, userInfo, permissions, roles, TokenUtil.DEFAULT_EXPIRE);
    }

    /**
     *
     * @param platform    平台类型
     * @param tenantId    租户id
     * @param userId      用户id
     * @param userInfo    用户信息
     * @param permissions 权限
     * @param roles       角色
     * @param expire      token过期时间,单位秒
     * @return
     */
    @Override
    public UserToken createNewToken(PlatformEnum platform, String tenantId, String userId, String userInfo, String[] permissions, String[] roles, long expire) {
        return createNewToken(platform, tenantId, userId, userInfo, permissions, roles, expire, TokenUtil.DEFAULT_EXPIRE_REFRESH_TOKEN);
    }

    /**
     *
     * @param platform    平台类型
     * @param tenantId    租户id
     * @param userId      用户id
     * @param userInfo    用户信息
     * @param permissions 权限
     * @param roles       角色
     * @param expire      token过期时间,单位秒
     * @param rtExpire
     * @return
     */
    @Override
    public UserToken createNewToken(PlatformEnum platform, String tenantId, String userId, String userInfo, String[] permissions, String[] roles, long expire, long rtExpire) {
        String tokenKey = getTokenKey();
        logger.debug("TOKEN_KEY: " + tokenKey);
        tenantId = Func.toStr(tenantId, HeaderConstant.DEFAULT_TENANT_ID);
        UserToken userToken = TokenUtil.buildToken(platform, tenantId, userId, expire, rtExpire, TokenUtil.parseHexKey(tokenKey));
        userToken.setRoles(roles);
        userToken.setPermissions(permissions);
        if(Func.isNotEmpty(userInfo)){
            userToken.setUserInfo(userInfo);
        }
        if (storeToken(platform, tenantId, userToken) > 0) {
            return userToken;
        }
        return null;
    }

    /**
     *
     * @param platform      平台类型
     * @param tenantId      租户id
     * @param refresh_token refresh_token
     * @return
     * @throws CoreException
     */
    @Override
    public UserToken refreshToken(PlatformEnum platform, String tenantId, String refresh_token) throws CoreException {
        return refreshToken(platform, tenantId, refresh_token, null, TokenUtil.DEFAULT_EXPIRE);
    }

    /**
     *
     * @param platform      平台类型
     * @param tenantId      租户id
     * @param refresh_token refresh_token
     * @param userInfo      用户信息
     * @return
     * @throws CoreException
     */
    @Override
    public UserToken refreshToken(PlatformEnum platform, String tenantId, String refresh_token, String userInfo) throws CoreException  {
        return refreshToken(platform, tenantId, refresh_token, userInfo, TokenUtil.DEFAULT_EXPIRE);
    }

    /**
     *
     * @param platform      平台类型
     * @param tenantId      租户id
     * @param refresh_token refresh_token
     * @param userInfo      用户信息
     * @param expire        token过期时间,单位秒
     * @return
     * @throws CoreException
     */
    @Override
    public UserToken refreshToken(PlatformEnum platform, String tenantId, String refresh_token, String userInfo, long expire) throws CoreException {
        return refreshToken(platform, tenantId, refresh_token, userInfo, null, null, expire);
    }

    /**
     *
     * @param platform      平台类型
     * @param tenantId      租户id
     * @param refresh_token refresh_token
     * @param userInfo      用户信息
     * @param permissions   权限
     * @param roles         角色
     * @param expire        token过期时间,单位秒
     * @return
     * @throws CoreException
     */
    @Override
    public UserToken refreshToken(PlatformEnum platform, String tenantId, String refresh_token, String userInfo, String[] permissions, String[] roles, long expire) throws CoreException {
        String tokenKey = getTokenKey();
        logger.debug("TOKEN_KEY: " + tokenKey);
        tenantId = Func.toStr(tenantId, HeaderConstant.DEFAULT_TENANT_ID);
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
            UserToken refreshUserToken = findRefreshToken(platform, tenantId, userId, refresh_token);
            if (refreshUserToken == null) {
                throw new ErrorTokenException();
            }
            // 生成新的token
            UserToken userToken = TokenUtil.buildToken(platform, tenantId, userId, expire, null, TokenUtil.parseHexKey(tokenKey), false);
            userToken.setRoles(roles);
            if(Func.isNotEmpty(userInfo)){
                userToken.setUserInfo(userInfo);
            }
            userToken.setPermissions(permissions);
            userToken.setRefreshToken(refresh_token);
            userToken.setRefreshTokenExpireTime(refreshUserToken.getRefreshTokenExpireTime());
            if (storeToken(platform, tenantId, userToken) > 0) {
                return userToken;
            }
        }
        return null;
    }

    /**
     *
     * @param maxToken
     */
    @Override
    public void setMaxToken(Integer maxToken) {
        this.maxToken = maxToken;
    }

    /**
     *
     * @param findRolesSql
     */
    @Override
    public void setFindRolesSql(String findRolesSql) {
        this.findRolesSql = findRolesSql;
    }

    /**
     *
     * @param findPermissionsSql
     */
    @Override
    public void setFindPermissionsSql(String findPermissionsSql) {
        this.findPermissionsSql = findPermissionsSql;
    }

    /**
     *
     * @return
     */
    @Override
    public Integer getMaxToken() {
        return maxToken;
    }

    /**
     *
     * @return
     */
    @Override
    public String getFindRolesSql() {
        return findRolesSql;
    }

    /**
     *
     * @return
     */
    @Override
    public String getFindPermissionsSql() {
        return findPermissionsSql;
    }

}
