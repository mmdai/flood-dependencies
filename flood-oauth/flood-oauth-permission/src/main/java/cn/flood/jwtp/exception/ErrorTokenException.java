package cn.flood.jwtp.exception;

/**
 * token验证失败异常
 *
 */
public class ErrorTokenException extends TokenException {
    private static final long serialVersionUID = -2283411683871567063L;

    public ErrorTokenException() {
        super("401", "身份验证失败");
    }

    public ErrorTokenException(String message) {
        super("401", message);
    }
}
