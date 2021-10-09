package cn.flood.pulsar.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PulsarProducer {

    Class<?> msgClass();

    String msgClassName() default "";

    String producerName() default "";

    String topic();

}
