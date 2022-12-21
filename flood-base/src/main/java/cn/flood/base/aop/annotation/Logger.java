package cn.flood.base.aop.annotation;


import java.lang.annotation.*;

/**
 * 用于log打印
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Logger {

}
