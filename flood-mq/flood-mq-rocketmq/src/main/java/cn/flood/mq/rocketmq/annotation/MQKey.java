package cn.flood.mq.rocketmq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来标识作为消息key的字段 prefix 会作为前缀拼到字段值前面
 *
 * @author suclogger
 * @since 0.0.4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MQKey {

  String prefix() default "";

  String field();
}
