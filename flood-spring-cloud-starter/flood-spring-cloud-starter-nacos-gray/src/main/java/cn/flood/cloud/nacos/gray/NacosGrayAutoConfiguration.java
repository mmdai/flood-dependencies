package cn.flood.cloud.nacos.gray;


import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 在服务注册至nacos客户端时，在服务详情中增加相应的元数据，增加服务注册时间,版本号header
 * @author mmdai
 * @version 1.0
 * @date 2022/4/28 15:57
 */
@AutoConfiguration
@ComponentScan(basePackages = {"cn.flood.cloud.nacos.gray.controller"})
public class NacosGrayAutoConfiguration {

}
