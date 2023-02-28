package cn.flood.cloud.grpc.fegin;


import cn.flood.cloud.grpc.fegin.retryer.FloodErrorDecoder;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

/**
 * <p>Title: FeignProtoSupportConfig</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2020</p>
 *
 * @author mmdai
 * @version 1.0
 * @date 2020/8/27
 */
public class FeignProtoRetrySupportConfig extends FeignProtoSupportConfig {

  /**
   * 自定义重试机制
   *
   * @return
   */
  @Bean
  public Retryer feignRetryer() {
    //最大请求次数为5， 初始间隔时间为100ms，下次间隔时间1.5倍递增，重试间最大间隔时间为1s，
    return new Retryer.Default();
  }

  /**
   * 自定义重试错误码
   *
   * @return
   */
  @Bean
  public ErrorDecoder errorDecoder() {
    return new FloodErrorDecoder();
  }

}
