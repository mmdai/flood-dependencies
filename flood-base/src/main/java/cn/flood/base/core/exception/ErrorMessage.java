package cn.flood.base.core.exception;

import java.io.Serializable;

/**
 * 错误信息实体类
 * <p>
 * Created on 2017年6月19日
 * <p>
 *
 * @author mmdai
 * @date 2017年6月19日
 */
public class ErrorMessage implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 5276841118425404723L;
  /**
   * 错误码
   */
  private String code;

  /**
   * 错误信息说明，实际从message.properties中翻译
   */
  private String msg;


  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
