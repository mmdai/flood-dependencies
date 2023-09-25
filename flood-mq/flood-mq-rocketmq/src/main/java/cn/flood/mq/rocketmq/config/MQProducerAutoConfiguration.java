package cn.flood.mq.rocketmq.config;

import cn.flood.mq.rocketmq.annotation.MQTransactionProducer;
import cn.flood.mq.rocketmq.base.AbstractMQTransactionProducer;
import cn.flood.mq.rocketmq.base.RocketMQTemplate;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * Created by yipin on 2017/6/29. 自动装配消息生产者
 */
@AutoConfiguration
@ConditionalOnBean(MQBaseAutoConfiguration.class)
public class MQProducerAutoConfiguration extends MQBaseAutoConfiguration {

  @Setter
  private static DefaultMQProducer producer;
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Bean
  public DefaultMQProducer exposeProducer() throws Exception {
    if (!mqProperties.getExistProducer()) {
      return null;
    }
    if (producer == null) {
      Assert.notNull(mqProperties.getProducerGroup(), "producer group must be defined");
      Assert.notNull(mqProperties.getNameServerAddress(), "name server address must be defined");
      producer = new DefaultMQProducer(mqProperties.getProducerGroup());
      producer.setNamesrvAddr(mqProperties.getNameServerAddress());
      producer.setSendMsgTimeout(mqProperties.getSendMsgTimeout());
      producer.setSendMessageWithVIPChannel(mqProperties.getVipChannelEnabled());
      //消息发送失败重试次数
      producer.setRetryTimesWhenSendFailed(mqProperties.getRetryTimesWhenSendFailed());
      //异步发送失败重试次数
      producer.setRetryTimesWhenSendAsyncFailed(mqProperties.getRetryTimesWhenSendAsyncFailed());
      //消息没有发送成功，是否发送到另一个Broker中
//            producer.setRetryAnotherBrokerWhenNotStoreOK(true);
      producer.start();
    }
    return producer;
  }

  @Bean
  public RocketMQTemplate exposeTemplate() {
    return new RocketMQTemplate();
  }

  @PostConstruct
  public void configTransactionProducer() {
    Map<String, Object> beans = applicationContext
        .getBeansWithAnnotation(MQTransactionProducer.class);
    if (CollectionUtils.isEmpty(beans)) {
      return;
    }
    ExecutorService executorService = new ThreadPoolExecutor(beans.size(), beans.size() * 2, 100,
        TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
      @Override
      public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("client-transaction-msg-check-thread");
        return thread;
      }
    });
    Environment environment = applicationContext.getEnvironment();
    beans.entrySet().forEach(transactionProducer -> {
      try {
        AbstractMQTransactionProducer beanObj = AbstractMQTransactionProducer.class
            .cast(transactionProducer.getValue());
        MQTransactionProducer anno = beanObj.getClass().getAnnotation(MQTransactionProducer.class);

        TransactionMQProducer producer = new TransactionMQProducer(
            environment.resolvePlaceholders(anno.producerGroup()));
        producer.setNamesrvAddr(mqProperties.getNameServerAddress());
        producer.setExecutorService(executorService);
        producer.setTransactionListener(beanObj);
        producer.start();
        beanObj.setProducer(producer);
      } catch (Exception e) {
        log.error("build transaction producer error : {}", e);
      }
    });
  }
}
