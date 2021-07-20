package cn.flood.jwtp.exception;

import cn.flood.exception.CoreException;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * TokenException
 *
 */
public abstract class TokenException extends CoreException {
    private static final long serialVersionUID = 2413958299445359500L;

    public TokenException(String code) {
        super(code);
    }

    public TokenException(String code, Throwable error) {
        super(code, error);
    }

    public TokenException(String code, Object[] args, Throwable error) {
        super(code, args, error);
    }

    public TokenException(String code, String msg) {
        super(code, msg);
    }

    public TokenException(String code, Object[] args, String msg) {
        super(code, args, msg);
    }

}
