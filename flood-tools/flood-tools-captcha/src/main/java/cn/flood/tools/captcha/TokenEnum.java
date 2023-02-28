package cn.flood.tools.captcha;

import java.util.stream.Stream;

/**
 * token enum
 */
public enum TokenEnum {

  CAPTCHA("0", "captcha"), //图片验证码

  EMAIL("1", "email"), //email

  MOBILE("2", "mobile"), //手机

  OTHER("3", "other"); //其他

  // 成员变量
  private String code;

  private String name;

  // 构造方法
  TokenEnum(String code, String name) {
    this.code = code;
    this.name = name;
  }

  public static TokenEnum valueOfEnum(String code) {
    return Stream.of(TokenEnum.values()).
        filter(em -> em.code.equals(code)).
        findFirst().orElse(null);
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
