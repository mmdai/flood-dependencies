package cn.flood.jwtp.provider;

import cn.flood.Func;
import cn.flood.UserToken;
import cn.flood.constants.HeaderConstant;
import cn.flood.jwtp.enums.PlatformEnum;
import cn.flood.lang.StringPool;
import cn.flood.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import cn.flood.jwtp.util.TokenUtil;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis存储token的实现
 */
public class RedisTokenStore extends TokenStoreAbstract {

    private static final String KEY_TOKEN_KEY = "oauth_token_key";  //tokenKey存储的key

    private static final String KEY_PRE_TOKEN = "flood:oauth_token:";  //token存储的key前缀

    private static final String KEY_PRE_REFRESH_TOKEN = "flood:oauth_refresh_token:";  //refresh_token存储的key前缀

    private static final String KEY_PRE_ROLE = "flood:oauth_role:";  //角色存储的key前缀

    private static final String KEY_PRE_PERM = "flood:oauth_prem:";  //权限存储的key前缀

    private static final String KEY_PRE_INFO = "flood:oauth_info:";  //信息存储的key前缀

    private static final TimeUnit timeUnit = TimeUnit.SECONDS; //redis过期单位

    private final StringRedisTemplate redisTemplate;

    private final Object jdbcTemplate;

    public RedisTokenStore(StringRedisTemplate redisTemplate) {
        this(redisTemplate, null);
    }

    public RedisTokenStore(StringRedisTemplate redisTemplate, DataSource dataSource) {
        Assert.notNull(redisTemplate, "StringRedisTemplate required");
        this.redisTemplate = redisTemplate;
        if (dataSource != null) {
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        } else {
            this.jdbcTemplate = null;
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String getTokenKey() {
        if (mTokenKey == null) {
            mTokenKey = redisTemplate.opsForValue().get(KEY_TOKEN_KEY);
            if (mTokenKey == null || mTokenKey.trim().isEmpty()) {
                mTokenKey = TokenUtil.getHexKey();
                redisTemplate.opsForValue().set(KEY_TOKEN_KEY, mTokenKey);
            }
        }
        return mTokenKey;
    }

    /**
     *
     * @param platform  平台类型
     * @param tenantId  租户id
     * @param userToken
     * @return
     */
    @Override
    public int storeToken(PlatformEnum platform, String tenantId, UserToken userToken) {
        //key = 平台 + 租户 + 用户
        String key = getKey(platform, tenantId, userToken.getUserId());
        // 存储access_token
        redisTemplate.opsForList().rightPush(KEY_PRE_TOKEN + key, userToken.getAccessToken());
        redisTemplate.expire(KEY_PRE_TOKEN + key, userToken.getExpireSecond(), timeUnit);
        // 存储refresh_token
        if (userToken.getRefreshToken() != null && findRefreshToken(platform, tenantId, userToken.getUserId(), userToken.getRefreshToken()) == null) {
            redisTemplate.opsForList().rightPush(KEY_PRE_REFRESH_TOKEN + key, userToken.getRefreshToken());
            redisTemplate.expire(KEY_PRE_REFRESH_TOKEN + key, userToken.getRefreshTokenExpireSecond(), timeUnit);
        }
        // 存储角色
        updateRolesByUserId(platform, tenantId, userToken.getUserId(), userToken.getRoles());
        if(Func.isNotEmpty(userToken.getRoles())){
            redisTemplate.expire(KEY_PRE_ROLE  + key, userToken.getExpireSecond(), timeUnit);
        }
        // 存储权限
        updatePermissionsByUserId(platform, tenantId, userToken.getUserId(), userToken.getPermissions());
        if(Func.isNotEmpty(userToken.getPermissions())){
            redisTemplate.expire(KEY_PRE_PERM  + key, userToken.getExpireSecond(), timeUnit);
        }
        //存储用户信息
        updateUserInfoByUserId(platform, tenantId, userToken.getUserId(), userToken.getUserInfo());
        if(Func.isNotEmpty(userToken.getUserInfo())){
            redisTemplate.expire(KEY_PRE_INFO  + key, userToken.getExpireSecond(), timeUnit);
        }
        // 限制用户的最大token数量
        if (getMaxToken() != -1) {
            listMaxLimit(KEY_PRE_TOKEN + key, getMaxToken());
            listMaxLimit(KEY_PRE_REFRESH_TOKEN + key, getMaxToken());
        }
        return 1;
    }

    /**
     *
     * @param platform     平台类型
     * @param tenantId     租户id
     * @param userId       用户id
     * @param access_token
     * @return
     */
    @Override
    public UserToken findToken(PlatformEnum platform, String tenantId, String userId, String access_token) {
        List<UserToken> userTokens = findTokensByUserId(platform, tenantId, userId);
        if (userTokens != null && access_token != null) {
            for (UserToken userToken : userTokens) {
                if (access_token.equals(userToken.getAccessToken())) {
                    return userToken;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param platform  平台类型
     * @param tenantId  租户id
     * @param userId    用户id
     * @return
     */
    @Override
    public List<UserToken> findTokensByUserId(PlatformEnum platform, String tenantId, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }
        List<UserToken> userTokens = new ArrayList<>();
        //key = 平台 + 租户 + 用户
        String key = getKey(platform, tenantId, userId);
        List<String> accessTokens = redisTemplate.opsForList().range(KEY_PRE_TOKEN + key, 0, -1);
        if (accessTokens != null && accessTokens.size() > 0) {
            for (String accessToken : accessTokens) {
                UserToken userToken = new UserToken();
                userToken.setUserId(userId);
                userToken.setAccessToken(accessToken);
                // 查询权限
                userToken.setPermissions(StringUtils.stringSetToArray(redisTemplate.opsForSet().members(KEY_PRE_PERM + key)));
                // 查询角色
                userToken.setRoles(StringUtils.stringSetToArray(redisTemplate.opsForSet().members(KEY_PRE_ROLE + key)));
                // 查询信息
                String info = redisTemplate.opsForValue().get(KEY_PRE_INFO + key);
                if(Func.isNotEmpty(info)){
                    userToken.setUserInfo(info);
                }
                userTokens.add(userToken);
            }
        }
        return userTokens;
    }

    /**
     *
     * @param platform      平台类型
     * @param tenantId      租户id
     * @param userId        用户id
     * @param refresh_token
     * @return
     */
    @Override
    public UserToken findRefreshToken(PlatformEnum platform, String tenantId, String userId, String refresh_token) {
        if (userId != null && !userId.trim().isEmpty() && refresh_token != null) {
            //key = 平台 + 租户 + 用户
            String key = getKey(platform, tenantId, userId);
            List<String> refreshTokens = redisTemplate.opsForList().range(KEY_PRE_REFRESH_TOKEN + key, 0, -1);
            for (String refreshToken : refreshTokens) {
                if (refresh_token.equals(refreshToken)) {
                    UserToken userToken = new UserToken();
                    userToken.setUserId(userId);
                    userToken.setRefreshToken(refresh_token);
                    return userToken;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param platform     平台类型
     * @param tenantId     租户id
     * @param userId       用户id
     * @param access_token
     * @return
     */
    @Override
    public int removeToken(PlatformEnum platform, String tenantId, String userId, String access_token) {
        //key = 平台 + 租户 + 用户
        String key = getKey(platform, tenantId, userId);
        redisTemplate.opsForList().remove(KEY_PRE_TOKEN + key, 0, access_token);
        return 1;
    }

    /**
     *
     * @param platform  平台类型
     * @param tenantId  租户id
     * @param userId    用户id
     * @return
     */
    @Override
    public int removeTokensByUserId(PlatformEnum platform, String tenantId, String userId) {
        //key = 平台 + 租户 + 用户
        String key = getKey(platform, tenantId, userId);
        redisTemplate.delete(KEY_PRE_TOKEN + key);
        // 删除角色、权限、信息、refresh_token
        redisTemplate.delete(KEY_PRE_ROLE + key);
        redisTemplate.delete(KEY_PRE_PERM + key);
        redisTemplate.delete(KEY_PRE_INFO + key);
        redisTemplate.delete(KEY_PRE_REFRESH_TOKEN + key);
        return 1;
    }

    /**
     *
     * @param platform  平台类型
     * @param tenantId  租户id
     * @param userId    用户id
     * @param roles     角色
     * @return
     */
    @Override
    public int updateRolesByUserId(PlatformEnum platform, String tenantId, String userId, String[] roles) {
        String roleKey = KEY_PRE_ROLE  + getKey(platform, tenantId, userId);
        redisTemplate.delete(roleKey);
        if (Func.isNotEmpty(roles)) {
            redisTemplate.opsForSet().add(roleKey, roles);
        }
        return 1;
    }

    /**
     *
     * @param platform  平台类型
     * @param tenantId  租户id
     * @param userId      用户id
     * @param permissions 权限
     * @return
     */
    @Override
    public int updatePermissionsByUserId(PlatformEnum platform, String tenantId, String userId, String[] permissions) {
        String permKey = KEY_PRE_PERM + getKey(platform, tenantId, userId);
        redisTemplate.delete(permKey);
        if (Func.isNotEmpty(permissions)) {
            redisTemplate.opsForSet().add(permKey, permissions);
        }
        return 1;
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
    public int updateUserInfoByUserId(PlatformEnum platform, String tenantId, String userId, String userInfo) {
        String infoKey = KEY_PRE_INFO + getKey(platform, tenantId, userId);
        redisTemplate.delete(infoKey);
        if (!Func.isEmpty(userInfo)) {
            redisTemplate.opsForValue().set(infoKey, userInfo);
        }
        return 1;
    }

    /**
     *
     * @param platform  平台类型
     * @param tenantId  租户id
     * @param userId    用户id
     * @param userToken
     * @return
     */
    @Override
    public String[] findRolesByUserId(PlatformEnum platform, String tenantId, String userId, UserToken userToken) {
        // 判断是否自定义查询
        if (getFindRolesSql() == null || getFindRolesSql().trim().isEmpty()) {
            return userToken.getRoles();
        }
        if (jdbcTemplate != null) {
            try {
                List<String> roleList = ((org.springframework.jdbc.core.JdbcTemplate) jdbcTemplate).query(getFindRolesSql(), new org.springframework.jdbc.core.RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString(1);
                    }
                }, platform.getType(), tenantId, userId);
                return StringUtils.stringListToArray(roleList);
            } catch (EmptyResultDataAccessException e) {
            }
        }
        return null;
    }

    /**
     *
     * @param platform  平台类型
     * @param tenantId  租户id
     * @param userId    用户id
     * @param userToken
     * @return
     */
    @Override
    public String[] findPermissionsByUserId(PlatformEnum platform, String tenantId, String userId, UserToken userToken) {
        // 判断是否自定义查询
        if (getFindPermissionsSql() == null || getFindPermissionsSql().trim().isEmpty()) {
            return userToken.getPermissions();
        }
        if (jdbcTemplate != null) {
            try {
                List<String> permList = ((JdbcTemplate) jdbcTemplate).query(getFindPermissionsSql(), new org.springframework.jdbc.core.RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString(1);
                    }
                }, platform.getType(), tenantId, userId);
                return StringUtils.stringListToArray(permList);
            } catch (EmptyResultDataAccessException e) {
            }
        }
        return null;
    }

    /**
     * 限制list的最大数量
     */
    private void listMaxLimit(String key, int max) {
        Long userTokenSize = redisTemplate.opsForList().size(key);
        if (userTokenSize > max) {
            for (int i = 0; i < userTokenSize - max; i++) {
                redisTemplate.opsForList().leftPop(key);
            }
        }
    }

    /**
     * key = 平台 + 租户 + 用户
     * @param platform
     * @param tenantId
     * @param userId
     * @return
     */
    private String getKey(PlatformEnum platform, String tenantId, String userId){
        return (platform.getType()+ StringPool.COLON + Func.toStr(tenantId, HeaderConstant.DEFAULT_TENANT_ID)+ StringPool.COLON + userId);
    }

}
