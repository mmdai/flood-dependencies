package cn.flood.base.core.enums;


import cn.flood.base.core.enums.base.CommonEnum;

import java.util.stream.Stream;

/**
 * 公共状态，一般用来表示开启和关闭
 *
 * @author aaronuu
 */
public enum StatusEnum implements CommonEnum {

  /**
   * 启用
   */
  ENABLE("enable", "启用"),

  /**
   * 禁用
   */
  DISABLE("disable", "禁用");

  private final String code;

  private final String name;

  StatusEnum(String code, String name) {
    this.code = code;
    this.name = name;
  }

  /**
   * 根据code获取枚举
   */
  public static StatusEnum valueOfEnum(String code) {
    return Stream.of(StatusEnum.values()).
        filter(eu -> eu.code.equals(code)).
        findFirst().orElse(null);
  }

  // 普通方法
  public static String getName(String code) {
    StatusEnum em = Stream.of(StatusEnum.values()).
        filter(eu -> eu.code.equals(code)).
        findFirst().orElse(null);
    return em == null ? "" : em.name;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return name;
  }
}
