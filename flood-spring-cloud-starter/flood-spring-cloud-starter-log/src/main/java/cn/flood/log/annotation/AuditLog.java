package cn.flood.log.annotation;

import cn.flood.log.enums.ActionEnum;

import java.lang.annotation.*;

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
     * 是否记录请求参数
//     * @return
     */
    boolean isRequestParam() default true;

    /**
     * 操作类型 1 添加 2 修改 3 删除
//     * @return
     */
    ActionEnum actionType() default ActionEnum.OTHER;
}
