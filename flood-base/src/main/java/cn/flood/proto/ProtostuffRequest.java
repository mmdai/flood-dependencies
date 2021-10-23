package cn.flood.proto;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})  //作用范围
@Retention(RetentionPolicy.RUNTIME)  //生效时期
@Documented  //文档化
public @interface ProtostuffRequest {
}
