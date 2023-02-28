package cn.flood.cloud.log.annotation;

import cn.flood.cloud.log.enums.ActionEnum;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {

  /**
   * 操作信息
   */
  String operation();

  /**
   * 是否记录请求参数 //     * @return
   */
  boolean isRequestParam() default true;

  /**
   * 操作类型 1 添加 2 修改 3 删除 //     * @return
   */
  ActionEnum actionType() default ActionEnum.OTHER;
}
