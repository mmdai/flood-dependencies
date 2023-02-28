package cn.flood.db.mybatis.plus.plugins.permssion.enums;

import java.util.stream.Stream;

/**
 * 数据权限（）
 *
 * @author mmdai
 * @version 1.0
 * @date 2022/2/25 9:32
 */
public enum DataScopeViewTypeEnum {

  ME(0, 0, "本人"),

  DEPARTMENT(1, 5, "本部门"),

  DEPARTMENT_AND_SUB(2, 10, "本部门及下属"),

  ALL(3, 15, "全部");

  private final Integer type;

  private final Integer level;

  private final String name;

  DataScopeViewTypeEnum(Integer type, Integer level, String name) {
    this.type = type;
    this.level = level;
    this.name = name;
  }

  /**
   * @param type
   * @return
   */
  public static String getName(String type) {
    DataScopeViewTypeEnum em = Stream.of(DataScopeViewTypeEnum.values()).
        filter(eu -> eu.type.equals(type)).
        findFirst().orElse(null);
    return em == null ? "" : em.name;
  }

  /**
   * @param type
   * @return
   */
  public static Integer getLevel(String type) {
    DataScopeViewTypeEnum em = Stream.of(DataScopeViewTypeEnum.values()).
        filter(eu -> eu.type.equals(type)).findFirst().orElse(null);
    return em == null ? null : em.level;
  }

  /**
   * @param type
   * @return
   */
  public static DataScopeViewTypeEnum typeOfEnum(Integer type) {
    return Stream.of(DataScopeViewTypeEnum.values()).
        filter(eu -> eu.type.equals(type)).findFirst().orElse(null);
  }

  /**
   * @param level
   * @return
   */
  public static DataScopeViewTypeEnum levelOfEnum(Integer level) {
    return Stream.of(DataScopeViewTypeEnum.values()).
        filter(eu -> eu.level.equals(level)).findFirst().orElse(null);
  }

  public Integer getType() {
    return type;
  }

  public Integer getLevel() {
    return level;
  }

  public String getName() {
    return name;
  }
}
