package cn.flood.constants;

/**
 *
 */
public interface HeaderConstant {
    // 请求id
    String REQUEST_ID = "requestId";
    // 调用ids
    String CALL_IDS = "callIds";

    // 用户访问token
    String USER_ACCESS_TOKEN = "USER-ACCESS-TOKEN";
    // 用户随机吗
    String USER_UUID = "USER-UUID";
    // 租户成员访问token
    String MEMBER_ACCESS_TOKEN = "MEMBER-ACCESS-TOKEN";
    // 租户成员随机码
    String MEMBER_UUID = "MEMBER-UUID";
    // 运营/运维管理员访问token
    String ADMIN_ACCESS_TOKEN = "ADMIN-ACCESS-TOKEN";
    // 运营/运维管理员随机码
    String ADMIN_UUID = "ADMIN-UUID";

    // 登录租户代码
    String LOGIN_TENANT_CODE = "LOGIN-TENANT-CODE";

    // 访问来源
    String ACCESS_SOURCE = "ACCESS-SOURCE";
    String HEADER_ACCESS_SOURCE = "nc-access-source";
    // 访问来源-nginx
    public static final String NC_NGINX = "NC-NGINX";
    // rsa标志
    public static final String RSA_TAG = "RSA-TAG";
    // 图片验证码sessionId
    public static final String IMAGE_CERT_SESSION_ID = "imageCertSessionId";
    // 访问版本号
    String HEADER_VERSION = "flood-api-version";
}
