package cn.flood.async;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>Title: AsyncProperties</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2020</p>
 *
 * @author mmdai
 * @version 1.0
 * @date 2021/3/20
 */
@ConfigurationProperties(
        prefix = "async"
)
public class AsyncProperties {

    private int corePoolSize = 50;

    private int maxPoolSize = 50;

    private int queueCapacity = 1024;

    private int keepAliveSeconds = 10;

    public AsyncProperties() {
    }


    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }
}
