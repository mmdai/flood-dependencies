/**
 * <p>Title: ResultSign.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author mmdai
 * @date 2019年3月12日
 * @version 1.0
 */
package cn.flood.base.core.rpc.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.ObjectUtils;

/**
 * <p>Title: ResultSign</p>  
 * <p>Description: </p>  
 * @author mmdai
 * @date 2019年3月12日
 */
@SuppressWarnings("unchecked")
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultSign<T> {

  /**
   * 成功码.
   */
  public static final String SUCCESS_CODE = "000000";
  /**
   * 成功信息.
   */
  public static final String SUCCESS_MSG = "success";
  /**
   * 错误码.
   */
  public static final String ERROR_CODE = "S00000";
  /**
   * 错误信息.
   */
  public static final String ERROR_MESSAGE = "系统内部错误";
  /** 编号 **/
  private String code;
  /** 信息 **/
  private String msg;
  /** 结果 **/
  private T data;
  /** 签名 **/
  private String sign;

  ResultSign() {
    this(SUCCESS_CODE, SUCCESS_MSG);
  }

  ResultSign(String code, String msg) {
    this(code, msg, null, null);
  }

  ResultSign(String code, String msg, T data) {
    this(code, msg, data, null);
  }

  ResultSign(String code, String msg, T data, String sign) {
    code(code).message(msg).data(data).sign(sign);
  }

  @JsonIgnore
  public boolean is_succeed() {
    if (ObjectUtils.isEmpty(this.code)) {
      return false;
    }
    return this.code.equals(SUCCESS_CODE);
  }

  @JsonIgnore
  public boolean error() {
    return !is_succeed();
  }

  /**
   * Sets the 编号 , 返回自身的引用.
   *
   * @param code the new 编号
   *
   * @return the wrapper
   */
  private ResultSign code(String code) {
    this.setCode(code);
    return this;
  }

  /**
   * Sets the 信息 , 返回自身的引用.
   *
   * @param msg the new 信息
   *
   * @return the Result
   */
  private ResultSign message(String msg) {
    this.setMsg(msg);
    return this;
  }

  /**
   * Sets the 结果数据 , 返回自身的引用.
   *
   * @param data the new 结果数据
   *
   * @return the wrapper
   */
  public ResultSign data(T data) {
    this.setData(data);
    return this;
  }

  /**
   * Sets the 结果数据 , 返回自身的引用.
   *
   * @param sign the new 结果数据
   *
   * @return the wrapper
   */
  public ResultSign sign(String sign) {
    this.setSign(sign);
    return this;
  }

}
