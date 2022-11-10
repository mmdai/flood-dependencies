package cn.flood.db.redis.lock.autoconfigure.core;

import cn.flood.db.redis.lock.autoconfigure.annotation.Rlock;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import cn.flood.db.redis.lock.autoconfigure.handler.RlockInvocationException;
import cn.flood.db.redis.lock.autoconfigure.lock.Lock;
import cn.flood.db.redis.lock.autoconfigure.lock.LockFactory;
import cn.flood.db.redis.lock.autoconfigure.model.LockInfo;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by on 2017/12/29.
 * Content :给添加@RLock切面加锁处理
 */
@Aspect
@Component
@Order(0)
public class RlockAspectHandler {

    private static final Logger logger = LoggerFactory.getLogger(RlockAspectHandler.class);

    @Autowired
    LockFactory lockFactory;

    @Autowired
    private LockInfoProvider lockInfoProvider;

    private final Map<String,LockRes> currentThreadLock = new ConcurrentHashMap<>();


    @Around(value = "@annotation(rlock)")
    public Object around(ProceedingJoinPoint joinPoint, Rlock rlock) throws Throwable {
        LockInfo lockInfo = lockInfoProvider.get(joinPoint, rlock);
        String currentLock = this.getCurrentLockId(joinPoint, rlock);
        currentThreadLock.put(currentLock,new LockRes(lockInfo, false));
        Lock lock = lockFactory.getLock(lockInfo);
        boolean lockRes = lock.acquire();

        //如果获取锁失败了，则进入失败的处理逻辑
        if(!lockRes) {
            if(logger.isWarnEnabled()) {
                logger.warn("Timeout while acquiring Lock({})", lockInfo.getName());
            }
            //如果自定义了获取锁失败的处理策略，则执行自定义的降级处理策略
            if(!ObjectUtils.isEmpty(rlock.customLockTimeoutStrategy())) {

                return handleCustomLockTimeout(rlock.customLockTimeoutStrategy(), joinPoint);

            } else {
                //否则执行预定义的执行策略
                //注意：如果没有指定预定义的策略，默认的策略为静默啥不做处理
                rlock.lockTimeoutStrategy().handle(lockInfo, lock, joinPoint);
            }
        }

        currentThreadLock.get(currentLock).setLock(lock);
        currentThreadLock.get(currentLock).setRes(true);

        return joinPoint.proceed();
    }

    @AfterReturning(value = "@annotation(rlock)")
    public void afterReturning(JoinPoint joinPoint, Rlock rlock) throws Throwable {
        String currentLock = this.getCurrentLockId(joinPoint,rlock);
        releaseLock(rlock, joinPoint,currentLock);
        cleanUpThreadLocal(currentLock);
    }

    @AfterThrowing(value = "@annotation(rlock)", throwing = "ex")
    public void afterThrowing (JoinPoint joinPoint, Rlock rlock, Throwable ex) throws Throwable {
        String currentLock = this.getCurrentLockId(joinPoint,rlock);
        releaseLock(rlock, joinPoint,currentLock);
        cleanUpThreadLocal(currentLock);
        throw ex;
    }

    /**
     * 处理自定义加锁超时
     */
    private Object handleCustomLockTimeout(String lockTimeoutHandler, JoinPoint joinPoint) throws Throwable {

        // prepare invocation context
        Method currentMethod = ((MethodSignature)joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod = null;
        try {
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(lockTimeoutHandler, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customLockTimeoutStrategy",e);
        }
        Object[] args = joinPoint.getArgs();

        // invoke
        Object res = null;
        try {
            res = handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new RlockInvocationException("Fail to invoke custom lock timeout handler: " + lockTimeoutHandler ,e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }

        return res;
    }

    /**
     *  释放锁
     */
    private void releaseLock(Rlock rlock, JoinPoint joinPoint, String currentLock) throws Throwable {
        LockRes lockRes = currentThreadLock.get(currentLock);
        if(Objects.isNull(lockRes)){
            throw new NullPointerException("Please check whether the input parameter used as the lock key value has been modified in the method, which will cause the acquire and release locks to have different key values and throw null pointers.currentLockKey:" + currentLock);
        }
        if (lockRes.getRes()) {
            boolean releaseRes = currentThreadLock.get(currentLock).getLock().release();
            // avoid release lock twice when exception happens below
            lockRes.setRes(false);
            if (!releaseRes) {
                handleReleaseTimeout(rlock, lockRes.getLockInfo(), joinPoint);
            }
        }
    }

    // avoid memory leak
    private void cleanUpThreadLocal(String currentLock) {
        currentThreadLock.remove(currentLock);
    }

    /**
     * 获取当前锁在map中的key
     * @param joinPoint
     * @param rlock
     * @return
     */
    private String getCurrentLockId(JoinPoint joinPoint , Rlock rlock){
        LockInfo lockInfo = lockInfoProvider.get(joinPoint,rlock);
        String currentLock= Thread.currentThread().getId() + lockInfo.getName();
        return currentLock;
    }

    /**
     *  处理释放锁时已超时
     */
    private void handleReleaseTimeout(Rlock rlock, LockInfo lockInfo, JoinPoint joinPoint) throws Throwable {

        if(logger.isWarnEnabled()) {
            logger.warn("Timeout while release Lock({})", lockInfo.getName());
        }

        if(!ObjectUtils.isEmpty(rlock.customReleaseTimeoutStrategy())) {

            handleCustomReleaseTimeout(rlock.customReleaseTimeoutStrategy(), joinPoint);

        } else {
            rlock.releaseTimeoutStrategy().handle(lockInfo);
        }

    }

    /**
     * 处理自定义释放锁时已超时
     */
    private void handleCustomReleaseTimeout(String releaseTimeoutHandler, JoinPoint joinPoint) throws Throwable {

        Method currentMethod = ((MethodSignature)joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod = null;
        try {
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(releaseTimeoutHandler, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customReleaseTimeoutStrategy",e);
        }
        Object[] args = joinPoint.getArgs();

        try {
            handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new RlockInvocationException("Fail to invoke custom release timeout handler: " + releaseTimeoutHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private class LockRes {

        private LockInfo lockInfo;
        private Lock lock;
        private Boolean res;

        LockRes(LockInfo lockInfo, Boolean res) {
            this.lockInfo = lockInfo;
            this.res = res;
        }

        LockInfo getLockInfo() {
            return lockInfo;
        }

        public Lock getLock() {
            return lock;
        }

        public void setLock(Lock lock) {
            this.lock = lock;
        }

        Boolean getRes() {
            return res;
        }

        void setRes(Boolean res) {
            this.res = res;
        }

        void setLockInfo(LockInfo lockInfo) {
            this.lockInfo = lockInfo;
        }
    }


}
