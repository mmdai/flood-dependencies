package cn.flood.db.redis.lock.autoconfigure.model;

/**
 * Created by on 2017/12/29.
 * Content :锁类型
 */
public enum LockType {
    /**
     * 可重入锁
     */
    Reentrant,
    /**
     * 公平锁
     */
    Fair,
    /**
     * 读锁
     */
    Read,
    /**
     * 写锁
     */
    Write,
    /**
     * 联锁
     */
    Multi;

    LockType() {
    }

}
