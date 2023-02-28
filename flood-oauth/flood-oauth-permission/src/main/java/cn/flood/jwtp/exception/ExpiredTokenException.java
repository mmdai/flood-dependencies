package cn.flood.jwtp.exception;

import cn.flood.base.core.exception.enums.GlobalErrorCodeEnum;

/**
 * token过期异常
 */
public class ExpiredTokenException extends TokenException {

  private static final long serialVersionUID = -8019541050781876369L;

  public ExpiredTokenException() {
    super(GlobalErrorCodeEnum.EXPIRE.getCode(), GlobalErrorCodeEnum.EXPIRE.getZhName());
  }
}
