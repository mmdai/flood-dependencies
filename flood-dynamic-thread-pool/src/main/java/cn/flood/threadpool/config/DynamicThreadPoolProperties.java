package cn.flood.threadpool.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

/**
 * 动态线程池配置
 *
 */
@Data
@ConfigurationProperties(prefix = "flood.threadpools")
@SuppressWarnings("unchecked")
public class DynamicThreadPoolProperties {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * Nacos DataId, 监听配置修改用
     */
    private String nacosDataId;

    /**
     * Nacos Group, 监听配置修改用
     */
    private String nacosGroup;

    /**
     * Nacos 等待配置刷新时间间隔（监听器收到消息变更通知，此时Spring容器中的配置bean还没更新，需要等待固定的时间）
     */
    private int nacosWaitRefreshConfigSeconds = 1;

    /**
     * 异常告警API地址，如果配置了此值就将异常信息告诉配置的API,异常告警由外部系统处理
     * 默认会通过钉钉告警，需要配置钉钉信息
     */
    private String alarmApiUrl;

    /**
     * 钉钉机器人access_token
     */
    private String accessToken;

    /**
     * 钉钉机器人secret
     */
    private String secret;

    /**
     * 告警时间间隔，单位分钟
     */
    private int alarmTimeInterval = 1;

    /**
     * 负责人
     */
    private String owner;

    /**
     * 线程池配置
     */
    private List<ThreadPoolProperties> executors = new ArrayList<>();

    /**
     * 刷新配置
     * @param content 整个配置文件的内容
     */
    public void refresh(String content) {
        Properties properties =  new Properties();
        try {
            properties.load(new ByteArrayInputStream(content.getBytes()));
        } catch (IOException e) {
            log.error("转换Properties异常", e);
        }
        doRefresh(properties);
    }

    public void refreshYaml(String content) {
        YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
        bean.setResources(new ByteArrayResource(content.getBytes()));
        Properties properties = bean.getObject();
        doRefresh(properties);
    }

    private void doRefresh(Properties properties) {
        Map<String, String> dataMap = new HashMap<String, String>((Map) properties);
        ConfigurationPropertySource sources = new MapConfigurationPropertySource(dataMap);

        Binder binder = new Binder(sources);
        binder.bind("kitty.threadpools", Bindable.ofInstance(this)).get();
    }

}
