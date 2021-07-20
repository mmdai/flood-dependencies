package cn.flood.lock.autoconfigure.lock;

import cn.flood.lock.autoconfigure.model.LockInfo;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MultiLock implements Lock {

    private RedissonMultiLock rLock;

    private RedissonClient redissonClient;

    private final List<LockInfo> lockInfos;


    public MultiLock(RedissonClient redissonClient, List<LockInfo> lockInfos) {
        this.lockInfos = lockInfos;
        this.redissonClient = redissonClient;
        RLock[] rLocks = new RLock[lockInfos.size()];
        for (int i = 0, length = lockInfos.size(); i < length; i++) {
            RLock lock = redissonClient.getLock(lockInfos.get(i).getName());
            rLocks[i] = lock;
        }
        this.rLock = new RedissonMultiLock(rLocks);
    }

    @Override
    public boolean acquire() {
        try {
            LockInfo lockInfo = lockInfos.get(0);
            return rLock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), TimeUnit.SECONDS);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean release() {
        try {
            rLock.unlock();
        } catch (Exception e) {
            return false;
        }
        return true;

    }
}
