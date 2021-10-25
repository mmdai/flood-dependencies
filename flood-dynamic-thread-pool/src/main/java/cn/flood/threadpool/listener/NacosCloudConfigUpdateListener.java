package cn.flood.threadpool.listener;

import cn.flood.threadpool.DynamicThreadPoolManager;
import cn.flood.threadpool.config.DynamicThreadPoolProperties;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * Spring Cloud Alibaba Nacos配置修改监听
 */
public class NacosCloudConfigUpdateListener {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NacosConfigProperties nacosConfigProperties;

    @Autowired
    private DynamicThreadPoolManager dynamicThreadPoolManager;

    @Autowired
    private DynamicThreadPoolProperties poolProperties;


    @PostConstruct
    public void init() {
        initConfigUpdateListener();
    }

    public void initConfigUpdateListener() {
        ConfigService configService = nacosConfigProperties.configServiceInstance();
        Assert.hasText(poolProperties.getNacosDataId(), "请配置flood.threadpools.nacosDataId");
        Assert.hasText(poolProperties.getNacosGroup(), "请配置flood.threadpools.nacosGroup");

        try {
            configService.addListener(poolProperties.getNacosDataId(), poolProperties.getNacosGroup(), new AbstractListener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    new Thread(() -> dynamicThreadPoolManager.refreshThreadPoolExecutor(true)).start();
                    log.info("线程池配置有变化，刷新完成");
                }
            });
        } catch (NacosException e) {
            log.error("Nacos配置监听异常", e);
        }
    }

}
