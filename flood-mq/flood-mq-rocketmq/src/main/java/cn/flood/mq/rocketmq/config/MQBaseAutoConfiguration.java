package cn.flood.mq.rocketmq.config;

import cn.flood.mq.rocketmq.annotation.EnableMQConfiguration;
import cn.flood.mq.rocketmq.base.AbstractMQProducer;
import cn.flood.mq.rocketmq.base.AbstractMQPushConsumer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by yipin on 2017/6/28. RocketMQ配置文件
 */
@AutoConfiguration
@ConditionalOnBean(annotation = EnableMQConfiguration.class)
@AutoConfigureAfter({AbstractMQProducer.class, AbstractMQPushConsumer.class})
@EnableConfigurationProperties(MQProperties.class)
public class MQBaseAutoConfiguration implements ApplicationContextAware {

  protected MQProperties mqProperties;
  protected ConfigurableApplicationContext applicationContext;

  @Autowired
  public void setMqProperties(MQProperties mqProperties) {
    this.mqProperties = mqProperties;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = (ConfigurableApplicationContext) applicationContext;
  }

}

