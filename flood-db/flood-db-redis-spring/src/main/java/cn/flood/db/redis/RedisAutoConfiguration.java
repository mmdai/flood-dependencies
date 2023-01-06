package cn.flood.db.redis;

import cn.flood.db.redis.builder.RedisTemplaterFactoryBuild;
import cn.flood.db.redis.config.lettuce.LettuceConnectionConfiguration;
import cn.flood.db.redis.config.properties.DynamicRedisProperties;
import cn.flood.db.redis.provider.RedisProvider;
import cn.flood.db.redis.provider.impl.DynamicRedisProvider;
import cn.flood.db.redis.util.ApplicationContextUtil;
import cn.flood.db.redis.service.RedisService;
import cn.flood.db.redis.service.impl.RedisServiceImpl;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * redis自动配置
 * @author daimm
 * @date 2019/4/18
 * @since 1.8
 */
@AutoConfiguration
@AutoConfigureBefore(org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class)
@EnableConfigurationProperties({DynamicRedisProperties.class})
@Import({
        ApplicationContextUtil.class,
        LettuceConnectionConfiguration.class
})
public class RedisAutoConfiguration {
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
    /**
     * 提供给外部使用的RedisTemplater工厂类
     * 用于获取动态RedisTemplater以及默认RedisTemplater
     * @param dynamicRedisProperties 动态数据源配置
     * @return
     * @throws Exception
     */
    @Bean
    public RedisTemplaterFactoryBuild redisTemplaterWrapperFactoryBuild(RedisConnectionFactory redisConnectionFactory, DynamicRedisProperties dynamicRedisProperties) throws Exception {
        RedisTemplaterFactoryBuild redisTemplaterFactoryBuild = getRedisTemplaterFactoryBuild(dynamicRedisProperties);
        redisTemplaterFactoryBuild.setDefaultRedisTemplate(redisTemplate(redisConnectionFactory, redisTemplaterFactoryBuild,dynamicRedisProperties));
        return redisTemplaterFactoryBuild;
    }

    /**
     * 构造RedisTemplater工厂类
     * @param dynamicRedisProperties 动态数据源配置属性
     * @return
     */
    public RedisTemplaterFactoryBuild getRedisTemplaterFactoryBuild(DynamicRedisProperties dynamicRedisProperties) {
        RedisTemplaterFactoryBuild redisTemplaterFactoryBuild = new RedisTemplaterFactoryBuild();
        //如果开启多数据源配置，则获取多数据源对应构造出来的RedisTemplate
        if (dynamicRedisProperties.isEnabled()) {
            DynamicRedisProvider yamlRedisProvider = new DynamicRedisProvider(dynamicRedisProperties);
            Map<String, RedisTemplate<String, Object>> stringRedisTemplateMap = redisTemplatesMap(yamlRedisProvider);
            redisTemplaterFactoryBuild.setRedisTemplateMap(stringRedisTemplateMap);
        }
        return redisTemplaterFactoryBuild;
    }


    /**
     * 序列化
     * @return
     */
    @Bean
    public RedisSerializer<Object> redisSerializer() {
        //创建JSON序列化器
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 日期和时间格式化
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        //时间序列化规则
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(NORM_DATE_PATTERN)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(NORM_TIME_PATTERN)));
        //时间反序列化规则
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(NORM_DATE_PATTERN)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(NORM_TIME_PATTERN)));
        objectMapper.registerModule(javaTimeModule);
        //必须设置，否则无法将JSON转化为对象，会转化成Map类型
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(objectMapper);
        return serializer;
    }

    /**
     * 将多数据源配置built出多个RedisTemplate
     * @param redisProvider
     * @return
     */
    public Map<String, RedisTemplate<String, Object>> redisTemplatesMap(RedisProvider redisProvider) {
        Map<String, RedisConnectionFactory> stringRedisConnectionFactoryMap = redisProvider.loadRedis();
        Map<String,RedisTemplate<String, Object>> redisTemplatesMap = new HashMap<>();
        for (String key : stringRedisConnectionFactoryMap.keySet()) {
            RedisConnectionFactory redisDataSoure = stringRedisConnectionFactoryMap.get(key);
            RedisSerializer<Object> serializer = redisSerializer();
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisDataSoure);
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(serializer);
            redisTemplate.setHashKeySerializer(new StringRedisSerializer());
            redisTemplate.setHashValueSerializer(serializer);
            redisTemplate.afterPropertiesSet();
            redisTemplatesMap.put(key, redisTemplate);
        }
        return redisTemplatesMap;
    }

    /**
     * RedisTemplaterWrapper为RedisTemplater的包装类
     * 当启用了多数据源配置时，RedisTemplaterWrapper中的数据源为配置中的其中一个
     *    1.当多数据源配置了默认数据源时，用默认数据源
     *    2.当多数据源没有配置默认数据源时，使用最后一个配置作为数据源
     * 当没有启用多数据源配置时，此类就是RedisTemplater的普通封装，配置什么数据源就用什么
     * @return
     * @throws Exception
     */
    @Primary
    @Bean
    @ConditionalOnMissingBean({RedisTemplate.class})
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, RedisTemplaterFactoryBuild fb, DynamicRedisProperties drp) throws Exception {
        RedisTemplate<String, Object> redisTemplate = null;
        if (fb.getRedisTemplateMap() != null) {
            //如果配置了默认的数据源，则使用默认的数据源
            String defaultDataSource = drp.getDefaultDataSource();
            if (!ObjectUtils.isEmpty(defaultDataSource)) {
                redisTemplate = fb.getRedisTemplaterByName(defaultDataSource);
            }else {
                //如果没有配置默认的数据源，则从所有配置的数据源中，选择最后一个配置作为数据源
                Map<String, RedisTemplate<String, Object>> allRedisTemplates = fb.getRedisTemplateMap();
                for (String key : allRedisTemplates.keySet()) {
                    redisTemplate = allRedisTemplates.get(key);
                    break;
                }
            }
        }else{
            // 配置redisTemplate
            redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            RedisSerializer<?> stringSerializer = new StringRedisSerializer();
            // key序列化
            redisTemplate.setKeySerializer(stringSerializer);
            // value序列化
            redisTemplate.setValueSerializer(redisSerializer());
            // Hash key序列化
            redisTemplate.setHashKeySerializer(stringSerializer);
            // Hash value序列化
            redisTemplate.setHashValueSerializer(redisSerializer());
            redisTemplate.afterPropertiesSet();
        }
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean({StringRedisTemplate.class})
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        RedisSerializer<?> stringSerializer = new StringRedisSerializer();
        // key序列化
        template.setKeySerializer(stringSerializer);
        // value序列化
        template.setValueSerializer(redisSerializer());
        // Hash key序列化
        template.setHashKeySerializer(stringSerializer);
        // Hash value序列化
        template.setHashValueSerializer(redisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisService getRedisService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisServiceImpl(redisTemplate);
    }
}
