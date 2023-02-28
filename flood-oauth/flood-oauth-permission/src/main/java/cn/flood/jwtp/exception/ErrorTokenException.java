package cn.flood.jwtp.exception;

import cn.flood.base.core.exception.enums.GlobalErrorCodeEnum;

/**
 * token验证失败异常
 */
public class ErrorTokenException extends TokenException {

  private static final long serialVersionUID = -2283411683871567063L;

  public ErrorTokenException() {
    super(GlobalErrorCodeEnum.UNAUTHORIZED.getCode(), GlobalErrorCodeEnum.UNAUTHORIZED.getZhName());
  }

  public ErrorTokenException(String message) {
    super(GlobalErrorCodeEnum.UNAUTHORIZED.getCode(), message);
  }
}
