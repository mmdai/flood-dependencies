/**
 * Copyright (c) 2018-2028,
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.flood.oauth.configuration.client.restTempate;


import cn.flood.constants.AppConstant;
import cn.flood.exception.CoreException;
import cn.flood.exception.enums.GlobalErrorCodeEnum;
import cn.flood.proto.converter.ProtostuffHttpMessageConverter;
import cn.flood.utils.Charsets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.commons.httpclient.OkHttpClientConnectionPoolFactory;
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Http RestTemplateHeaderInterceptor 配置
 *
 * @author mmdai
 */
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@ConditionalOnClass(okhttp3.OkHttpClient.class)
@Slf4j
public class RestTemplateConfiguration {

	/**
	 * dev, test 环境打印出BODY
	 * @return HttpRestLoggingInterceptor
	 */
	@Bean("httpRestLoggingInterceptor")
	@Profile({AppConstant.DEV_CODE, AppConstant.TEST_CODE})
	public HttpRestLoggingInterceptor testLoggingInterceptor() {
		HttpRestLoggingInterceptor interceptor = new HttpRestLoggingInterceptor(new OkHttpSlf4jLogger());
		interceptor.setLevel(HttpRestLoggingInterceptor.Level.BODY);
		return interceptor;
	}

	/**
	 * uat 环境 打印 请求头
	 * @return HttpRestLoggingInterceptor
	 */
	@Bean("httpRestLoggingInterceptor")
	@Profile(AppConstant.UAT_CODE)
	public HttpRestLoggingInterceptor onTestLoggingInterceptor() {
		HttpRestLoggingInterceptor interceptor = new HttpRestLoggingInterceptor(new OkHttpSlf4jLogger());
		interceptor.setLevel(HttpRestLoggingInterceptor.Level.HEADERS);
		return interceptor;
	}

	/**
	 * prod 环境只打印请求url
	 * @return HttpRestLoggingInterceptor
	 */
	@Bean("httpRestLoggingInterceptor")
	@Profile(AppConstant.PROD_CODE)
	public HttpRestLoggingInterceptor prodLoggingInterceptor() {
		HttpRestLoggingInterceptor interceptor = new HttpRestLoggingInterceptor(new OkHttpSlf4jLogger());
		interceptor.setLevel(HttpRestLoggingInterceptor.Level.BASIC);
		return interceptor;
	}


	@Bean
	@ConditionalOnMissingBean(HttpRestLoggingInterceptor.class)
	public HttpRestLoggingInterceptor httpRestLoggingInterceptor() {
		return new HttpRestLoggingInterceptor();
	}
	/**
	 * okhttp3 链接池配置
	 * @param connectionPoolFactory 链接池配置
	 * @param httpClientProperties httpClient配置
	 * @return okhttp3.ConnectionPool
	 */
	@Bean
	@ConditionalOnMissingBean(okhttp3.ConnectionPool.class)
	public okhttp3.ConnectionPool httpClientConnectionPool(
		HttpClientProperties httpClientProperties,
		OkHttpClientConnectionPoolFactory connectionPoolFactory) {
		Integer maxTotalConnections = httpClientProperties.getMaxConnections();
		Long timeToLive = httpClientProperties.getTimeToLive();
		TimeUnit ttlUnit = httpClientProperties.getTimeToLiveUnit();
		return connectionPoolFactory.create(maxTotalConnections, timeToLive, ttlUnit);
	}

	/**
	 * 配置OkHttpClient
	 * @param httpClientFactory httpClient 工厂
	 * @param connectionPool 链接池配置
	 * @param httpClientProperties httpClient配置
	 * @param interceptor 拦截器
	 * @return OkHttpClient
	 */
	@Bean
	@ConditionalOnMissingBean(okhttp3.OkHttpClient.class)
	public okhttp3.OkHttpClient httpClient(
		OkHttpClientFactory httpClientFactory,
		okhttp3.ConnectionPool connectionPool,
		HttpClientProperties httpClientProperties,
		HttpRestLoggingInterceptor interceptor) {
		Boolean followRedirects = httpClientProperties.isFollowRedirects();
		Integer connectTimeout = httpClientProperties.getConnectionTimeout();
		return httpClientFactory.createBuilder(httpClientProperties.isDisableSslValidation())
			.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
			.writeTimeout(30, TimeUnit.SECONDS)
			.readTimeout(30, TimeUnit.SECONDS)
			.followRedirects(followRedirects)
			.connectionPool(connectionPool)
			.addInterceptor(interceptor)
			.build();
	}


	/**
	 * 普通的 RestTemplate，不透传请求头，一般只做外部 http 调用
	 * @param httpClient OkHttpClient
	 * @return RestTemplate
	 */
	@Bean
	@ConditionalOnMissingBean(RestTemplate.class)
	public RestTemplate restTemplate(okhttp3.OkHttpClient httpClient) {
		RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory(httpClient));
		restTemplate.setErrorHandler(new ResponseErrorHandler() {

			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				int rawStatusCode = response.getRawStatusCode();
				log.info("rawStatusCode: {}",rawStatusCode);
				if(rawStatusCode== HttpStatus.OK.value() || rawStatusCode==HttpStatus.CREATED.value()){
					return false;
				}
				return true;
			}

			@Override
			public void handleError(ClientHttpResponse response) throws IOException {

				String errorMesssge = null;
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(response.getBody()));
					String body = reader.readLine();
					log.info("handleError body: {}",body);
					errorMesssge = body;
				} catch (Exception e) {
					log.error("读取response错误异常",e);
				} finally {
					if(reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							log.error("关闭Reader输入流异常",e);
						}
					}
				}
				String code = GlobalErrorCodeEnum.INTERNAL_SERVER_ERROR.getCode();
				String message = "";

				int rawStatusCode = response.getRawStatusCode();
				//404
				if(rawStatusCode == HttpStatus.NOT_FOUND.value()){
					code = GlobalErrorCodeEnum.NOT_FOUND.getCode();
					message = GlobalErrorCodeEnum.NOT_FOUND.getEnName();
					throw new CoreException(code, message);
				}
				//超时
				if(rawStatusCode == HttpStatus.REQUEST_TIMEOUT.value()){
					code = GlobalErrorCodeEnum.READ_TIME_OUT.getCode();
					message = GlobalErrorCodeEnum.READ_TIME_OUT.getEnName();
					throw new CoreException(code, message);
				}

				if(StringUtils.isEmpty(errorMesssge)) {
					// 如果异常体为空，错误码取httpCode
					code = String.valueOf(rawStatusCode);
					throw new CoreException(code, message);
				}
				throw new CoreException(code,message);
			}
		});
		configMessageConverters(restTemplate.getMessageConverters());
		return restTemplate;
	}

	private void configMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.removeIf(x -> x instanceof StringHttpMessageConverter || x instanceof MappingJackson2HttpMessageConverter);
		converters.add(new StringHttpMessageConverter(Charsets.UTF_8));
		converters.add(new ProtostuffHttpMessageConverter());
	}
}
