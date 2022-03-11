package cn.flood.jwtp.provider;

import cn.flood.Func;
import cn.flood.UserToken;
import cn.flood.jwtp.enums.PlatformEnum;
import cn.flood.lang.CollectionUtil;
import cn.flood.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;
import cn.flood.jwtp.util.TokenUtil;

import javax.annotation.PostConstruct;
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
@SuppressWarnings("unchecked")
public class JdbcTokenStore extends TokenStoreAbstract {
    private final JdbcTemplate jdbcTemplate;
    private RowMapper<UserToken> rowMapper = new TokenRowMapper();
    // sql
    private static final String UPDATE_FIELDS = "user_id, access_token, refresh_token, expire_time, refresh_token_expire_time, roles, permissions, info, platform, tenant_id";
    private static final String BASE_SELECT = "select token_id, " + UPDATE_FIELDS + " from oauth_token";
    // 查询用户的某个token
    private static final String SQL_SELECT_BY_TOKEN = BASE_SELECT + " where user_id = ?  and platform = ? and tenant_id = ? order by create_time desc";
    // 查询某个用户的全部token
    private static final String SQL_SELECT_BY_USER_ID = BASE_SELECT + " where user_id = ? and platform = ? and tenant_id = ? order by create_time";
    // 插入token
    private static final String SQL_INSERT = "insert into oauth_token (" + UPDATE_FIELDS + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    // 删除某个用户指定token
    private static final String SQL_DELETE = "delete from oauth_token where user_id = ? and access_token = ? and platform = ?  and tenant_id = ?";
    // 删除某个用户全部token
    private static final String SQL_DELETE_BY_USER_ID = "delete from oauth_token where user_id = ? and platform = ?  and tenant_id = ?";
    // 修改某个用户的角色
    private static final String SQL_UPDATE_ROLES = "update oauth_token set roles = ? where user_id = ? and platform = ?  and tenant_id = ?";
    // 修改某个用户的权限
    private static final String SQL_UPDATE_PERMS = "update oauth_token set permissions = ? where user_id = ? and platform = ?  and tenant_id = ?";
    // 修改某个用户的信息
    private static final String SQL_UPDATE_INFO = "update oauth_token set info = ? where user_id = ? and platform = ?  and tenant_id = ?";
    // 查询某个用户的refresh_token
    private static final String SQL_SELECT_REFRESH_TOKEN = BASE_SELECT + " where user_id = ? and platform = ?  and tenant_id = ? order by create_time desc";
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
    public int storeToken(PlatformEnum platform, String tenantId, UserToken userToken) {
        List<Object> objects = getFieldsForUpdate(userToken, platform);
        int rs = jdbcTemplate.update(SQL_INSERT, StringUtils.objectListToArray(objects));
        // 限制用户的最大token数量
        if (getMaxToken() != -1) {
            List<UserToken> userUserTokens = findTokensByUserId(platform, tenantId, userToken.getUserId());
            if (userUserTokens.size() > getMaxToken()) {
                for (int i = 0; i < userUserTokens.size() - getMaxToken(); i++) {
                    removeToken(platform, tenantId, userToken.getUserId(), userUserTokens.get(i).getAccessToken());
                }
            }
        }
        return rs;
    }

    @Override
    public UserToken findToken(PlatformEnum platform, String tenantId, String userId, String access_token) {
        try {
            List<UserToken> list = (List)jdbcTemplate.queryForList(SQL_SELECT_BY_TOKEN, rowMapper, userId, platform.getType(),  tenantId);
            if(CollectionUtil.isNotEmpty(list)){
                return list.stream().filter(u -> access_token.equals(u.getAccessToken())).findFirst().orElse(null);
            }
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }

    @Override
    public List<UserToken> findTokensByUserId(PlatformEnum platform, String tenantId, String userId) {
        try {
            return jdbcTemplate.query(SQL_SELECT_BY_USER_ID, rowMapper, userId, platform.getType(), tenantId);
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }

    @Override
    public UserToken findRefreshToken(PlatformEnum platform, String tenantId, String userId, String refresh_token) {
        try {
            List<UserToken> list = jdbcTemplate.query(SQL_SELECT_REFRESH_TOKEN, rowMapper, userId, platform.getType(), tenantId);

            if (CollectionUtil.isNotEmpty(list)) {
                return list.stream().filter(u -> refresh_token.equals(u.getRefreshToken())).findFirst().orElse(null);
            }
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }

    @Override
    public int removeToken(PlatformEnum platform, String tenantId, String userId, String access_token) {
        return jdbcTemplate.update(SQL_DELETE, userId, access_token, platform.getType(), tenantId);
    }

    @Override
    public int removeTokensByUserId(PlatformEnum platform, String tenantId, String userId) {
        return jdbcTemplate.update(SQL_DELETE_BY_USER_ID, userId, platform.getType(), tenantId);
    }

    @Override
    public int updateRolesByUserId(PlatformEnum platform, String tenantId, String userId, String[] roles) {
        String rolesJson = Func.toJson(roles);
        return jdbcTemplate.update(SQL_UPDATE_ROLES, rolesJson, userId, platform.getType(), tenantId);
    }

    @Override
    public int updatePermissionsByUserId(PlatformEnum platform, String tenantId, String userId, String[] permissions) {
        String permJson = Func.toJson(permissions);
        return jdbcTemplate.update(SQL_UPDATE_PERMS, permJson, userId, platform.getType(), tenantId);
    }

    @Override
    public int updateUserInfoByUserId(PlatformEnum platform, String tenantId, String userId, String userInfo) {
        return jdbcTemplate.update(SQL_UPDATE_INFO, userInfo, userId, platform.getType(), tenantId);
    }

    @Override
    public String[] findRolesByUserId(PlatformEnum platform, String tenantId, String userId, UserToken userToken) {
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
            }, userId, platform.getType(), tenantId);
            return StringUtils.stringListToArray(roleList);
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }

    @Override
    public String[] findPermissionsByUserId(PlatformEnum platform, String tenantId, String userId, UserToken userToken) {
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
            }, userId, platform.getType(), tenantId);
            return StringUtils.stringListToArray(permList);
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }

    /**
     * 插入、修改操作的sql参数
     */
    private List<Object> getFieldsForUpdate(UserToken userToken, PlatformEnum platform) {
        List<Object> objects = new ArrayList();
        objects.add(userToken.getUserId());  // userId
        objects.add(userToken.getAccessToken());  // userToken
        objects.add(userToken.getRefreshToken());  // refresh_token
        objects.add(userToken.getExpireTime());  // expire_time
        objects.add(userToken.getRefreshTokenExpireTime());  // refresh_expire_time
        objects.add(Func.toJson(userToken.getRoles()));  // roles
        objects.add(Func.toJson(userToken.getPermissions()));  // permissions
        objects.add(userToken.getUserInfo());  // user_info
        objects.add(platform.getType());  //平台
        objects.add(userToken.getTenantId());  //租户id
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
//            String platform = rs.getString("platform");
            String tenant_id = rs.getString("tenant_id");
            UserToken userToken = new UserToken();
            userToken.setTokenId(token_id);
            userToken.setUserId(user_id);
            userToken.setAccessToken(access_token);
            userToken.setRefreshToken(refresh_token);
            userToken.setExpireTime(expire_time);
            userToken.setUserInfo(info);
            userToken.setRefreshTokenExpireTime(refresh_token_expire_time);
            userToken.setRoles(StringUtils.stringListToArray(Func.parseList(roles, String.class)));
            userToken.setPermissions(StringUtils.stringListToArray(Func.parseList(permissions, String.class)));
            userToken.setTenantId(tenant_id);
            return userToken;
        }

        private Date timestampToDate(Timestamp timestamp) {
            if (timestamp != null) {
                return new Date(timestamp.getTime());
            }
            return null;
        }
    }

    @PostConstruct
    public void init() {
        String sql = "CREATE TABLE IF NOT EXISTS `oauth_token`  (\n" +
                "  `token_id` int(32) NOT NULL AUTO_INCREMENT COMMENT '自增 id',\n" +
                "  `platform` int(1) NOT NULL COMMENT '平台类型',\n" +
                "  `tenant_id` varchar(128) NOT NULL COMMENT '租户id',\n" +
                "  `user_id` varchar(128) NOT NULL COMMENT '用户id',\n" +
                "  `access_token` varchar(128) NOT NULL COMMENT '用户token',\n" +
                "  `refresh_token` varchar(128) DEFAULT NULL COMMENT '刷新token',\n" +
                "  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',\n" +
                "  `refresh_token_expire_time` datetime DEFAULT NULL COMMENT '刷新token过期时间',\n" +
                "  `roles` varchar(512) DEFAULT NULL COMMENT '角色',\n" +
                "  `permissions` varchar(512) DEFAULT NULL COMMENT '权限',\n" +
                "  `info` varchar(512) DEFAULT NULL COMMENT '用户信息',\n" +
                "  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',\n" +
                "  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  PRIMARY KEY(token_id)," +
                "  KEY `index_oauth_platform` (`platform`)," +
                "  KEY `index_oauth_tenantid` (`tenant_id`)," +
                "  KEY `index_oauth_userid` (`user_id`)," +
                "  KEY `index_oauth_access` (`access_token`)" +
                ")COMMENT='oauth_token信息表',ENGINE = INNODB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;";
        this.jdbcTemplate.execute(sql);
        sql = "CREATE TABLE IF NOT EXISTS `oauth_token_key`  (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增 id',\n" +
                "  `token_key` varchar(128) NOT NULL COMMENT '生成token时的key',\n" +
                "  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  PRIMARY KEY(id)" +
                ")COMMENT='oauth_key信息表',ENGINE = INNODB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;";
        this.jdbcTemplate.execute(sql);
    }

}
