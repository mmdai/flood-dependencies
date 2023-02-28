package cn.flood.base.core.enums;

/**
 * 环境常量
 *
 * @author pangu
 */
public enum EnvType {

  /**
   * 环境变量 LOCAL 本地 DEV 开发 TEST 测试 PROD 生产 DOCKER Docker
   */
  LOCAL("local"),
  DEV("dev"),
  TEST("test"),
  PROD("prod"),
  DOCKER("docker");

  private final String code;

  EnvType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
