package cn.flood.delay.redis.core;


import cn.flood.delay.redis.configuration.Config;
import cn.flood.delay.redis.constans.LuaScriptConst;
import cn.flood.delay.redis.exception.RDQException;
import cn.flood.delay.redis.job.AckMessageJob;
import cn.flood.delay.redis.job.DelayMessageJob;
import cn.flood.delay.redis.job.ErrorMessageJob;
import cn.flood.delay.redis.utils.ClassUtil;
import cn.flood.delay.redis.utils.GsonUtil;
import cn.flood.delay.redis.utils.ThreadUtil;
import io.lettuce.core.ScriptOutputType;
import java.io.Serializable;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;

/**
 * RDQueue
 *
 * @author mmdai
 * @date 2019/11/21
 */
@Slf4j
public class RDQueue {

  private DQRedis dqRedis;

  private Config config;

  public RDQueue(Config config) {
    this.config = config;
    this.init();
  }

  private void init() {
    this.dqRedis = new DQRedis(config.getHost(), config.getPort(), config.getDatabase(), config.getPassword(),
        config.getTimeout(), config.getCluster(),
        config.getSentinel());

    log.info("redis-dqueue starting...");

    int maxJobCoreSize = config.getMaxJobCoreSize();
    int maxCallbackCoreSize = config.getMaxCallbackCoreSize();

    ScheduledExecutorService jobThreadPool =
        new ScheduledThreadPoolExecutor(maxJobCoreSize,
            ThreadUtil.jobThreadFactory(maxJobCoreSize));

    ExecutorService callbackThreadPool =
        Executors.newFixedThreadPool(maxCallbackCoreSize,
            ThreadUtil.callbackThreadFactory(maxCallbackCoreSize));

    jobThreadPool
        .scheduleAtFixedRate(new DelayMessageJob(config, dqRedis, callbackThreadPool), 0, 200,
            TimeUnit.MILLISECONDS);
    jobThreadPool
        .scheduleAtFixedRate(new AckMessageJob(config, dqRedis), 0, 1000, TimeUnit.MILLISECONDS);
    jobThreadPool
        .scheduleAtFixedRate(new ErrorMessageJob(config, dqRedis), 0, 1000, TimeUnit.MILLISECONDS);
  }

  /**
   * 异步推送消息到延时队列
   *
   * @param message
   * @param action
   * @throws RDQException
   */
  public void asyncPushDelayQueue(Message message, BiConsumer<String, ? super Throwable> action)
      throws RDQException {
    this.push(message, action, true);
  }

  /**
   * 同步推送消息到延时队列
   *
   * @param message
   * @throws RDQException
   */
  public void syncPushDelayQueue(Message message) throws RDQException {
    this.push(message, null, false);
  }

  /**
   * 删除延时队列里的消息
   *
   * @param key
   * @throws RDQException
   */
  public void delDelayQueue(String key) {
    dqRedis.zrem(config.getDelayKey(), key);
    dqRedis.zrem(config.getAckKey(), key);
    dqRedis.hdel(config.getHashKey(), key);
  }

  private void push(Message message, BiConsumer<String, ? super Throwable> action,
      boolean asyncExecute) throws RDQException {
    this.checkQueue(config.getKeyPrefix(), message);

    String queueKey = config.getDelayKey();
    log.info("push message {}", message);

    String key = message.getMsgId();

    RawMessage rawMessage = buildTask(key, message);
    String hashValue = GsonUtil.toJson(rawMessage);

    String[] keys = new String[]{config.getHashKey(), queueKey, key};
    String[] args = new String[]{hashValue, rawMessage.getExecuteTime() + ""};

    if (asyncExecute) {
      dqRedis.asyncEval(LuaScriptConst.PUSH_MESSAGE, ScriptOutputType.INTEGER, keys, args)
          .thenApply(NULL -> key)
          .whenComplete(action);
    } else {
      Long result = dqRedis
          .syncEval(LuaScriptConst.PUSH_MESSAGE, ScriptOutputType.INTEGER, keys, args);
      log.debug("sync push result: {}", result);
    }
  }

  private RawMessage buildTask(String key, Message message) {
    long delayTime = message.getTimeUnit().toSeconds(message.getDelayTime());

    long now = Instant.now().getEpochSecond();

    long executeTime = now + delayTime;

    RawMessage rawMessage = new RawMessage();
    rawMessage.setKey(key);
    rawMessage.setCreateTime(now);
    rawMessage.setExecuteTime(executeTime);
    rawMessage.setTopic(message.getTopic());
    Class<? extends Serializable> type = message.getMsg().getClass();
    if (ClassUtil.isBasicType(type)) {
      rawMessage.setMsg(message.getMsg().toString());
    } else {
      rawMessage.setMsg(GsonUtil.toJson(message.getMsg()));
    }
    rawMessage.setMaxRetries(message.getRetries());
    rawMessage.setHasRetries(0);
    return rawMessage;
  }

  private void checkQueue(String queueName, Message message) throws RDQException {
    if (null == queueName || queueName.isEmpty()) {
      throw new RDQException("queue name can not be empty.");
    }

    if (null == message) {
      throw new RDQException("message can not be null.");
    }

    if (null == message.getMsg()) {
      throw new RDQException("message msg can not be null.");
    }
  }

  public void subscribe(String topic, Callback callback) {
    log.info("listen the topic [{}]", topic);
    config.getCallbacks().put(topic, callback);
  }

  public void shutdown() {
    dqRedis.shutdown();
  }

}
