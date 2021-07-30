package cn.flood.enums;

import lombok.Getter;

/**
 * 性别的枚举
 *
 * @author aaronuu
 */
@Getter
public enum SexEnum {

    /**
     * 男
     */
    M("M", "男"),

    /**
     * 女
     */
    F("F", "女");

    private final String code;

    private final String name;

    SexEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据code获取枚举
     */
    public static SexEnum codeToEnum(String code) {
        if (null != code) {
            for (SexEnum e : SexEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e;
                }
            }
        }
        return null;
    }

    /**
     * 编码转化成中文含义
     */
    public static String codeToName(String code) {
        if (null != code) {
            for (SexEnum e : SexEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.name();
                }
            }
        }
        return "未知";
    }

}
