package cn.flood.db.redis.lock.autoconfigure.lock;

import cn.flood.db.redis.lock.autoconfigure.model.LockInfo;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * Created by on 2017/12/29.
 */
public class FairLock implements Lock {

  private final LockInfo lockInfo;
  private RLock rLock;
  private RedissonClient redissonClient;

  public FairLock(RedissonClient redissonClient, LockInfo info) {
    this.redissonClient = redissonClient;
    this.lockInfo = info;
  }

  @Override
  public boolean acquire() {
    try {
      rLock = redissonClient.getFairLock(lockInfo.getName());
      return rLock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      return false;
    }
  }

  @Override
  public boolean release() {
    if (rLock.isHeldByCurrentThread()) {

      try {
        return rLock.forceUnlockAsync().get();
      } catch (InterruptedException e) {
        return false;
      } catch (ExecutionException e) {
        return false;
      }
    }
    return false;
  }
}
