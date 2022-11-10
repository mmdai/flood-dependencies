package cn.flood.base.core.constants;

/**
 * @author mmdai
 * @date 2020/8/24 17:10
 */
public interface RedisConstants {
    // 管理员登陆信息
    String ADMIN_LOGIN_INFO = "admin:login:info:";
    // 用户登陆信息
    String USER_LOGIN_INFO = "user:login:info:";
    // 成员登录信息
    String MEMBER_LOGIN_INFO = "member:login:info:";

    // 管理员登录失败次数
    String ADMIN_LOGIN_FAIL_NUM = "admin:login:failNum:";
    // 管理员登录失败次数
    String USER_LOGIN_FAIL_NUM = "user:login:failNum:";

    // 用户信息
    String USER_ID_INFO = "user:id:";

    // appApi Cache
    String APP_API_VERSION = "appApi:version";
    // appApi Cache
    String APP_API_LIST = "appApi:list";
    // 产品菜单列表
    String PRODUCT_MENU_LIST = "product:menu:list:";
    // 路由版本
    String ROUTE_VERSION = "route:version";
    // 路由列表
    String ROUTE_LIST = "route:list";
    // 配置代码
    String CONFIG_CODE = "config:code:";
    // 配置分组
    String CONFIG_GROUP = "config:group:";

    // rsa密钥对标识
    String RSA_TAG = "rsa:tag";
    // rsa秘钥对公钥
    String RSA_PUBLIC_KEY = "rsa:public:";
    // rsa密钥对私钥
    String RSA_PRIVATE_KEY = "rsa:private:";

    // 文章详情标志 存固定字符f 标识是否过期 有效期是value的一半
    String ARTICLE_INFO_FLAG = "article:info:flag:";
    // 文章详情value 存文章信息 有效期是flag的两倍 预防缓存击穿
    String ARTICLE_INFO_VALUE = "article:info:value:";

    // 图片验证码
    String IMAGE_CERT = "image:cert:";

    // 邮件验证码
    String EMAIL_CERT = "email:cert:";
}
