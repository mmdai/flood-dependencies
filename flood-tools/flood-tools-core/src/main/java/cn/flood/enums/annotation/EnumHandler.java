package cn.flood.enums.annotation;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.*;

@IndexAnnotated
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnumHandler {

    String value() default "code";

}
