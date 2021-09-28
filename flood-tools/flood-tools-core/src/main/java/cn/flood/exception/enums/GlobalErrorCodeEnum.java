package cn.flood.exception.enums;

import lombok.Getter;

/**
 * 全局异常enum
 */
@Getter
public enum GlobalErrorCodeEnum {
    /**
     * 参数错误
     */
    BAD_REQUEST("400", "请求参数错误", "BAD_REQUEST"),

    /**
     * 账号未登录
     */
    UNAUTHORIZED("401", "账号未登录", "UNAUTHORIZED"),
    /**
     * 登录过期
     */
    EXPIRE("402", "登录已过期", "EXPIRE"),

    /**
     * 没有该操作权限
     */
    FORBIDDEN("403", "没有该操作权限", "FORBIDDEN"),

    /**
     * 请求未找到
     */
    NOT_FOUND("404", "请求未找到", "NOT_FOUND"),

    /**
     * 请求方法不正确
     */
    METHOD_NOT_ALLOWED("405", "请求方法不正确", "METHOD_NOT_ALLOWED"),
    /**
     * 获取当前用户失败
     */
    CURRENT_USER_FAIL("1001", "获取当前用户失败", "CURRENT_USER_FAIL"),
    /**
     * 用户是超级管理员，不可以修改状态
     */
    UPDATE_USER_STATUS("1002", "用户是超级管理员，不可以修改状态", "UPDATE_USER_STATUS"),
    /**
     * 用户是超级管理员，不可以修改密码
     */
    UPDATE_USER_PASSWORD("1003", "用户是超级管理员，不可以修改密码", "UPDATE_USER_PASSWORD"),
    /**
     * 用户未登录，请登陆后进行访问
     */
    USER_NEED_LOGIN("1101", "用户未登录，请登陆后进行访问", "USER_NEED_LOGIN"),
    /**
     * 该用户已在其它地方登录
     */
    USER_MAX_LOGIN("1102", "该用户已在其它地方登录", "USER_MAX_LOGIN"),
    /**
     * 长时间未操作，自动退出
     */
    USER_LOGIN_TIMEOUT("1103", "长时间未操作，自动退出", "USER_LOGIN_TIMEOUT"),
    /**
     * 用户被禁11005用
     */
    USER_DISABLED("1104", "用户被禁11005用", "USER_DISABLED"),
    /**
     * 用户被锁定
     */
    USER_LOCKED("1105", "用户被锁定", "USER_LOCKED"),
    /**
     * 用户名或密码错误
     */
    USER_PASSWORD_ERROR("1106", "用户名或密码错误", "USER_PASSWORD_ERROR"),
    /**
     * 用户密码过期
     */
    USER_PASSWORD_EXPIRED("1107", "用户密码过期", "USER_PASSWORD_EXPIRED"),
    /**
     * 用户账号过期
     */
    USER_ACCOUNT_EXPIRED("1108", "用户账号过期", "USER_ACCOUNT_EXPIRED"),
    /**
     * 没有该用户
     */
    USER_NOT_EXIST("1109", "没有该用户", "USER_NOT_EXIST"),
    /**
     * 用户登录失败
     */
    USER_LOGIN_FAIL("1110", "用户登录失败", "USER_LOGIN_FAIL"),
    /**
     * 验证码错误
     */
    VERIFY_CODE_ERROR("1111", "验证码错误", "VERIFY_CODE_ERROR"),
    /**
     * 用户已存在
     */
    USER_IS_EXIST("1112", "用户已存在", "USER_IS_EXIST"),
    /**
     * 无权访问
     */
    NO_AUTHENTICATION("1306", "无权访问", "NO_AUTHENTICATION"),
    /**
     * 角色ID无效
     */
    ROLE_IS_NOT_EXIST("1301", "角色ID无效", "ROLE_IS_NOT_EXIST"),
    /**
     * 角色代码已存在
     */
    ROLE_IS_EXIST("1302", "角色代码已存在", "ROLE_IS_EXIST"),
    /**
     * 配置信息为空
     */
    CONFIG_ID_IS_NOT_EXIST("1401", "配置信息为空", "CONFIG_ID_IS_NOT_EXIST"),
    /**
     * 配置ID无效
     */
    CONFIG_IS_NOT_EXIST("1402", "配置ID无效", "CONFIG_IS_NOT_EXIST"),
    /**
     * 配置ID已存在
     */
    CONFIG_IS_EXIST("1403", "配置ID已存在", "CONFIG_IS_EXIST"),
    /**
     * 系统配置不允许修改
     */
    CONFIG_IS_SYSTEM("1403", "系统配置不允许修改", "CONFIG_IS_SYSTEM"),
    /**
     * 系统配置不允许删除
     */
    CONFIG_IS_NOT_DELETE("1404", "系统配置不允许删除", "CONFIG_IS_NOT_DELETE"),
    /**
     * 文件不存在
     */
    FILE_DOES_NOT_EXIST("1601", "文件不存在", "FILE_DOES_NOT_EXIST"),
    /**
     * 文件上传异常
     */
    FILE_UPLOAD_EXCEPTION("1602", "文件上传异常", "FILE_UPLOAD_EXCEPTION"),
    /**
     * 文件下载异常
     */
    FILE_DOWNLOAD_ABNORMAL("1603", "文件下载异常", "FILE_DOWNLOAD_ABNORMAL"),
    /**
     * 无效的资源ID
     */
    RESOURCE_NOT_FIND("1201", "无效的资源ID", "RESOURCE_NOT_FIND"),
    /**
     * 资源ID已存在
     */
    RESOURCE_IS_EXIST("1201", "资源ID已存在", "RESOURCE_IS_EXIST"),
    /**
     * 无效资源父节点ID
     */
    RESOURCE_PARENT_NOT_FIND("1202", "无效资源父节点ID", "RESOURCE_PARENT_NOT_FIND"),
    /**
     * 无效资源父节点ID
     */
    RESOURCE_PARENT_INVALID("1203", "无效资源父节点ID", "RESOURCE_PARENT_INVALID"),
    /**
     * 该资源下有子资源，不能删除
     */
    RESOURCE_HAVE_SUB("1204", "该资源下有子资源，不能删除", "RESOURCE_HAVE_SUB"),
    /**
     * 机构已存在
     */
    ORG_IS_EXIST("1701", "机构已存在", "ORG_IS_EXIST"),
    /**
     * 机构不存在
     */
    ORG_NOT_EXIST("1702", "机构不存在", "ORG_NOT_EXIST"),
    /**
     * 机构下存在用户
     */
    ORG_HAVE_USER("1703", "机构下存在用户", "ORG_HAVE_USER"),
    /**
     * 无效机构父节点ID
     */
    ORG_PID_ERROR("1704", "无效机构父节点ID", "ORG_PID_ERROR"),
    /**
     * 父级节点禁止删除
     */
    ORG_TOP_FORBID("1705", "父级节点禁止删除", "ORG_TOP_FORBID"),
    /**
     * 机构下存在子机构
     */
    ORG_HAVE_BRANCH("1706", "机构下存在子机构", "ORG_HAVE_BRANCH"),
    /**
     * 停用原因不能为空
     */
    ORG_STOP_REASON("1707", "停用原因不能为空", "ORG_STOP_REASON"),

    //字典管理
    /**
     * 父级ID无效
     */
    DICT_PID_ERROR("1801", "父级ID无效", "DICT_PID_ERROR"),
    /**
     * ID无效
     */
    DICT_ID_ERROR("1802", "ID无效", "DICT_ID_ERROR"),
    /**
     * 字典code已存在
     */
    DICT_CODE_EXIST("1803", "字典code已存在", "DICT_CODE_EXIST"),
    /**
     * 字典name已存在
     */
    DICT_NAME_EXIST("1804", "字典name已存在", "DICT_NAME_EXIST"),
    /**
     * 字典下存在数据
     */
    DICT_HAVE_DATA("1805", "字典下存在数据", "DICT_HAVE_DATA"),
    /**
     * 字典不存在
     */
    DICT_NOT_EXIST("1806", "字典不存在", "DICT_NOT_EXIST"),
    /**
     * 存在子节点
     */
    DICT_HAVE_SON("1807", "存在子节点", "DICT_HAVE_SON"),
    //数据组
    /**
     * 数据组信息不存在
     */
    GROUP_ID_ERROR("1901", "数据组信息不存在", "GROUP_ID_ERROR"),
    /**
     * 数据组初始化无机构信息
     */
    GROUP_INIT_DATA_ERROR("1902", "数据组初始化无机构信息", "GROUP_INIT_DATA_ERROR"),

    /**
     * 系统异常
     */
    INTERNAL_SERVER_ERROR("500", "系统异常", "INTERNAL_SERVER_ERROR"),

    /**
     *
     */
    CONNECTION_TIME_OUT("501","服务连接超时", "CONNECTION_TIME_OUT"),

    /**
     *
     */
    READ_TIME_OUT("502","服务响应超时", "READ_TIME_OUT"),
    /**
     * 未知错误
     */
    UNKNOWN("999", "未知错误", "UNKNOWN");


    private final String code;

    private final String zhName;

    private final String enName;

    GlobalErrorCodeEnum(String code, String zhName, String enName) {
        this.code = code;
        this.zhName = zhName;
        this.enName = enName;
    }
}
