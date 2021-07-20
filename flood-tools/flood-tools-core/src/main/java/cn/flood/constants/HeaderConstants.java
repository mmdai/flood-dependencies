package cn.flood.constants;

/**
 *
 */
public class HeaderConstants {
    // 请求id
    public final static String REQUEST_ID = "requestId";
    // 调用ids
    public final static String CALL_IDS = "callIds";

    // 用户访问token
    public final static String USER_ACCESS_TOKEN = "USER-ACCESS-TOKEN";
    // 用户随机吗
    public final static String USER_UUID = "USER-UUID";
    // 租户成员访问token
    public final static String MEMBER_ACCESS_TOKEN = "MEMBER-ACCESS-TOKEN";
    // 租户成员随机码
    public final static String MEMBER_UUID = "MEMBER-UUID";
    // 运营/运维管理员访问token
    public final static String ADMIN_ACCESS_TOKEN = "ADMIN-ACCESS-TOKEN";
    // 运营/运维管理员随机码
    public final static String ADMIN_UUID = "ADMIN-UUID";

    // 登录租户代码
    public final static String LOGIN_TENANT_CODE = "LOGIN-TENANT-CODE";

    // 访问来源
    public final static String ACCESS_SOURCE = "ACCESS-SOURCE";
    public final static String HEADER_ACCESS_SOURCE = "nc-access-source";
    // 访问来源-nginx
    public static final String NC_NGINX = "NC-NGINX";
    // rsa标志
    public static final String RSA_TAG = "RSA-TAG";
    // 图片验证码sessionId
    public static final String IMAGE_CERT_SESSION_ID = "imageCertSessionId";
    // 访问版本号
    public final static String HEADER_VERSION = "flood-api-version";
}
