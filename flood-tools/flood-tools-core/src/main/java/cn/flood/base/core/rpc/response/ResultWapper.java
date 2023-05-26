/**
 * <p>Title: ResultWapper.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author mmdai
 * @date 2018年12月6日
 * @version 1.0
 */
package cn.flood.base.core.rpc.response;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>Title: ResultWapper</p>  
 * <p>Description: 封装返回值</p>  
 * @author mmdai
 * @date 2018年12月6日
 */
public class ResultWapper {

  /**
   * Instantiates a new result mapper.
   */
  private ResultWapper() {
  }

  /**
   * Result.
   *
   * @param <E>     the element type
   * @param code    the code
   * @param msg the msg
   * @param o       the o
   *
   * @return the wrapper
   */
  public static <E> Result<E> wrap(String code, String msg, E o) {
    return new Result<>(code, msg, o);
  }

  /**
   * Result.
   *
   * @param <E>     the element type
   * @param code    the code
   * @param msg the msg
   *
   * @return the wrapper
   */
  public static <E> Result<E> wrap(String code, String msg) {
    return wrap(code, msg, null);
  }

  /**
   * Result.
   *
   * @param <E>  the element type
   * @param code the code
   *
   * @return the wrapper
   */
  public static <E> Result<E> wrap(String code) {
    return wrap(code, null);
  }

  /**
   * Wrap.
   *
   * @param <E> the element type
   * @param e   the e
   *
   * @return the wrapper
   */
  public static <E> Result<E> wrap(Exception e) {
    return new Result<>(Result.ERROR_CODE, e.getMessage());
  }

  /**
   * Un wrapper.
   *
   * @param <E>     the element type
   * @param wrapper the wrapper
   *
   * @return the e
   */
  public static <E> E unWrap(Result<E> wrapper) {
    return wrapper.getData();
  }


  /**
   * Wrap ERROR. code=500
   *s
   * @param <E> the element type
   *
   * @return the wrapper
   */
  public static <E> Result<E> error() {
    return wrap(Result.ERROR_CODE, Result.ERROR_MESSAGE);
  }


  /**
   * Result wrapper.
   *
   * @param <E>     the type parameter
   * @param msg the msg
   *
   * @return the wrapper
   */
  public static <E> Result<E> error(String msg) {
    return wrap(Result.ERROR_CODE, StringUtils.isBlank(msg) ? Result.ERROR_MESSAGE : msg);
  }

  /**
   * Wrap SUCCESS. code=200
   *
   * @param <E> the element type
   *
   * @return the wrapper
   */
  public static <E> Result<E> ok() {
    return new Result<>();
  }

  /**
   * Ok wrapper.
   *
   * @param <E> the type parameter
   * @param o   the o
   *
   * @return the wrapper
   */
  public static <E> Result<E> ok(E o) {
    return new Result<>(Result.SUCCESS_CODE, Result.SUCCESS_MSG, o);
  }

  /**
   * Ok wrapper.
   *
   * @param <E> the type parameter
   * @param o   the o
   *
   * @return the wrapper
   */
  public static <E> ResultSign<E> okSign(E o, String sign) {
    return new ResultSign<>(ResultSign.SUCCESS_CODE, ResultSign.SUCCESS_MSG, o, sign);
  }

}
