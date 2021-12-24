package cn.flood.pulsar.collector;

import cn.flood.pulsar.annotation.PulsarProducer;
import cn.flood.pulsar.exception.PulsarProducerTypeCheckException;
import cn.flood.pulsar.properties.PulsarProperties;
import cn.flood.pulsar.util.PulsarAnnotationUtils;
import cn.flood.pulsar.util.RandomNameUtils;
import cn.flood.pulsar.util.SchemaUtils;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.shade.org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;


import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unchecked")
public class ProducerCollector implements BeanPostProcessor {

    private Environment environment;
    private PulsarClient pulsarClient;
    private PulsarProperties pulsarProperties;

    public void setPulsarProperties(PulsarProperties pulsarProperties2) {
        this.pulsarProperties = pulsarProperties2;
    }

    public void setPulsarClient(PulsarClient pulsarClient2) {
        this.pulsarClient = pulsarClient2;
    }

    public void setEnvironment(Environment environment2) {
        this.environment = environment2;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            PulsarProducer pulsarProducer = (PulsarProducer) field.getAnnotation(PulsarProducer.class);
            if (pulsarProducer != null) {
                if (field.getType() == Producer.class) {
                    fieldSetValue(bean, field, pulsarProducer);
                } else {
                    throw new PulsarProducerTypeCheckException("PulsarProducer can only be used in org.apache.pulsar.client.api.Producer type field.");
                }
            }
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void fieldSetValue(Object bean, Field field, PulsarProducer pulsarProducer) throws BeansException {
        try {
            String producerName = pulsarProducer.producerName();
            if (!StringUtils.isNotBlank(producerName)) {
                producerName = RandomNameUtils.generateProducerName();
            }
            Class msgClass = PulsarAnnotationUtils.getMsgClass(this.environment, pulsarProducer.msgClassName(), pulsarProducer.msgClass());
            field.setAccessible(true);
            field.set(bean, this.pulsarClient.newProducer(SchemaUtils.schemaInstance(msgClass)).producerName(this.environment.resolvePlaceholders(producerName)).topic(this.environment.resolvePlaceholders(pulsarProducer.topic())).batchingMaxMessages(1024).batchingMaxPublishDelay(100, TimeUnit.MILLISECONDS).enableBatching(true).blockIfQueueFull(true).maxPendingMessages(512).sendTimeout(this.pulsarProperties.getSendTimeout().intValue(), TimeUnit.MILLISECONDS).create());
        } catch (PulsarClientException e) {
            throw new BeanInstantiationException(bean.getClass(), String.format("初始化%s字段失败", field.getName()));
        } catch (IllegalAccessException e2) {
            throw new BeanInstantiationException(bean.getClass(), String.format("初始化%s字段失败", field.getName()));
        }
    }


}
