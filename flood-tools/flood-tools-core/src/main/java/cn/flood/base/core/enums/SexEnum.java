package cn.flood.base.core.enums;

import java.util.stream.Stream;

/**
 * 性别的枚举
 *
 * @author aaronuu
 */
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
    public static SexEnum valueOfEnum(String code) {
        return Stream.of(SexEnum.values()).
                filter(eu -> eu.code.equals(code)).
                findFirst().orElse(null);
    }
    /**
     * 编码转化成中文含义
     */
    public static String getName(String code) {
        SexEnum em = Stream.of(SexEnum.values()).
                filter(eu -> eu.code.equals(code)).
                findFirst().orElse(null);
        return em == null? "" : em.name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
