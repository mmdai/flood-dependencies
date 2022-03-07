package cn.flood.jwtp.provider;

import cn.flood.UserToken;
import cn.flood.exception.CoreException;
import cn.flood.jwtp.enums.PlatformEnum;

import java.util.List;

/**
 * 操作token的接口
 */
public interface TokenStore {

    /**
     * 获取生成token用的key
     *
     * @return
     */
    String getTokenKey();

    /**
     * 创建新的token
     *
     * @param platform  平台类型
     * @param userId    用户id
     * @return
     */
    UserToken createNewToken(PlatformEnum platform, String userId);

    /**
     * 创建新的token
     *
     * @param platform  平台类型
     * @param userId    用户id
     * @param userInfo  用户信息
     * @return
     */
    UserToken createNewToken(PlatformEnum platform, String userId, String userInfo);

    /**
     * 创建新的token
     *
     * @param platform  平台类型
     * @param userId    用户id
     * @param userInfo  用户信息
     * @param expire    token过期时间,单位秒
     * @return
     */
    UserToken createNewToken(PlatformEnum platform, String userId, String userInfo, long expire);


    /**
     * 创建新的token
     *
     * @param platform  平台类型
     * @param userId    用户id
     * @param userInfo  用户信息
     * @param expire    token过期时间,单位秒
     * @param rtExpire  refresh_token过期时间,单位秒
     * @return
     */
    UserToken createNewToken(PlatformEnum platform, String userId, String userInfo, long expire, long rtExpire);


    /**
     * 创建新的token
     *
     * @param platform    平台类型
     * @param userId      用户id
     * @param userInfo    用户信息
     * @param permissions 权限
     * @param roles       角色
     * @return
     */
    UserToken createNewToken(PlatformEnum platform, String userId, String userInfo, String[] permissions, String[] roles);

    /**
     * 创建新的token
     *
     * @param platform    平台类型
     * @param userId      用户id
     * @param userInfo    用户信息
     * @param permissions 权限
     * @param roles       角色
     * @param expire      token过期时间,单位秒
     * @return
     */
    UserToken createNewToken(PlatformEnum platform, String userId, String userInfo, String[] permissions, String[] roles, long expire);

    /**
     * 创建新的token
     *
     * @param platform    平台类型
     * @param userId      用户id
     * @param userInfo    用户信息
     * @param permissions 权限
     * @param roles       角色
     * @param expire      token过期时间,单位秒
     * @param rtExpire    refresh_token过期时间,单位秒
     * @param rtExpire
     * @return
     */
    UserToken createNewToken(PlatformEnum platform, String userId, String userInfo, String[] permissions, String[] roles, long expire, long rtExpire);

    /**
     * 刷新token
     *
     * @param platform      平台类型
     * @param refresh_token refresh_token
     * @return
     */
    UserToken refreshToken(PlatformEnum platform, String refresh_token) throws CoreException;
    /**
     * 刷新token
     *
     * @param platform      平台类型
     * @param refresh_token refresh_token
     * @param userInfo      用户信息
     * @return
     */
    UserToken refreshToken(PlatformEnum platform, String refresh_token, String userInfo) throws CoreException;

    /**
     * 刷新token
     *
     * @param platform      平台类型
     * @param refresh_token refresh_token
     * @param userInfo      用户信息
     * @param expire        token过期时间,单位秒
     * @return
     */
    UserToken refreshToken(PlatformEnum platform, String refresh_token, String userInfo, long expire) throws CoreException;

    /**
     * 刷新token
     *
     * @param platform      平台类型
     * @param refresh_token refresh_token
     * @param userInfo      用户信息
     * @param permissions   权限
     * @param roles         角色
     * @param expire        token过期时间,单位秒
     * @return
     */
    UserToken refreshToken(PlatformEnum platform, String refresh_token, String userInfo, String[] permissions, String[] roles, long expire) throws CoreException;

    /**
     * 保存Token
     *
     * @param platform    平台类型
     * @param userToken
     * @return
     */
    int storeToken(PlatformEnum platform, UserToken userToken);

    /**
     * 查询用户的某个token
     *
     * @param platform     平台类型
     * @param userId       用户id
     * @param access_token
     * @return
     */
    UserToken findToken(PlatformEnum platform, String userId, String access_token);

    /**
     * 查询用户的全部token
     *
     * @param platform  平台类型
     * @param userId    用户id
     * @return
     */
    List<UserToken> findTokensByUserId(PlatformEnum platform, String userId);

    /**
     * 查询用户的某个refresh_token
     *
     * @param platform      平台类型
     * @param userId        用户id
     * @param refresh_token
     * @return
     */
    UserToken findRefreshToken(PlatformEnum platform, String userId, String refresh_token);

    /**
     * 移除用户的某个token
     *
     * @param platform     平台类型
     * @param userId       用户id
     * @param access_token
     * @return
     */
    int removeToken(PlatformEnum platform, String userId, String access_token);

    /**
     * 移除用户的全部token
     *
     * @param platform  平台类型
     * @param userId    用户id
     * @return
     */
    int removeTokensByUserId(PlatformEnum platform, String userId);

    /**
     * 修改某个用户的角色
     *
     * @param platform  平台类型
     * @param userId    用户id
     * @param roles     角色
     * @return
     */
    int updateRolesByUserId(PlatformEnum platform, String userId, String[] roles);

    /**
     * 修改某个用户的权限
     *
     * @param platform    平台类型
     * @param userId      用户id
     * @param permissions 权限
     * @return
     */
    int updatePermissionsByUserId(PlatformEnum platform, String userId, String[] permissions);

    /**
     * 修改某个用户的信息
     *
     * @param platform  平台类型
     * @param userId    用户id
     * @param userInfo  用户信息
     * @return
     */
    int updateUserInfoByUserId(PlatformEnum platform, String userId, String userInfo);

    /**
     * 查询用户的角色列表
     *
     * @param platform  平台类型
     * @param userId    用户id
     * @return
     */
    String[] findRolesByUserId(PlatformEnum platform, String userId, UserToken userToken);

    /**
     * 查询用户的权限列表
     *
     * @param platform  平台类型
     * @param userId    用户id
     * @return
     */
    String[] findPermissionsByUserId(PlatformEnum platform, String userId, UserToken userToken);

    /**
     * 设置单个用户最大token数量
     * @param maxToken
     */
    void setMaxToken(Integer maxToken);

    /**
     * 自定义查询角色sql
     *
     * @param findRolesSql
     */
    void setFindRolesSql(String findRolesSql);

    /**
     * 自定义查询权限sql
     *
     * @param findPermissionsSql
     */
    void setFindPermissionsSql(String findPermissionsSql);

    Integer getMaxToken();

    String getFindRolesSql();

    String getFindPermissionsSql();

}
