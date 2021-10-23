package cn.flood.cloud.fegin;


import cn.flood.cloud.version.FloodSpringMvcContract;
import feign.Contract;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

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
@Slf4j
public class FeignProtoSupportConfig {


    private static final String PROTO_TYPE = "prototype";
    //Autowire the message converters.
    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    //override the encoder
    @Bean
    @Primary
    public Encoder springEncoder(){
        return new SpringEncoder(this.messageConverters);
    }

    //override the encoder
    @Bean
    public Decoder springDecoder(){
        return new ResponseEntityDecoder(new SpringDecoder(this.messageConverters));
    }

    /**
     * 覆盖FeignClientsConfiguration默认
     */
    @Bean
    @Primary
    public Contract feignContract() {
        return new FloodSpringMvcContract();
    }

}
