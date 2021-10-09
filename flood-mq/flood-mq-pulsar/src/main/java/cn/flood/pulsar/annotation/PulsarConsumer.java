package cn.flood.pulsar.annotation;

import org.apache.pulsar.client.api.SubscriptionType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface PulsarConsumer {

    String consumerName() default "";

    Class<?> msgClass();

    String msgClassName() default "";

    int receiveThreads() default 10;

    String subscriptionName();

    SubscriptionType subscriptionType() default SubscriptionType.Exclusive;

    String subscriptionTypeName() default "";

    String topic();

}
