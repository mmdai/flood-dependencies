package cn.flood.redis.exception;

/**
 * redis运行时异常
 */
public class RedisException extends RuntimeException {
    public RedisException(String message) {
        super(message);
    }
}
