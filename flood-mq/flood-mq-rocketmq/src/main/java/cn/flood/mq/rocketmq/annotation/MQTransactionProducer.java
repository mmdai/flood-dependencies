package cn.flood.mq.rocketmq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * Created by pufang on 2018/7/26. RocketMQ事务消息生产者
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MQTransactionProducer {

  /**
   * *重要* 事务的反查是基于同一个producerGroup为维度
   */
  String producerGroup();
}
