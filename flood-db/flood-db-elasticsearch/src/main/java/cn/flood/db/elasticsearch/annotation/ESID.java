package cn.flood.db.elasticsearch.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * program: esdemo description: ES entity 标识ID的注解,在es entity field上添加 author: X-Pacific zhang
 * create: 2019-01-18 16:092
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface ESID {

}
