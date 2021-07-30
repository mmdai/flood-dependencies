package cn.flood.jwtp.provider;

import cn.flood.UserToken;
import cn.flood.json.JsonUtils;
import cn.flood.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;
import cn.flood.jwtp.util.TokenUtil;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * jdbc存储token的实现
 */
public class JdbcTokenStore extends TokenStoreAbstract {
    private final JdbcTemplate jdbcTemplate;
    private RowMapper<UserToken> rowMapper = new TokenRowMapper();
    // sql
    private static final String UPDATE_FIELDS = "user_id, access_token, refresh_token, expire_time, refresh_token_expire_time, roles, permissions, info";
    private static final String BASE_SELECT = "select token_id, " + UPDATE_FIELDS + " from oauth_token";
    // 查询用户的某个token
    private static final String SQL_SELECT_BY_TOKEN = BASE_SELECT + " where user_id = ? and access_token = ?";
    // 查询某个用户的全部token
    private static final String SQL_SELECT_BY_USER_ID = BASE_SELECT + " where user_id = ? order by create_time";
    // 插入token
    private static final String SQL_INSERT = "insert into oauth_token (" + UPDATE_FIELDS + ") values (?, ?, ?, ?, ?, ?, ?, ?)";
    // 删除某个用户指定token
    private static final String SQL_DELETE = "delete from oauth_token where user_id = ? and access_token = ?";
    // 删除某个用户全部token
    private static final String SQL_DELETE_BY_USER_ID = "delete from oauth_token where user_id = ?";
    // 修改某个用户的角色
    private static final String SQL_UPDATE_ROLES = "update oauth_token set roles = ? where user_id = ?";
    // 修改某个用户的权限
    private static final String SQL_UPDATE_PERMS = "update oauth_token set permissions = ? where user_id = ?";
    // 修改某个用户的信息
    private static final String SQL_UPDATE_INFO = "update oauth_token set info = ? where user_id = ?";
    // 查询某个用户的refresh_token
    private static final String SQL_SELECT_REFRESH_TOKEN = BASE_SELECT + " where user_id = ? and refresh_token= ?";
    // 查询tokenKey
    private static final String SQL_SELECT_KEY = "select token_key from oauth_token_key";
    // 插入tokenKey
    private static final String SQL_INSERT_KEY = "insert into oauth_token_key (token_key) values (?)";

    public JdbcTokenStore(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public String getTokenKey() {
        if (mTokenKey == null) {
            try {
                mTokenKey = jdbcTemplate.queryForObject(SQL_SELECT_KEY, String.class);
            } catch (EmptyResultDataAccessException e) {
            }
            if (mTokenKey == null || mTokenKey.trim().isEmpty()) {
                mTokenKey = TokenUtil.getHexKey();
                jdbcTemplate.update(SQL_INSERT_KEY, mTokenKey);
            }
        }
        return mTokenKey;
    }

    @Override
    public int storeToken(UserToken userToken) {
        List<Object> objects = getFieldsForUpdate(userToken);
        int rs = jdbcTemplate.update(SQL_INSERT, StringUtils.objectListToArray(objects));
        // 限制用户的最大token数量
        if (getMaxToken() != -1) {
            List<UserToken> userUserTokens = findTokensByUserId(userToken.getUserId());
            if (userUserTokens.size() > getMaxToken()) {
                for (int i = 0; i < userUserTokens.size() - getMaxToken(); i++) {
                    removeToken(userToken.getUserId(), userUserTokens.get(i).getAccessToken());
                }
            }
        }
        return rs;
    }

    @Override
    public UserToken findToken(String userId, String access_token) {
        try {
            return jdbcTemplate.queryForObject(SQL_SELECT_BY_TOKEN, rowMapper, userId, access_token);
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }

    @Override
    public List<UserToken> findTokensByUserId(String userId) {
        try {
            return jdbcTemplate.query(SQL_SELECT_BY_USER_ID, rowMapper, userId);
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }

    @Override
    public UserToken findRefreshToken(String userId, String refresh_token) {
        try {
            List<UserToken> list = jdbcTemplate.query(SQL_SELECT_REFRESH_TOKEN, rowMapper, userId, refresh_token);
            if (list.size() > 0) {
                return list.get(0);
            }
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }

    @Override
    public int removeToken(String userId, String access_token) {
        return jdbcTemplate.update(SQL_DELETE, userId, access_token);
    }

    @Override
    public int removeTokensByUserId(String userId) {
        return jdbcTemplate.update(SQL_DELETE_BY_USER_ID, userId);
    }

    @Override
    public int updateRolesByUserId(String userId, String[] roles) {
        String rolesJson = JsonUtils.toJSONString(roles);
        return jdbcTemplate.update(SQL_UPDATE_ROLES, rolesJson, userId);
    }

    @Override
    public int updatePermissionsByUserId(String userId, String[] permissions) {
        String permJson = JsonUtils.toJSONString(permissions);
        return jdbcTemplate.update(SQL_UPDATE_PERMS, permJson, userId);
    }

    @Override
    public int updateUserInfoByUserId(String userId, String userInfo) {
        return jdbcTemplate.update(SQL_UPDATE_INFO, userInfo, userId);
    }

    @Override
    public String[] findRolesByUserId(String userId, UserToken userToken) {
        // 判断是否自定义查询
        if (getFindRolesSql() == null || getFindRolesSql().trim().isEmpty()) {
            return userToken.getRoles();
        }
        try {
            List<String> roleList = jdbcTemplate.query(getFindRolesSql(), new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString(1);
                }
            }, userId);
            return StringUtils.stringListToArray(roleList);
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }

    @Override
    public String[] findPermissionsByUserId(String userId, UserToken userToken) {
        // 判断是否自定义查询
        if (getFindPermissionsSql() == null || getFindPermissionsSql().trim().isEmpty()) {
            return userToken.getPermissions();
        }
        try {
            List<String> permList = jdbcTemplate.query(getFindPermissionsSql(), new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString(1);
                }
            }, userId);
            return StringUtils.stringListToArray(permList);
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }

    /**
     * 插入、修改操作的sql参数
     */
    private List<Object> getFieldsForUpdate(UserToken userToken) {
        List<Object> objects = new ArrayList();
        objects.add(userToken.getUserId());  // userId
        objects.add(userToken.getAccessToken());  // userToken
        objects.add(userToken.getRefreshToken());  // refresh_token
        objects.add(userToken.getExpireTime());  // expire_time
        objects.add(userToken.getRefreshTokenExpireTime());  // refresh_expire_time
        objects.add(JsonUtils.toJSONString(userToken.getRoles()));  // roles
        objects.add(JsonUtils.toJSONString(userToken.getPermissions()));  // permissions
        objects.add(userToken.getUserInfo());  // user_info
        return objects;
    }

    /**
     * Token结果集映射
     */
    private static class TokenRowMapper implements RowMapper<UserToken> {
        @Override
        public UserToken mapRow(ResultSet rs, int rowNum) throws SQLException {
            int token_id = rs.getInt("token_id");
            String user_id = rs.getString("user_id");
            String access_token = rs.getString("access_token");
            String refresh_token = rs.getString("refresh_token");
            Date expire_time = timestampToDate(rs.getTimestamp("expire_time"));
            Date refresh_token_expire_time = timestampToDate(rs.getTimestamp("refresh_token_expire_time"));
            String roles = rs.getString("roles");
            String permissions = rs.getString("permissions");
            String info = rs.getString("info");
            UserToken userToken = new UserToken();
            userToken.setTokenId(token_id);
            userToken.setUserId(user_id);
            userToken.setAccessToken(access_token);
            userToken.setRefreshToken(refresh_token);
            userToken.setExpireTime(expire_time);
            userToken.setUserInfo(info);
            userToken.setRefreshTokenExpireTime(refresh_token_expire_time);
            userToken.setRoles(StringUtils.stringListToArray(JsonUtils.toJavaObjectList(roles, String.class)));
            userToken.setPermissions(StringUtils.stringListToArray(JsonUtils.toJavaObjectList(permissions, String.class)));
            return userToken;
        }

        private Date timestampToDate(Timestamp timestamp) {
            if (timestamp != null) {
                return new Date(timestamp.getTime());
            }
            return null;
        }
    }

}