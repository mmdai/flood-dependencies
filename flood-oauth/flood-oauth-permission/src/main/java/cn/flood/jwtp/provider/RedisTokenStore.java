package cn.flood.jwtp.provider;

import cn.flood.Func;
import cn.flood.UserToken;
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

    @Override
    public int storeToken(UserToken userToken) {
        // 存储access_token
        redisTemplate.opsForList().rightPush(KEY_PRE_TOKEN + userToken.getUserId(), userToken.getAccessToken());
        // 存储refresh_token
        if (userToken.getRefreshToken() != null && findRefreshToken(userToken.getUserId(), userToken.getRefreshToken()) == null) {
            redisTemplate.opsForList().rightPush(KEY_PRE_REFRESH_TOKEN + userToken.getUserId(), userToken.getRefreshToken());
        }
        // 存储角色
        updateRolesByUserId(userToken.getUserId(), userToken.getRoles());
        // 存储权限
        updatePermissionsByUserId(userToken.getUserId(), userToken.getPermissions());
        //存储用户信息
        updateUserInfoByUserId(userToken.getUserId(), userToken.getUserInfo());
        // 限制用户的最大token数量
        if (getMaxToken() != -1) {
            listMaxLimit(KEY_PRE_TOKEN + userToken.getUserId(), getMaxToken());
            listMaxLimit(KEY_PRE_REFRESH_TOKEN + userToken.getUserId(), getMaxToken());
        }
        return 1;
    }

    @Override
    public UserToken findToken(String userId, String access_token) {
        List<UserToken> userTokens = findTokensByUserId(userId);
        if (userTokens != null && access_token != null) {
            for (UserToken userToken : userTokens) {
                if (access_token.equals(userToken.getAccessToken())) {
                    return userToken;
                }
            }
        }
        return null;
    }

    @Override
    public List<UserToken> findTokensByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }
        List<UserToken> userTokens = new ArrayList<>();
        List<String> accessTokens = redisTemplate.opsForList().range(KEY_PRE_TOKEN + userId, 0, -1);
        if (accessTokens != null && accessTokens.size() > 0) {
            for (String accessToken : accessTokens) {
                UserToken userToken = new UserToken();
                userToken.setUserId(userId);
                userToken.setAccessToken(accessToken);
                // 查询权限
                userToken.setPermissions(StringUtils.stringSetToArray(redisTemplate.opsForSet().members(KEY_PRE_PERM + userId)));
                // 查询角色
                userToken.setRoles(StringUtils.stringSetToArray(redisTemplate.opsForSet().members(KEY_PRE_ROLE + userId)));
                // 查询信息
                String info = redisTemplate.opsForValue().get(KEY_PRE_INFO + userId);
                if(Func.isNotEmpty(info)){
                    userToken.setUserInfo(info);
                }
                userTokens.add(userToken);
            }
        }
        return userTokens;
    }

    @Override
    public UserToken findRefreshToken(String userId, String refresh_token) {
        if (userId != null && !userId.trim().isEmpty() && refresh_token != null) {
            List<String> refreshTokens = redisTemplate.opsForList().range(KEY_PRE_REFRESH_TOKEN + userId, 0, -1);
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

    @Override
    public int removeToken(String userId, String access_token) {
        redisTemplate.opsForList().remove(KEY_PRE_TOKEN + userId, 0, access_token);
        return 1;
    }

    @Override
    public int removeTokensByUserId(String userId) {
        redisTemplate.delete(KEY_PRE_TOKEN + userId);
        // 删除角色、权限、信息、refresh_token
        redisTemplate.delete(KEY_PRE_ROLE + userId);
        redisTemplate.delete(KEY_PRE_PERM + userId);
        redisTemplate.delete(KEY_PRE_INFO + userId);
        redisTemplate.delete(KEY_PRE_REFRESH_TOKEN + userId);
        return 1;
    }

    @Override
    public int updateRolesByUserId(String userId, String[] roles) {
        String roleKey = KEY_PRE_ROLE + userId;
        redisTemplate.delete(roleKey);
        if (roles != null && roles.length > 0) {
            redisTemplate.opsForSet().add(roleKey, roles);
        }
        return 1;
    }

    @Override
    public int updatePermissionsByUserId(String userId, String[] permissions) {
        String permKey = KEY_PRE_PERM + userId;
        redisTemplate.delete(permKey);
        if (permissions != null && permissions.length > 0) {
            redisTemplate.opsForSet().add(permKey, permissions);
        }
        return 1;
    }

    @Override
    public int updateUserInfoByUserId(String userId, String userInfo) {
        String infoKey = KEY_PRE_INFO + userId;
        redisTemplate.delete(infoKey);
        if (!Func.isEmpty(userInfo)) {
            redisTemplate.opsForValue().set(infoKey, userInfo);
        }
        return 1;
    }

    @Override
    public String[] findRolesByUserId(String userId, UserToken userToken) {
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
                }, userId);
                return StringUtils.stringListToArray(roleList);
            } catch (EmptyResultDataAccessException e) {
            }
        }
        return null;
    }

    @Override
    public String[] findPermissionsByUserId(String userId, UserToken userToken) {
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
                }, userId);
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

}
