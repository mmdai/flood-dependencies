package cn.flood.lock.autoconfigure.model;

import org.aspectj.lang.JoinPoint;
import cn.flood.lock.autoconfigure.handler.RlockTimeoutException;
import cn.flood.lock.autoconfigure.handler.lock.LockTimeoutHandler;
import cn.flood.lock.autoconfigure.lock.Lock;

import java.util.concurrent.TimeUnit;


/**
 * @author daimm
 * @since 2019/4/15
 **/
public enum LockTimeoutStrategy implements LockTimeoutHandler {


    /**
     * 继续执行业务逻辑，不做任何处理
     */
    NO_OPERATION() {
        @Override
        public void handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint) {
            // do nothing
        }
    },

    /**
     * 快速失败
     */
    FAIL_FAST() {
        @Override
        public void handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint) throws RlockTimeoutException {

            String errorMsg = String.format("Failed to acquire Lock(%s) with timeout(%ds)", lockInfo.getName(), lockInfo.getWaitTime());
            throw new RlockTimeoutException(errorMsg);
        }
    },

    /**
     * 一直阻塞，直到获得锁，在太多的尝试后，仍会报错
     */
    KEEP_ACQUIRE() {

        private static final long DEFAULT_INTERVAL = 100L;

        private static final long DEFAULT_MAX_INTERVAL = 3 * 60 * 1000L;

        @Override
        public void handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint) throws RlockTimeoutException {

            long interval = DEFAULT_INTERVAL;

            while(!lock.acquire()) {

                if(interval > DEFAULT_MAX_INTERVAL) {
                    String errorMsg = String.format("Failed to acquire Lock(%s) after too many times, this may because dead lock occurs.",
                                                     lockInfo.getName());
                    throw new RlockTimeoutException(errorMsg);
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(interval);
                    interval <<= 1;
                } catch (InterruptedException e) {
                    throw new RlockTimeoutException("Failed to acquire Lock", e);
                }
            }
        }
    }
}