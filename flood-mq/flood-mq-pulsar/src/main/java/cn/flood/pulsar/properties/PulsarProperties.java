package cn.flood.pulsar.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.pulsar")
public class PulsarProperties {

    private String serviceUrl;

    private String authentication;

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public void setListenerThreads(Integer listenerThreads) {
        this.listenerThreads = listenerThreads;
    }

    public void setEnableTcpNoDelay(Boolean enableTcpNoDelay) {
        this.enableTcpNoDelay = enableTcpNoDelay;
    }

    public void setSendTimeout(Integer sendTimeout) {
        this.sendTimeout = sendTimeout;
    }

    public void setAckTimeout(Integer ackTimeout) {
        this.ackTimeout = ackTimeout;
    }

    public void setReceiverQueueSize(Integer receiverQueueSize) {
        this.receiverQueueSize = receiverQueueSize;
    }

    public void setConsumerCorePoolSize(Integer consumerCorePoolSize) {
        this.consumerCorePoolSize = consumerCorePoolSize;
    }

    public void setConsumerMaximumPoolSize(Integer consumerMaximumPoolSize) {
        this.consumerMaximumPoolSize = consumerMaximumPoolSize;
    }

    public String getServiceUrl() {
        return this.serviceUrl;
    }

    public String getAuthentication() {
        return this.authentication;
    }

    private Integer listenerThreads = 10;

    public Integer getListenerThreads() {
        return this.listenerThreads;
    }

    private Boolean enableTcpNoDelay = true;

    public Boolean getEnableTcpNoDelay() {
        return this.enableTcpNoDelay;
    }

    private Integer sendTimeout = 1000;

    public Integer getSendTimeout() {
        return this.sendTimeout;
    }

    private Integer ackTimeout = 10000;

    public Integer getAckTimeout() {
        return this.ackTimeout;
    }

    private Integer receiverQueueSize = 500;

    public Integer getReceiverQueueSize() {
        return this.receiverQueueSize;
    }

    private Integer consumerCorePoolSize = 5;

    public Integer getConsumerCorePoolSize() {
        return this.consumerCorePoolSize;
    }

    private Integer consumerMaximumPoolSize = 2000;

    public Integer getConsumerMaximumPoolSize() {
        return this.consumerMaximumPoolSize;
    }
}
