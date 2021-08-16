package cn.flood.pulsar.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface PulsarProducer {

    String topic();

    Class<?> msgClass();

    String producerName();
}
