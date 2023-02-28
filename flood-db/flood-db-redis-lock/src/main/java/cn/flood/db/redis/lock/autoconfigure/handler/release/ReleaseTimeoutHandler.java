package cn.flood.db.redis.lock.autoconfigure.handler.release;

import cn.flood.db.redis.lock.autoconfigure.handler.RlockTimeoutException;
import cn.flood.db.redis.lock.autoconfigure.model.LockInfo;

/**
 * 获取锁超时的处理逻辑接口
 *
 * @author wanglaomo
 * @since 2019/4/15
 **/
public interface ReleaseTimeoutHandler {

  void handle(LockInfo lockInfo) throws RlockTimeoutException;
}
