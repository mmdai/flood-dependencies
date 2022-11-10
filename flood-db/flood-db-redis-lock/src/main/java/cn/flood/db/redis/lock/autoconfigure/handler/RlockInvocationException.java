package cn.flood.db.redis.lock.autoconfigure.handler;

import cn.flood.base.core.exception.CoreException;

/**
 * @author daimm
 * @since 2019/4/16
 **/
public class RlockInvocationException extends CoreException {

    private static final String ErrorCode = "S10002";


    public RlockInvocationException() {
        super(ErrorCode);
    }

    public RlockInvocationException( String message) {
        super(ErrorCode, message);
    }

    public RlockInvocationException(String message, Throwable cause) {
        super(ErrorCode, message, cause);
    }
}
