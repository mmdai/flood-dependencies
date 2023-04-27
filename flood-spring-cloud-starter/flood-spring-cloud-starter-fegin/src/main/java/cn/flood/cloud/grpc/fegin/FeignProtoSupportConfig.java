package cn.flood.cloud.grpc.fegin;

import cn.flood.cloud.grpc.version.ProtoSpringMvcContract;
import feign.Contract;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * <p>Title: FeignProtoSupportConfig</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2020</p>
 *
 * @author mmdai
 * @version 1.0
 * @date 2020/8/27
 */
@Configuration
public class FeignProtoSupportConfig {

  private static final String PROTO_TYPE = "prototype";
  private final Logger log = LoggerFactory.getLogger(this.getClass());
  /**
   * Autowire the message converters.
   */
  @Autowired
  private ObjectFactory<HttpMessageConverters> messageConverters;
  @Autowired
  private ObjectProvider<HttpMessageConverterCustomizer> customizers;

  /**
   * override the encoder
   *
   * @return
   */
  @Bean
  @Primary
  public Encoder springEncoder() {
    return new SpringEncoder(this.messageConverters);
  }

  /**
   * override the encoder
   *
   * @return
   */
  @Bean
  public Decoder springDecoder() {
    return new ResponseEntityDecoder(new SpringDecoder(this.messageConverters, customizers));
  }

  /**
   * 覆盖FeignClientsConfiguration默认
   */
  @Bean
  @Primary
  public Contract feignContract() {
    return new ProtoSpringMvcContract();
  }

}
