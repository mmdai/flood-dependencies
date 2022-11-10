package cn.flood.db.redis.lock.autoconfigure.lock;

/**
 * Created by on 2017/12/29.
 */
public interface Lock {

    boolean acquire();

    boolean release();
}

