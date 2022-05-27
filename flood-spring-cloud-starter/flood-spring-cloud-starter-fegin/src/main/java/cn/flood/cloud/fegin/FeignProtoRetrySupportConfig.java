package cn.flood.cloud.fegin;


import cn.flood.cloud.fegin.retryer.FloodErrorDecoder;
import cn.flood.cloud.version.FloodSpringMvcContract;
import feign.Contract;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
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
public class FeignProtoRetrySupportConfig extends FeignProtoSupportConfig {

    /**
     * 自定义重试机制
     * @return
     */
    @Bean
    public Retryer feignRetryer() {
        //最大请求次数为5， 初始间隔时间为100ms，下次间隔时间1.5倍递增，重试间最大间隔时间为1s，
        return new Retryer.Default();
    }

    /**
     * 自定义重试错误码
     * @return
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FloodErrorDecoder();
    }

}
