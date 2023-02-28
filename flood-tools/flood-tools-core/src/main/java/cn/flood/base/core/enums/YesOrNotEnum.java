package cn.flood.base.core.enums;

import java.util.stream.Stream;
import lombok.Getter;

/**
 * 是或否的枚举，一般用在数据库字段，例如del_flag字段，char(1)，填写Y或N
 *
 * @author aaronuu
 */
@Getter
public enum YesOrNotEnum {

  /**
   * 是
   */
  Y("Y", "是"),

  /**
   * 否
   */
  N("N", "否");

  private final String code;

  private final String name;

  YesOrNotEnum(String code, String name) {
    this.code = code;
    this.name = name;
  }

  // 普通方法
  public static String getName(String code) {
    YesOrNotEnum em = Stream.of(YesOrNotEnum.values()).
        filter(eu -> eu.code.equals(code)).
        findFirst().orElse(null);
    return em == null ? "" : em.name;
  }

  public static YesOrNotEnum valueOfEnum(String code) {
    return Stream.of(YesOrNotEnum.values()).
        filter(eu -> eu.code.equals(code)).
        findFirst().orElse(null);
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }
}
