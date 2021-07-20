package cn.flood.captcha;

import java.util.stream.Stream;

/**
 * token enum
 */
public enum TokenEnum {

    CAPTCHA("0"), //图片验证码

    EMAIL("1"), //email

    MOBILE("2"); //手机

    // 成员变量
    private String code;


    // 构造方法
    TokenEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static TokenEnum valueOfEnum(String code) {
        return Stream.of(TokenEnum.values()).
                filter(em -> em.code.equals(code)).
                findFirst().orElse(null);
    }
}
