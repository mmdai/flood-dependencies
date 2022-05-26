/**  
* <p>Title: FeignMultipartSupportConfig.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月27日  
* @version 1.0  
*/  
package cn.flood.cloud.fegin;

import cn.flood.cloud.fegin.retryer.FloodErrorDecoder;
import cn.flood.cloud.version.FloodSpringMvcContract;
import feign.Contract;
import feign.Retryer;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

/**  
* <p>Title: FeignMultipartSupportConfig</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2018年12月27日  
*/
public class FeignMultipartSupportConfig {

	/**
	 * 覆盖FeignClientsConfiguration默认
	 */
	@Bean
	public Contract feignContract() {
		return new FloodSpringMvcContract();
	}

	@Autowired
	private ObjectFactory<HttpMessageConverters> messageConverters;

	@Bean
	@Primary
	@Scope("prototype")
	public Encoder multipartFormEncoder() {
		return new SpringFormEncoder(new SpringEncoder(messageConverters));
	}

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
