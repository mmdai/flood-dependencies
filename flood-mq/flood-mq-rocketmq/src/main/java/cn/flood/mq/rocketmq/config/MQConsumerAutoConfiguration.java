package cn.flood.mq.rocketmq.config;

import cn.flood.mq.rocketmq.annotation.MQConsumer;
import cn.flood.mq.rocketmq.base.AbstractMQPushConsumer;
import cn.flood.mq.rocketmq.base.MessageExtConst;
import cn.flood.mq.rocketmq.trace.common.OnsTraceConstants;
import cn.flood.mq.rocketmq.trace.dispatch.impl.AsyncTraceAppender;
import cn.flood.mq.rocketmq.trace.dispatch.impl.AsyncTraceDispatcher;
import cn.flood.mq.rocketmq.trace.tracehook.OnsConsumeMessageHookImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.consumer.DefaultMQPushConsumerImpl;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * Created by suclogger on 2017/6/28. 自动装配消息消费者
 */
@SuppressWarnings("unchecked")
@AutoConfiguration
@ConditionalOnBean(MQBaseAutoConfiguration.class)
public class MQConsumerAutoConfiguration extends MQBaseAutoConfiguration implements
    InitializingBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(MQConsumerAutoConfiguration.class);

  private AsyncTraceDispatcher asyncTraceDispatcher;
  // 维护一份map用于检测是否用同样的consumerGroup订阅了不同的topic+tag
  private Map<String, String> validConsumerMap;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.init();
  }

  public void init() throws Exception {
    Map<String, Object> beans = applicationContext.getBeansWithAnnotation(MQConsumer.class);
    if (!CollectionUtils.isEmpty(beans) && mqProperties.getTraceEnabled()) {
      initAsyncAppender();
    }
    validConsumerMap = new HashMap<>();
    for (Map.Entry<String, Object> entry : beans.entrySet()) {
      publishConsumer(entry.getKey(), entry.getValue());
    }
    // 清空map，等待回收
    validConsumerMap = null;
  }

  private AsyncTraceDispatcher initAsyncAppender() {
    if (asyncTraceDispatcher != null) {
      return asyncTraceDispatcher;
    }
    try {
      Properties tempProperties = new Properties();
      tempProperties.put(OnsTraceConstants.MaxMsgSize, "128000");
      tempProperties.put(OnsTraceConstants.AsyncBufferSize, "2048");
      tempProperties.put(OnsTraceConstants.MaxBatchNum, "1");
      tempProperties.put(OnsTraceConstants.WakeUpNum, "1");
      tempProperties.put(OnsTraceConstants.NAMESRV_ADDR, mqProperties.getNameServerAddress());
      tempProperties.put(OnsTraceConstants.InstanceName, UUID.randomUUID().toString());
      AsyncTraceAppender asyncAppender = new AsyncTraceAppender(tempProperties);
      asyncTraceDispatcher = new AsyncTraceDispatcher(tempProperties);
      asyncTraceDispatcher.start(asyncAppender, "DEFAULT_WORKER_NAME");
    } catch (MQClientException e) {
      e.printStackTrace();
    }
    return asyncTraceDispatcher;
  }

  private void publishConsumer(String beanName, Object bean) throws Exception {
    MQConsumer mqConsumer = applicationContext.findAnnotationOnBean(beanName, MQConsumer.class);
    if (ObjectUtils.isEmpty(mqProperties.getNameServerAddress())) {
      throw new RuntimeException("name server address must be defined");
    }
    Assert.notNull(mqConsumer.consumerGroup(), "consumer's consumerGroup must be defined");
    Assert.notNull(mqConsumer.topic(), "consumer's topic must be defined");
    if (!AbstractMQPushConsumer.class.isAssignableFrom(bean.getClass())) {
      throw new RuntimeException(bean.getClass().getName() + " - consumer未实现Consumer抽象类");
    }
    Environment environment = applicationContext.getEnvironment();

    String consumerGroup = environment.resolvePlaceholders(mqConsumer.consumerGroup());
    String topic = environment.resolvePlaceholders(mqConsumer.topic());
    String tags = "*";
    if (mqConsumer.tag().length == 1) {
      tags = environment.resolvePlaceholders(mqConsumer.tag()[0]);
    } else if (mqConsumer.tag().length > 1) {
      tags = StringUtils.join(mqConsumer.tag(), "||");
    }

    // 检查consumerGroup
    if (!ObjectUtils.isEmpty(validConsumerMap.get(consumerGroup))) {
      String exist = validConsumerMap.get(consumerGroup);
      throw new RuntimeException(
          "消费组重复订阅，请新增消费组用于新的topic和tag组合: " + consumerGroup + "已经订阅了" + exist);
    } else {
      validConsumerMap.put(consumerGroup, topic + "-" + tags);
    }

    // 配置push consumer
    if (AbstractMQPushConsumer.class.isAssignableFrom(bean.getClass())) {
      DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
      consumer.setNamesrvAddr(mqProperties.getNameServerAddress());
      consumer.setMessageModel(MessageModel.valueOf(mqConsumer.messageMode()));
      consumer.subscribe(topic, tags);
      consumer.setInstanceName(UUID.randomUUID().toString());
      consumer.setVipChannelEnabled(mqProperties.getVipChannelEnabled());
      // 设置最大重试次数
      consumer.setMaxReconsumeTimes(mqProperties.getMaxReconsumeTimes());
      AbstractMQPushConsumer abstractMQPushConsumer = (AbstractMQPushConsumer) bean;
      if (MessageExtConst.CONSUME_MODE_CONCURRENTLY.equals(mqConsumer.consumeMode())) {
        if (MessageExtConst.DEDUP_DISABLE == mqConsumer.dedup()) {
          consumer.registerMessageListener(
              (List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) ->
                  abstractMQPushConsumer.dealMessage(list, consumeConcurrentlyContext));
        } else {
          //注入幂等逻辑
          consumer.registerMessageListener(
              (List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) ->
                  abstractMQPushConsumer.dealDedupMessage(list, consumeConcurrentlyContext));
        }
      } else if (MessageExtConst.CONSUME_MODE_ORDERLY.equals(mqConsumer.consumeMode())) {
        consumer.registerMessageListener(
            (List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) ->
                abstractMQPushConsumer.dealMessage(list, consumeOrderlyContext));
      } else {
        throw new RuntimeException("unknown consume mode ! only support CONCURRENTLY and ORDERLY");
      }
      abstractMQPushConsumer.setConsumer(consumer);
      // 为Consumer增加消息轨迹回发模块
      if (mqProperties.getTraceEnabled()) {
        try {
          DefaultMQPushConsumerImpl mqPushConsumer = new DefaultMQPushConsumerImpl(consumer, null);
          mqPushConsumer.registerConsumeMessageHook(
              new OnsConsumeMessageHookImpl(asyncTraceDispatcher));
        } catch (Throwable e) {
          LOGGER.error("system mqtrace hook init failed ,maybe can't send msg trace data");
        }
      }

      consumer.start();
    }

    LOGGER.info(String.format("%s is ready to subscribe message", bean.getClass().getName()));
  }

}