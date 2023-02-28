package cn.flood.base.core.enums;

/**
 * 方法类型
 *
 * @author pangu
 */
public enum MethodType {

  /**
   * 方法类型 GET PUT POST DELETE OPTIONS
   */
  GET(false),
  PUT(true),
  POST(true),
  DELETE(false),
  HEAD(false),
  OPTIONS(false);

  private final boolean code;

  MethodType(boolean code) {
    this.code = code;
  }

  public boolean isCode() {
    return code;
  }
}
