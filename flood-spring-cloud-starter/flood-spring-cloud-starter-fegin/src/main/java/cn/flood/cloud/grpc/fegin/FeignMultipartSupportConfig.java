/**  
* <p>Title: FeignMultipartSupportConfig.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月27日  
* @version 1.0  
*/  
package cn.flood.cloud.grpc.fegin;

import cn.flood.cloud.grpc.version.FloodSpringMvcContract;
import feign.Contract;
import feign.Retryer;
import feign.codec.Encoder;
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
	 * 默认不重试
	 * @return
	 */
	@Bean
	public Retryer feignRetryer() {
		return Retryer.NEVER_RETRY;
	}



}
