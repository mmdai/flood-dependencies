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
     * 没有该操作权限
     */
    UNAUTHORIZED("401", "账号未登录", "UNAUTHORIZED"),

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
