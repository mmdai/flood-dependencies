package cn.flood.lock.autoconfigure.handler;

import cn.flood.exception.CoreException;

/**
 * @author daimm
 * @since 2019/4/16
 **/
public class RlockTimeoutException extends CoreException {

    private static final String ErrorCode = "S10001";

    public RlockTimeoutException() {
        super(ErrorCode);
    }

    public RlockTimeoutException(String message) {
        super(ErrorCode, message);
    }

    public RlockTimeoutException(String message, Throwable cause) {
        super(ErrorCode, message, cause);
    }
}
