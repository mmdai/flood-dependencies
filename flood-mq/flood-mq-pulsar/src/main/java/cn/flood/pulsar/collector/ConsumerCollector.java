package cn.flood.pulsar.collector;

import cn.flood.pulsar.annotation.PulsarConsumer;
import cn.flood.pulsar.properties.PulsarProperties;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ConsumerCollector implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(ConsumerCollector.class);

    @Autowired
    private PulsarProperties pulsarProperties;

    @Autowired
    private PulsarClient pulsarClient;

    @Autowired
    @Qualifier("consumerCachedThreadPool")
    ExecutorService cachedThreadPool;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        for (Method method : beanClass.getDeclaredMethods()) {
            PulsarConsumer pulsarConsumer = method.getAnnotation(PulsarConsumer.class);
            if (null != pulsarConsumer)
                newConsumer(bean, method, pulsarConsumer);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException  {
        return bean;
    }

    private void newConsumer(final Object bean, final Method method, PulsarConsumer pulsarConsumer) throws BeansException {
        try {
            final Consumer consumer = this.pulsarClient.newConsumer((Schema) JSONSchema.of(pulsarConsumer.msgClass())).
                    consumerName(pulsarConsumer.consumerName()).
                    topic(new String[] { pulsarConsumer.topic() }).
                    subscriptionName(pulsarConsumer.subscriptionName()).
                    ackTimeout(this.pulsarProperties.getAckTimeout().intValue(), TimeUnit.MILLISECONDS).
                    subscriptionType(pulsarConsumer.subscriptionType()).
                    receiverQueueSize(this.pulsarProperties.getReceiverQueueSize().intValue()).subscribe();
            method.setAccessible(true);
            for (int i = 0; i < pulsarConsumer.receiveThreads(); i++) {
                this.cachedThreadPool.execute(()-> {
                    Message msg = null;
                    try {
                        msg = consumer.receive();
                        method.invoke(bean, new Object[] { msg.getValue() });
                        consumer.acknowledge(msg.getMessageId());
                    } catch (Exception ex) {
                        consumer.negativeAcknowledge(msg.getMessageId());
                        log.error("{}->Msg:{}", method.getName(), msg.getValue().toString());
                    }
                });
            }
        } catch (PulsarClientException pce) {
            throw new BeanInstantiationException(bean.getClass(), String.format("初始化%s字段失败", new Object[] { method.getName() }));
        }
    }
}
