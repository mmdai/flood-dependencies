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
