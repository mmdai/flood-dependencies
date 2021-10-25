package cn.flood.rocketmq.config;

import cn.flood.rocketmq.annotation.MQTransactionProducer;
import cn.flood.rocketmq.base.AbstractMQTransactionProducer;
import cn.flood.rocketmq.base.RocketMQTemplate;
import lombok.Setter;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by yipin on 2017/6/29.
 * 自动装配消息生产者
 */
@Configuration
@ConditionalOnBean(MQBaseAutoConfiguration.class)
public class MQProducerAutoConfiguration extends MQBaseAutoConfiguration {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Setter
    private static DefaultMQProducer producer;

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
            producer.setRetryTimesWhenSendFailed(3);
            //异步发送失败重试次数
            producer.setRetryTimesWhenSendAsyncFailed(3);
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
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(MQTransactionProducer.class);
        if (CollectionUtils.isEmpty(beans)) {
            return;
        }
        ExecutorService executorService = new ThreadPoolExecutor(beans.size(), beans.size() * 2, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
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
                AbstractMQTransactionProducer beanObj = AbstractMQTransactionProducer.class.cast(transactionProducer.getValue());
                MQTransactionProducer anno = beanObj.getClass().getAnnotation(MQTransactionProducer.class);

                TransactionMQProducer producer = new TransactionMQProducer(environment.resolvePlaceholders(anno.producerGroup()));
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
