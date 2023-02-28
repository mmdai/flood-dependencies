package cn.flood.redisdelayqueuespringdemo.service;

import cn.flood.db.redis.lock.autoconfigure.annotation.Rlock;
import cn.flood.db.redis.lock.autoconfigure.model.LockTimeoutStrategy;
import cn.flood.db.redis.lock.autoconfigure.model.LockType;
import cn.flood.db.redis.lock.autoconfigure.model.ReleaseTimeoutStrategy;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author daimm
 * @since 2019/4/16
 **/
@Service
public class TimeoutService {

  private static final Logger logger = LoggerFactory.getLogger(TimeoutService.class);

  @Rlock(name = "foo-service", leaseTime = -1, releaseTimeoutStrategy = ReleaseTimeoutStrategy.FAIL_FAST)
  public void foo1() {
    try {
      logger.info("foo1 acquire lock");
      TimeUnit.SECONDS.sleep(3);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Rlock(name = "foo-service", waitTime = 1, lockType = LockType.Write, lockTimeoutStrategy = LockTimeoutStrategy.FAIL_FAST)
  public void foo2() {
    try {
      logger.info("acquire lock");
      TimeUnit.SECONDS.sleep(30);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Rlock(name = "foo-service", lockType = LockType.Fair, waitTime = 1000, lockTimeoutStrategy = LockTimeoutStrategy.FAIL_FAST)
  public void foo3() {
    try {
      TimeUnit.SECONDS.sleep(30);
      logger.info("acquire lock");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Rlock(name = "foo-service",
      waitTime = 2,
      customLockTimeoutStrategy = "customLockTimeout")
  public String foo4(String foo, String bar) {
    try {
      TimeUnit.SECONDS.sleep(2);
      logger.info("acquire lock");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return "foo4";
  }

  private String customLockTimeout(String foo, String bar) {

    logger.info("customLockTimeout foo: " + foo + " bar: " + bar);
    return "custom foo: " + foo + " bar: " + bar;
  }


  @Rlock(name = "foo-service", waitTime = 10)
  public void foo5(String foo, String bar) {
    try {
      TimeUnit.SECONDS.sleep(2);
      logger.info("acquire lock");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Rlock(name = "foo-service", leaseTime = 10, waitTime = 10000)
  public void foo6(String foo, String bar) {
    try {
      TimeUnit.SECONDS.sleep(2);
      logger.info("acquire lock");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Rlock(name = "foo-service", leaseTime = 1, waitTime = 10000, releaseTimeoutStrategy = ReleaseTimeoutStrategy.FAIL_FAST)
  public void foo7(String foo, String bar) {
    try {
      TimeUnit.SECONDS.sleep(2);
      logger.info("acquire lock");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


  @Rlock(name = "foo-service", leaseTime = 1, waitTime = 10000, customReleaseTimeoutStrategy = "customReleaseTimeout")
  public String foo8(String foo, String bar) {
    try {
      TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return "foo8";
  }

  private String customReleaseTimeout(String foo, String bar) {

    throw new IllegalStateException("customReleaseTimeout");
  }
}
