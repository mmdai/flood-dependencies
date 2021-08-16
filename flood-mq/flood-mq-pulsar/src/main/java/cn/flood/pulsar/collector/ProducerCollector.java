package cn.flood.pulsar.collector;

import cn.flood.pulsar.annotation.PulsarProducer;
import cn.flood.pulsar.properties.PulsarProperties;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

@Component
public class ProducerCollector implements BeanPostProcessor {

    @Autowired
    private PulsarProperties pulsarProperties;

    @Autowired
    private PulsarClient pulsarClient;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            PulsarProducer pulsarProducer = field.getAnnotation(PulsarProducer.class);
            if (null != pulsarProducer)
                fieldSetValue(bean, field, pulsarProducer);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void fieldSetValue(Object bean, Field field, PulsarProducer pulsarProducer) throws BeansException {
        try {
            field.setAccessible(true);
            field.set(bean, this.pulsarClient.newProducer((Schema) JSONSchema.of(pulsarProducer.msgClass()))
                    .producerName(pulsarProducer.producerName())
                    .topic(pulsarProducer.topic())
                    .batchingMaxMessages(1024)
                    .batchingMaxPublishDelay(100L, TimeUnit.MILLISECONDS)
                    .enableBatching(true)
                    .blockIfQueueFull(true)
                    .maxPendingMessages(512)
                    .sendTimeout(this.pulsarProperties.getSendTimeout().intValue(), TimeUnit.MILLISECONDS)
                    .create());
        } catch (PulsarClientException pce) {
            throw new BeanInstantiationException(bean.getClass(), String.format("初始化%s字段失败", new Object[] { field.getName() }));
        } catch (IllegalAccessException iae) {
            throw new BeanInstantiationException(bean.getClass(), String.format("初始化%s字段失败", new Object[] { field.getName() }));
        }
    }

}
