package cn.flood.db.elasticsearch.annotation;

import cn.flood.db.elasticsearch.ElasticSearchClientAutoConfiguration;
import cn.flood.db.elasticsearch.auto.ESCRegistrar;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * program: esdemo description: springboot启动类配置该注解，该注解有两个作用 作用1：引入自动配置的restHighLevelClient
 * 作用2：配置entityPath以识别es entity自动创建索引以及mapping（如果不配置，则按照Main方法的路径包进行扫描）
 * 作用3：引入ESToolsRegistrar准备实现切面实现接口的功能（参考JPA实现） author: X-Pacific zhang create: 2019-01-24 11:45
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ESCRegistrar.class, ElasticSearchClientAutoConfiguration.class})
public @interface EnableESTools {

  /**
   * 配置repository包路径,如果不配置，则按照Main方法的路径包进行扫描
   *
   * @return
   */
  String[] basePackages() default {};

  /**
   * 配置repository包路径,如果不配置，则按照Main方法的路径包进行扫描
   *
   * @return
   */
  String[] value() default {};

  /**
   * entity路径配置,如果不配置，则按照Main方法的路径包进行扫描
   */
  String[] entityPath() default {};

  /**
   * 是否打印注册信息
   *
   * @return
   */
  boolean printregmsg() default false;
}
