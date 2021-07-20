package cn.flood.constants;

/**
 * @author mmdai
 * @date 2020/8/24 17:10
 */
public class RedisConstants {
    // 管理员登陆信息
    public static final String ADMIN_LOGIN_INFO = "admin:login:info:";
    // 用户登陆信息
    public static final String USER_LOGIN_INFO = "user:login:info:";
    // 成员登录信息
    public static final String MEMBER_LOGIN_INFO = "member:login:info:";

    // 管理员登录失败次数
    public static final String ADMIN_LOGIN_FAIL_NUM = "admin:login:failNum:";
    // 管理员登录失败次数
    public static final String USER_LOGIN_FAIL_NUM = "user:login:failNum:";

    // 用户信息
    public static final String USER_ID_INFO = "user:id:";

    // appApi Cache
    public static final String APP_API_VERSION = "appApi:version";
    // appApi Cache
    public static final String APP_API_LIST = "appApi:list";
    // 产品菜单列表
    public static final String PRODUCT_MENU_LIST = "product:menu:list:";
    // 路由版本
    public static final String ROUTE_VERSION = "route:version";
    // 路由列表
    public static final String ROUTE_LIST = "route:list";
    // 配置代码
    public static final String CONFIG_CODE = "config:code:";
    // 配置分组
    public static final String CONFIG_GROUP = "config:group:";

    // rsa密钥对标识
    public static final String RSA_TAG = "rsa:tag";
    // rsa秘钥对公钥
    public static final String RSA_PUBLIC_KEY = "rsa:public:";
    // rsa密钥对私钥
    public static final String RSA_PRIVATE_KEY = "rsa:private:";

    // 文章详情标志 存固定字符f 标识是否过期 有效期是value的一半
    public static final String ARTICLE_INFO_FLAG = "article:info:flag:";
    // 文章详情value 存文章信息 有效期是flag的两倍 预防缓存击穿
    public static final String ARTICLE_INFO_VALUE = "article:info:value:";

    // 图片验证码
    public static final String IMAGE_CERT = "image:cert:";

    // 邮件验证码
    public static final String EMAIL_CERT = "email:cert:";
}
