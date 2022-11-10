package cn.flood.db.redis.lock.autoconfigure.core;

import cn.flood.db.redis.lock.autoconfigure.annotation.Rlock;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import cn.flood.db.redis.lock.autoconfigure.config.RlockConfig;
import cn.flood.db.redis.lock.autoconfigure.model.LockInfo;
import cn.flood.db.redis.lock.autoconfigure.model.LockType;

/**
 * Created by on 2017/12/29.
 */
public class LockInfoProvider {

    private static final String LOCK_NAME_PREFIX = "lock";
    private static final String LOCK_NAME_SEPARATOR = ".";


    @Autowired
    private RlockConfig rlockConfig;

    @Autowired
    private BusinessKeyProvider businessKeyProvider;

    private static final Logger logger = LoggerFactory.getLogger(LockInfoProvider.class);

    LockInfo get(JoinPoint joinPoint, Rlock rlock) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LockType type= rlock.lockType();
        String businessKeyName=businessKeyProvider.getKeyName(joinPoint,rlock);
        //锁的名字，锁的粒度就是这里控制的
        String lockName = LOCK_NAME_PREFIX + LOCK_NAME_SEPARATOR + getName(rlock.name(), signature) + businessKeyName;
        long waitTime = getWaitTime(rlock);
        long leaseTime = getLeaseTime(rlock);
        //如果占用锁的时间设计不合理，则打印相应的警告提示
        if(leaseTime == -1 && logger.isWarnEnabled()) {
            logger.warn("Trying to acquire Lock({}) with no expiration, " +
                        "Rlock will keep prolong the lock expiration while the lock is still holding by current thread. " +
                        "This may cause dead lock in some circumstances.", lockName);
        }
        return new LockInfo(type,lockName,waitTime,leaseTime);
    }

    /**
     * 获取锁的name，如果没有指定，则按全类名拼接方法名处理
     * @param annotationName
     * @param signature
     * @return
     */
    private String getName(String annotationName, MethodSignature signature) {
        if (annotationName.isEmpty()) {
            return String.format("%s.%s", signature.getDeclaringTypeName(), signature.getMethod().getName());
        } else {
            return annotationName;
        }
    }


    private long getWaitTime(Rlock lock) {
        return lock.waitTime() == Long.MIN_VALUE ?
                rlockConfig.getWaitTime() : lock.waitTime();
    }

    private long getLeaseTime(Rlock lock) {
        return lock.leaseTime() == Long.MIN_VALUE ?
                rlockConfig.getLeaseTime() : lock.leaseTime();
    }
}
