package cn.flood.pulsar.collector;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cn.flood.pulsar.annotation.PulsarConsumer;
import cn.flood.pulsar.exception.PulsarConsumerThreadCountOverflowException;
import cn.flood.pulsar.properties.PulsarProperties;
import cn.flood.pulsar.util.PulsarAnnotationUtils;
import cn.flood.pulsar.util.RandomNameUtils;
import cn.flood.pulsar.util.SchemaUtils;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.shade.org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;


@SuppressWarnings("unchecked")
public class ConsumerCollector implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(ConsumerCollector.class);
    private ExecutorService cachedThreadPool;
    private AtomicInteger consumeTaskCounter = new AtomicInteger(0);
    private Environment environment;
    private PulsarClient pulsarClient;
    private PulsarProperties pulsarProperties;

    public void setPulsarProperties(PulsarProperties pulsarProperties2) {
        this.pulsarProperties = pulsarProperties2;
    }

    public void setPulsarClient(PulsarClient pulsarClient2) {
        this.pulsarClient = pulsarClient2;
    }

    public void setCachedThreadPool(ExecutorService cachedThreadPool2) {
        this.cachedThreadPool = cachedThreadPool2;
    }

    public void setEnvironment(Environment environment2) {
        this.environment = environment2;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Method[] declaredMethods = bean.getClass().getDeclaredMethods();
        for (Method method : declaredMethods) {
            PulsarConsumer pulsarConsumer = (PulsarConsumer) method.getAnnotation(PulsarConsumer.class);
            if (pulsarConsumer != null) {
                newConsumer(bean, method, pulsarConsumer);
            }
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void newConsumer(final Object bean, final Method method, PulsarConsumer pulsarConsumer) throws BeansException {
        try {
            String consumerName = pulsarConsumer.consumerName();
            if (!StringUtils.isNotBlank(consumerName)) {
                consumerName = RandomNameUtils.generateConsumerName();
            }
            final Consumer consumer = this.pulsarClient.newConsumer(SchemaUtils.schemaInstance(PulsarAnnotationUtils.getMsgClass(this.environment, pulsarConsumer.msgClassName(), pulsarConsumer.msgClass()))).consumerName(this.environment.resolvePlaceholders(consumerName)).topic(new String[]{this.environment.resolvePlaceholders(pulsarConsumer.topic())}).subscriptionName(this.environment.resolvePlaceholders(pulsarConsumer.subscriptionName())).ackTimeout((long) this.pulsarProperties.getAckTimeout().intValue(), TimeUnit.MILLISECONDS).subscriptionType(PulsarAnnotationUtils.getSubscriptionType(this.environment, pulsarConsumer.subscriptionTypeName(), pulsarConsumer.subscriptionType())).receiverQueueSize(this.pulsarProperties.getReceiverQueueSize().intValue()).subscribe();
            method.setAccessible(true);
            for (int i = 0; i < pulsarConsumer.receiveThreads(); i++) {
                if (this.consumeTaskCounter.incrementAndGet() > PulsarProperties.DEFAULT_CONSUMER_THREAD_POOL_MAX_SIZE) {
                    log.error("Pulsar Consumer total Thread count greater than {}", Integer.valueOf(PulsarProperties.DEFAULT_CONSUMER_THREAD_POOL_MAX_SIZE));
                    throw new PulsarConsumerThreadCountOverflowException();
                } else {
                    this.cachedThreadPool.execute(new Runnable() {
                        /* class com.yumchina.architecture.framework.starter.pulsar.collector.ConsumerCollector.AnonymousClass1 */

                        public void run() {
                            Message msg = null;
                            while (true) {
                                try {
                                    msg = consumer.receive();
                                    method.invoke(bean, msg.getValue());
                                    consumer.acknowledge(msg.getMessageId());
                                } catch (Exception e) {
                                    consumer.negativeAcknowledge(msg.getMessageId());
                                    ConsumerCollector.log.error("{}->消息处理失败(unAck) Msg:{}", method.getName(), msg.getValue().toString());
                                }
                            }
                        }
                    });
                }
            }
        } catch (PulsarClientException e) {
            throw new BeanInstantiationException(bean.getClass(), String.format("初始化%s失败", method.getName()));
        }
    }

}
