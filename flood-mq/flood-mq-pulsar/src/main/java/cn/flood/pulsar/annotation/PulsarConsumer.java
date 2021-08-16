package cn.flood.pulsar.annotation;

import org.apache.pulsar.client.api.SubscriptionType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface PulsarConsumer {

    String topic();

    Class<?> msgClass();

    String consumerName();

    String subscriptionName();

    SubscriptionType subscriptionType() default SubscriptionType.Exclusive;

    int receiveThreads() default 10;
}
