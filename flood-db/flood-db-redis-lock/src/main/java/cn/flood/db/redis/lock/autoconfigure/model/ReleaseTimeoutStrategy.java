package cn.flood.db.redis.lock.autoconfigure.model;

import cn.flood.db.redis.lock.autoconfigure.handler.RlockTimeoutException;
import cn.flood.db.redis.lock.autoconfigure.handler.release.ReleaseTimeoutHandler;

/**
 * @author daimm
 * @since 2019/4/15
 **/
public enum ReleaseTimeoutStrategy implements ReleaseTimeoutHandler {

    /**
     * 继续执行业务逻辑，不做任何处理
     */
    NO_OPERATION() {
        @Override
        public void handle(LockInfo lockInfo) {
            // do nothing
        }
    },
    /**
     * 快速失败
     */
    FAIL_FAST() {
        @Override
        public void handle(LockInfo lockInfo) throws RlockTimeoutException {

            String errorMsg = String.format("Found Lock(%s) already been released while lock lease time is %d s", lockInfo.getName(), lockInfo.getLeaseTime());
            throw new RlockTimeoutException(errorMsg);
        }
    }
}
