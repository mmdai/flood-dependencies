package cn.flood.base.proto;

import cn.flood.base.jackson.MappingApiJackson2HttpMessageConverter;
import cn.flood.base.proto.converter.ProtostuffHttpMessageConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * <p>Title: MessageProtoSupportConfig</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2020</p>
 *
 * @author mmdai
 * @version 1.0
 * @date 2020/8/27
 */

@AutoConfiguration
public class MessageProtoSupportConfig implements WebMvcConfigurer {

  /**
   * yyyy-MM-dd HH:mm:ss 格式
   */
  private static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
  /**
   * yyyy-MM-dd 格式
   */
  private static final String NORM_DATE_PATTERN = "yyyy-MM-dd";
  /**
   * HH:mm:ss 格式
   */
  private static final String NORM_TIME_PATTERN = "HH:mm:ss";
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  //add the protobuf http message converter
  @Bean
  public ProtostuffHttpMessageConverter protobufHttpMessageConverter() {
    log.info("ProtostuffHttpMessageConverter");
    return new ProtostuffHttpMessageConverter();
  }

  /**
   * 同一个WebMvcConfigurerAdapter中的configureMessageConverters方法先于extendMessageConverters方法执行
   *
   * @param converters
   */
  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        log.info("ProtobufHttpMessageConverter:------------start------ ");
    converters.removeIf(x -> x instanceof StringHttpMessageConverter
        || x instanceof AbstractJackson2HttpMessageConverter);
    converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
    //使用converters.add(xxx)会放在最低优先级（List的尾部）
    converters.add(protobufHttpMessageConverter());
    //需要追加byte，否则springdoc-openapi接口会响应Base64编码内容，导致接口文档显示失败
    // https://github.com/springdoc/springdoc-openapi/issues/2143
    // 解决方案
    converters.add(0, new ByteArrayHttpMessageConverter());

    // Jackson配置类
    ObjectMapper objectMapper = new ObjectMapper();
    //设置地点为中国
    objectMapper.setLocale(Locale.CHINA);
    //去掉默认的时间戳格式
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    //设置为中国上海时区
    objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
    // 序列化BigDecimal时不使用科学计数法输出
    objectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
    //序列化时，日期的统一格式
    objectMapper.setDateFormat(new SimpleDateFormat(NORM_DATETIME_PATTERN, Locale.CHINA));
    //序列化处理
    objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
    objectMapper
        .configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true);
    objectMapper.findAndRegisterModules();
    //失败处理
    // 对于空的对象转json的时候不抛出错误
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    // 禁用遇到未知属性抛出异常
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    //单引号处理
    objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    //反序列化时，属性不存在的兼容处理
    objectMapper.getDeserializationConfig()
        .withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    // 日期和时间格式化
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    //时间序列化规则
    javaTimeModule.addSerializer(LocalDateTime.class,
        new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN)));
    javaTimeModule.addSerializer(LocalDate.class,
        new LocalDateSerializer(DateTimeFormatter.ofPattern(NORM_DATE_PATTERN)));
    javaTimeModule.addSerializer(LocalTime.class,
        new LocalTimeSerializer(DateTimeFormatter.ofPattern(NORM_TIME_PATTERN)));
    //Instant 类型序列化
    javaTimeModule.addSerializer(Instant.class, InstantSerializer.INSTANCE);
    //时间反序列化规则
    javaTimeModule.addDeserializer(LocalDateTime.class,
        new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN)));
    javaTimeModule.addDeserializer(LocalDate.class,
        new LocalDateDeserializer(DateTimeFormatter.ofPattern(NORM_DATE_PATTERN)));
    javaTimeModule.addDeserializer(LocalTime.class,
        new LocalTimeDeserializer(DateTimeFormatter.ofPattern(NORM_TIME_PATTERN)));
    //Instant 类型反序列化
    javaTimeModule.addDeserializer(Instant.class, InstantDeserializer.INSTANT);
    objectMapper.registerModule(javaTimeModule);

    converters.add(1, new MappingApiJackson2HttpMessageConverter(objectMapper));


  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
    localeInterceptor.setParamName("lang");
    registry.addInterceptor(localeInterceptor);
  }

  /**
   * gateway 网关已经添加 所以注释掉该代码
   *
   * @param registry
   */
//    private final Long ACCESS_CONTROL_MAX_AGE = 3600L;
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOriginPatterns(CorsConfiguration.ALL)//允许跨域的主机地址,*表示所有都可以
//                .allowCredentials(Boolean.TRUE)//是否允许携带cookie,true表示允许
//                .allowedMethods(CorsConfiguration.ALL)//允许跨域的请求方法
//                .exposedHeaders("X-Access-Token", "Flood_Token","channel_id", "cache-control",
//                        "content-language", "client_id", "version", "tenant_id")//允许跨域的请求头
//                .maxAge(ACCESS_CONTROL_MAX_AGE);//单位为秒, 重新预检验跨域的缓存时间,表示该时间内，不用重新检验跨域
//    }
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/js/**").addResourceLocations("classpath:/js/");
    registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
    registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/static/");
    /** swagger-ui 地址 */
    registry.addResourceHandler("/swagger-ui/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
        .resourceChain(false);
  }

}
