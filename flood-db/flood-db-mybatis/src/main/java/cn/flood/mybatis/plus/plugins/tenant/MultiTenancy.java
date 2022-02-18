package cn.flood.mybatis.plus.plugins.tenant;

import cn.flood.mybatis.plus.plugins.tenant.impl.TenancyQueryValue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多租户查询注解
 *
 * @Author iloveoverfly
 * @LocalDateTime 2020/6/15 13:43
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface MultiTenancy {

    /**
     * 是否可以过滤
     */
    boolean isFiltered() default true;

    /**
     * 数据库表前缀名
     */
    String preTableName() default "";

    /**
     * 过滤条件查询值Factory
     */
    Class<? extends MultiTenancyQueryValueFactory> multiTenancyQueryValueFactory() default TenancyQueryValue.class;
}
