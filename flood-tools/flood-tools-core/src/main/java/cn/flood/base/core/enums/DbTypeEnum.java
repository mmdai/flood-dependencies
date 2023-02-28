package cn.flood.base.core.enums;

import java.util.stream.Stream;

/**
 * 不同数据库类型的枚举
 * <p>
 * 用于标识mapping.xml中不同数据库的标识
 *
 * @author aaronuu
 */
public enum DbTypeEnum {

  /**
   * mysql
   */
  MYSQL("mysql", "mysql"),

  /**
   * pgsql
   */
  PG_SQL("pgsql", "postgresql"),

  /**
   * oracle
   */
  ORACLE("oracle", "oracle"),

  /**
   * mssql
   */
  MS_SQL("mssql", "sqlserver");

  private final String code;

  private final String name;

  DbTypeEnum(String code, String name) {
    this.code = code;
    this.name = name;
  }

  /**
   * 根据code获取枚举
   */
  public static DbTypeEnum valueOfEnum(String code) {
    return Stream.of(DbTypeEnum.values()).
        filter(eu -> eu.code.equals(code)).
        findFirst().orElse(null);
  }

  /**
   * 编码转化成中文含义
   */
  public static String getName(String code) {
    DbTypeEnum em = Stream.of(DbTypeEnum.values()).
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
}
