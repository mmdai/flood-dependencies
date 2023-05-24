package cn.flood.cloud.grpc.http;

import cn.flood.base.core.constants.AppConstant;
import cn.flood.base.core.exception.CoreException;
import cn.flood.base.core.exception.enums.GlobalErrorCodeEnum;
import cn.flood.base.proto.converter.ProtostuffHttpMessageConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.flood.cloud.grpc.httpclient.DefaultOkHttpClientConnectionPoolFactory;
import cn.flood.cloud.grpc.httpclient.DefaultOkHttpClientFactory;
import cn.flood.cloud.grpc.httpclient.OkHttpClientConnectionPoolFactory;
import cn.flood.cloud.grpc.httpclient.OkHttpClientFactory;
import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * Http RestTemplateHeaderInterceptor 配置
 *
 * @author mmdai
 */
@AutoConfiguration
@AllArgsConstructor
@ConditionalOnClass(okhttp3.OkHttpClient.class)
public class RestTemplateConfiguration {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  /**
   * dev, test 环境打印出BODY
   *
   * @return HttpLoggingInterceptor
   */
  @Bean("httpLoggingInterceptor")
  @Profile({AppConstant.DEV_CODE, AppConstant.TEST_CODE})
  public HttpLoggingInterceptor testLoggingInterceptor() {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new OkHttpSlf4jLogger());
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    return interceptor;
  }

  /**
   * uat 环境 打印 请求头
   *
   * @return HttpLoggingInterceptor
   */
  @Bean("httpLoggingInterceptor")
  @Profile(AppConstant.UAT_CODE)
  public HttpLoggingInterceptor onTestLoggingInterceptor() {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new OkHttpSlf4jLogger());
    interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
    return interceptor;
  }

  /**
   * prod 环境只打印请求url
   *
   * @return HttpLoggingInterceptor
   */
  @Bean("httpLoggingInterceptor")
  @Profile(AppConstant.PROD_CODE)
  public HttpLoggingInterceptor prodLoggingInterceptor() {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new OkHttpSlf4jLogger());
    interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
    return interceptor;
  }



  @Bean
  @ConditionalOnMissingBean(OkHttpClientConnectionPoolFactory.class)
  public OkHttpClientConnectionPoolFactory connPoolFactory() {
    return new DefaultOkHttpClientConnectionPoolFactory();
  }


  @Bean
  @ConditionalOnMissingBean(OkHttpClientFactory.class)
  public OkHttpClientFactory okHttpClientFactory() {
    return new DefaultOkHttpClientFactory(new OkHttpClient.Builder());
  }
  /**
   * okhttp3 链接池配置
   *
   * @param connectionPoolFactory 链接池配置
   * @param httpClientProperties  httpClient配置
   * @return okhttp3.ConnectionPool
   */
  @Bean
  @ConditionalOnMissingBean(okhttp3.ConnectionPool.class)
  public okhttp3.ConnectionPool httpClientConnectionPool(
      FeignHttpClientProperties httpClientProperties,
      OkHttpClientConnectionPoolFactory connectionPoolFactory) {
    Integer maxTotalConnections = httpClientProperties.getMaxConnections();
    Long timeToLive = httpClientProperties.getTimeToLive();
    TimeUnit ttlUnit = httpClientProperties.getTimeToLiveUnit();
    return connectionPoolFactory.create(maxTotalConnections, timeToLive, ttlUnit);
  }

  /**
   * 配置OkHttpClient
   *
   * @param httpClientFactory    httpClient 工厂
   * @param connectionPool       链接池配置
   * @param httpClientProperties httpClient配置
   * @param interceptor          拦截器
   * @return OkHttpClient
   */
  @Bean
  @ConditionalOnMissingBean(okhttp3.OkHttpClient.class)
  public okhttp3.OkHttpClient httpClient(
      OkHttpClientFactory httpClientFactory,
      okhttp3.ConnectionPool connectionPool,
      FeignHttpClientProperties httpClientProperties,
      HttpLoggingInterceptor interceptor) {
    Boolean followRedirects = httpClientProperties.isFollowRedirects();
    Integer connectTimeout = httpClientProperties.getConnectionTimeout();
    List<Protocol> protocols = new ArrayList<>();
    protocols.add(Protocol.H2_PRIOR_KNOWLEDGE);
    OkHttpClient.Builder builder = httpClientFactory
        .createBuilder(httpClientProperties.isDisableSslValidation())
        .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .followRedirects(followRedirects)
        .connectionPool(connectionPool);
    builder.protocols(protocols);
    builder.addInterceptor(interceptor);
    return builder.build();
  }


  /**
   * 普通的 RestTemplate，不透传请求头，一般只做外部 http 调用
   *
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
//				log.info("rawStatusCode: {}",rawStatusCode);
        if (rawStatusCode == HttpStatus.OK.value() || rawStatusCode == HttpStatus.CREATED.value()) {
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
          log.info("handleError body: {}", body);
          errorMesssge = body;
        } catch (Exception e) {
          log.error("读取response错误异常", e);
        } finally {
          if (reader != null) {
            try {
              reader.close();
            } catch (IOException e) {
              log.error("关闭Reader输入流异常", e);
            }
          }
        }
        String code = GlobalErrorCodeEnum.INTERNAL_SERVER_ERROR.getCode();
        String message = "";

        int rawStatusCode = response.getRawStatusCode();
        //404
        if (rawStatusCode == HttpStatus.NOT_FOUND.value()) {
          code = GlobalErrorCodeEnum.NOT_FOUND.getCode();
          message = GlobalErrorCodeEnum.NOT_FOUND.getEnName();
          throw new CoreException(code, message);
        }
        //超时
        if (rawStatusCode == HttpStatus.REQUEST_TIMEOUT.value()) {
          code = GlobalErrorCodeEnum.READ_TIME_OUT.getCode();
          message = GlobalErrorCodeEnum.READ_TIME_OUT.getEnName();
          throw new CoreException(code, message);
        }

        if (ObjectUtils.isEmpty(errorMesssge)) {
          // 如果异常体为空，错误码取httpCode
          code = String.valueOf(rawStatusCode);
          throw new CoreException(code, message);
        }
        throw new CoreException(code, message);
      }
    });
    configMessageConverters(restTemplate.getMessageConverters());
    return restTemplate;
  }

  private void configMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.removeIf(x -> x instanceof StringHttpMessageConverter
        || x instanceof MappingJackson2HttpMessageConverter);
    converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
    converters.add(new ProtostuffHttpMessageConverter());
  }
}
