package cn.flood.redisdelayqueuespringdemo.service;

import cn.flood.idempotent.autoconfigure.annotation.Idempotent;
import cn.flood.lock.autoconfigure.annotation.Rlock;
import cn.flood.lock.autoconfigure.annotation.RlockKey;
import cn.flood.lock.autoconfigure.model.LockTimeoutStrategy;
import org.springframework.stereotype.Service;

/**
 */
@Service
public class TestService {

    @Idempotent(key = "#param", expireTime = 60, info = "请勿重复查询")
//    @Rlock(waitTime = 10,leaseTime = 60,keys = {"#param"},lockTimeoutStrategy = LockTimeoutStrategy.FAIL_FAST)
    public String getValue(String param) throws Exception {
      //  if ("sleep".equals(param)) {//线程休眠或者断点阻塞，达到一直占用锁的测试效果
//            Thread.sleep(10000*3);
        //}
        System.out.println("===============================");
        return "success";
    }

    @Rlock(keys = {"#userId"})
    public String getValue(String userId,@RlockKey Integer id)throws Exception{
        Thread.sleep(60*1000);
        return "success";
    }

    @Rlock(keys = {"#user.name","#user.id"})
    public String getValue(User user)throws Exception{
        Thread.sleep(60*1000);
        return "success";
    }

}
