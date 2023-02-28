package cn.flood.db.elasticsearch.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * program: esdemo description: ES entity 标识匹配率的注解,在es entity field上添加
 *
 * @author: junpei.deng create: 2022-04-15 16:092
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface ESScore {

}
