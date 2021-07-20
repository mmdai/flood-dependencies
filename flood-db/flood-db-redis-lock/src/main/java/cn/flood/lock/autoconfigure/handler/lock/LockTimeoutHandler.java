package cn.flood.lock.autoconfigure.handler.lock;

import cn.flood.lock.autoconfigure.handler.RlockTimeoutException;
import org.aspectj.lang.JoinPoint;
import cn.flood.lock.autoconfigure.lock.Lock;
import cn.flood.lock.autoconfigure.model.LockInfo;

/**
 * 获取锁超时的处理逻辑接口
 *
 * @since 2019/4/15
 **/
public interface LockTimeoutHandler {

    void handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint) throws RlockTimeoutException;
}
