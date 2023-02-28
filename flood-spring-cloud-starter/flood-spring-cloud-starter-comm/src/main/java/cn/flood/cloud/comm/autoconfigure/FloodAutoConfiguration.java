package cn.flood.cloud.comm.autoconfigure;

import cn.flood.cloud.comm.service.ValidCodeService;
import cn.flood.cloud.comm.service.impl.ValidCodeServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
//@ComponentScan(basePackages = {"cn.flood.base.aop",
//"cn.flood.filter","cn.flood.base.handle"})
public class FloodAutoConfiguration {

  @Bean
  public ValidCodeService validCodeService() {
    return new ValidCodeServiceImpl();
  }

}
