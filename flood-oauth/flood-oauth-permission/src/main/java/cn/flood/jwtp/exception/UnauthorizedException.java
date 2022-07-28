package cn.flood.jwtp.exception;

import cn.flood.exception.enums.GlobalErrorCodeEnum;

/**
 * 没有权限的异常
 *
 * @author mmdai
 */
public class UnauthorizedException extends TokenException {
    private static final long serialVersionUID = 8109117719383003891L;

    public UnauthorizedException() {
        super(GlobalErrorCodeEnum.FORBIDDEN.getCode(), GlobalErrorCodeEnum.FORBIDDEN.getZhName());
    }
}
