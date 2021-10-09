package cn.flood.pulsar.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.pulsar")
public class PulsarProperties {

    public static int DEFAULT_CONSUMER_THREAD_POOL_CORE_SIZE = 0;
    public static int DEFAULT_CONSUMER_THREAD_POOL_MAX_SIZE = 2000;
    private Integer ackTimeout = 10000;
    private String authentication;
    @Deprecated
    private Integer consumerCorePoolSize = 0;
    private Integer consumerMaximumPoolSize = 2000;
    private Boolean disableConsumer = false;
    private Boolean disableProducer = false;
    private Boolean enableTcpNoDelay = true;
    private String env = "prod";
    private Integer listenerThreads = 10;
    private String pilotAuthentication;
    private Integer receiverQueueSize = 500;
    private Integer sendTimeout = 1000;
    private String serviceUrl;

    public String toString() {
        return "PulsarProperties(serviceUrl=" + getServiceUrl() + ", authentication=" + getAuthentication() + ", env=" + getEnv() + ", pilotAuthentication=" + getPilotAuthentication() + ", listenerThreads=" + getListenerThreads() + ", enableTcpNoDelay=" + getEnableTcpNoDelay() + ", sendTimeout=" + getSendTimeout() + ", ackTimeout=" + getAckTimeout() + ", receiverQueueSize=" + getReceiverQueueSize() + ", consumerCorePoolSize=" + getConsumerCorePoolSize() + ", consumerMaximumPoolSize=" + getConsumerMaximumPoolSize() + ", disableConsumer=" + getDisableConsumer() + ", disableProducer=" + getDisableProducer() + ")";
    }

    /* access modifiers changed from: protected */
    public boolean canEqual(Object other) {
        return other instanceof PulsarProperties;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PulsarProperties)) {
            return false;
        }
        PulsarProperties other = (PulsarProperties) o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$serviceUrl = getServiceUrl();
        String other$serviceUrl = other.getServiceUrl();
        if (this$serviceUrl != null ? !this$serviceUrl.equals(other$serviceUrl) : other$serviceUrl != null) {
            return false;
        }
        String this$authentication = getAuthentication();
        String other$authentication = other.getAuthentication();
        if (this$authentication != null ? !this$authentication.equals(other$authentication) : other$authentication != null) {
            return false;
        }
        String this$env = getEnv();
        String other$env = other.getEnv();
        if (this$env != null ? !this$env.equals(other$env) : other$env != null) {
            return false;
        }
        String this$pilotAuthentication = getPilotAuthentication();
        String other$pilotAuthentication = other.getPilotAuthentication();
        if (this$pilotAuthentication != null ? !this$pilotAuthentication.equals(other$pilotAuthentication) : other$pilotAuthentication != null) {
            return false;
        }
        Integer this$listenerThreads = getListenerThreads();
        Integer other$listenerThreads = other.getListenerThreads();
        if (this$listenerThreads != null ? !this$listenerThreads.equals(other$listenerThreads) : other$listenerThreads != null) {
            return false;
        }
        Boolean this$enableTcpNoDelay = getEnableTcpNoDelay();
        Boolean other$enableTcpNoDelay = other.getEnableTcpNoDelay();
        if (this$enableTcpNoDelay != null ? !this$enableTcpNoDelay.equals(other$enableTcpNoDelay) : other$enableTcpNoDelay != null) {
            return false;
        }
        Integer this$sendTimeout = getSendTimeout();
        Integer other$sendTimeout = other.getSendTimeout();
        if (this$sendTimeout != null ? !this$sendTimeout.equals(other$sendTimeout) : other$sendTimeout != null) {
            return false;
        }
        Integer this$ackTimeout = getAckTimeout();
        Integer other$ackTimeout = other.getAckTimeout();
        if (this$ackTimeout != null ? !this$ackTimeout.equals(other$ackTimeout) : other$ackTimeout != null) {
            return false;
        }
        Integer this$receiverQueueSize = getReceiverQueueSize();
        Integer other$receiverQueueSize = other.getReceiverQueueSize();
        if (this$receiverQueueSize != null ? !this$receiverQueueSize.equals(other$receiverQueueSize) : other$receiverQueueSize != null) {
            return false;
        }
        Integer this$consumerCorePoolSize = getConsumerCorePoolSize();
        Integer other$consumerCorePoolSize = other.getConsumerCorePoolSize();
        if (this$consumerCorePoolSize != null ? !this$consumerCorePoolSize.equals(other$consumerCorePoolSize) : other$consumerCorePoolSize != null) {
            return false;
        }
        Integer this$consumerMaximumPoolSize = getConsumerMaximumPoolSize();
        Integer other$consumerMaximumPoolSize = other.getConsumerMaximumPoolSize();
        if (this$consumerMaximumPoolSize != null ? !this$consumerMaximumPoolSize.equals(other$consumerMaximumPoolSize) : other$consumerMaximumPoolSize != null) {
            return false;
        }
        Boolean this$disableConsumer = getDisableConsumer();
        Boolean other$disableConsumer = other.getDisableConsumer();
        if (this$disableConsumer != null ? !this$disableConsumer.equals(other$disableConsumer) : other$disableConsumer != null) {
            return false;
        }
        Boolean this$disableProducer = getDisableProducer();
        Boolean other$disableProducer = other.getDisableProducer();
        return this$disableProducer != null ? this$disableProducer.equals(other$disableProducer) : other$disableProducer == null;
    }

    public int hashCode() {
        String $serviceUrl = getServiceUrl();
        int hashCode = $serviceUrl == null ? 43 : $serviceUrl.hashCode();
        String $authentication = getAuthentication();
        int i = (hashCode + 59) * 59;
        int hashCode2 = $authentication == null ? 43 : $authentication.hashCode();
        String $env = getEnv();
        int i2 = (i + hashCode2) * 59;
        int hashCode3 = $env == null ? 43 : $env.hashCode();
        String $pilotAuthentication = getPilotAuthentication();
        int i3 = (i2 + hashCode3) * 59;
        int hashCode4 = $pilotAuthentication == null ? 43 : $pilotAuthentication.hashCode();
        Integer $listenerThreads = getListenerThreads();
        int i4 = (i3 + hashCode4) * 59;
        int hashCode5 = $listenerThreads == null ? 43 : $listenerThreads.hashCode();
        Boolean $enableTcpNoDelay = getEnableTcpNoDelay();
        int i5 = (i4 + hashCode5) * 59;
        int hashCode6 = $enableTcpNoDelay == null ? 43 : $enableTcpNoDelay.hashCode();
        Integer $sendTimeout = getSendTimeout();
        int i6 = (i5 + hashCode6) * 59;
        int hashCode7 = $sendTimeout == null ? 43 : $sendTimeout.hashCode();
        Integer $ackTimeout = getAckTimeout();
        int i7 = (i6 + hashCode7) * 59;
        int hashCode8 = $ackTimeout == null ? 43 : $ackTimeout.hashCode();
        Integer $receiverQueueSize = getReceiverQueueSize();
        int i8 = (i7 + hashCode8) * 59;
        int hashCode9 = $receiverQueueSize == null ? 43 : $receiverQueueSize.hashCode();
        Integer $consumerCorePoolSize = getConsumerCorePoolSize();
        int i9 = (i8 + hashCode9) * 59;
        int hashCode10 = $consumerCorePoolSize == null ? 43 : $consumerCorePoolSize.hashCode();
        Integer $consumerMaximumPoolSize = getConsumerMaximumPoolSize();
        int i10 = (i9 + hashCode10) * 59;
        int hashCode11 = $consumerMaximumPoolSize == null ? 43 : $consumerMaximumPoolSize.hashCode();
        Boolean $disableConsumer = getDisableConsumer();
        int i11 = (i10 + hashCode11) * 59;
        int hashCode12 = $disableConsumer == null ? 43 : $disableConsumer.hashCode();
        Boolean $disableProducer = getDisableProducer();
        return ((i11 + hashCode12) * 59) + ($disableProducer == null ? 43 : $disableProducer.hashCode());
    }

    public void setServiceUrl(String serviceUrl2) {
        this.serviceUrl = serviceUrl2;
    }

    public String getServiceUrl() {
        return this.serviceUrl;
    }

    public void setAuthentication(String authentication2) {
        this.authentication = authentication2;
    }

    public String getAuthentication() {
        return this.authentication;
    }

    public void setEnv(String env2) {
        this.env = env2;
    }

    public String getEnv() {
        return this.env;
    }

    public void setPilotAuthentication(String pilotAuthentication2) {
        this.pilotAuthentication = pilotAuthentication2;
    }

    public String getPilotAuthentication() {
        return this.pilotAuthentication;
    }

    public void setListenerThreads(Integer listenerThreads2) {
        this.listenerThreads = listenerThreads2;
    }

    public Integer getListenerThreads() {
        return this.listenerThreads;
    }

    public void setEnableTcpNoDelay(Boolean enableTcpNoDelay2) {
        this.enableTcpNoDelay = enableTcpNoDelay2;
    }

    public Boolean getEnableTcpNoDelay() {
        return this.enableTcpNoDelay;
    }

    public void setSendTimeout(Integer sendTimeout2) {
        this.sendTimeout = sendTimeout2;
    }

    public Integer getSendTimeout() {
        return this.sendTimeout;
    }

    public void setAckTimeout(Integer ackTimeout2) {
        this.ackTimeout = ackTimeout2;
    }

    public Integer getAckTimeout() {
        return this.ackTimeout;
    }

    public void setReceiverQueueSize(Integer receiverQueueSize2) {
        this.receiverQueueSize = receiverQueueSize2;
    }

    public Integer getReceiverQueueSize() {
        return this.receiverQueueSize;
    }

    @Deprecated
    public void setConsumerCorePoolSize(Integer consumerCorePoolSize2) {
        this.consumerCorePoolSize = consumerCorePoolSize2;
    }

    @Deprecated
    public Integer getConsumerCorePoolSize() {
        return this.consumerCorePoolSize;
    }

    public Integer getConsumerMaximumPoolSize() {
        return this.consumerMaximumPoolSize;
    }

    public void setConsumerMaximumPoolSize(Integer consumerMaximumPoolSize2) {
        if (consumerMaximumPoolSize2.intValue() > DEFAULT_CONSUMER_THREAD_POOL_MAX_SIZE) {
            this.consumerMaximumPoolSize = Integer.valueOf(DEFAULT_CONSUMER_THREAD_POOL_MAX_SIZE);
        } else {
            this.consumerMaximumPoolSize = consumerMaximumPoolSize2;
        }
    }

    public Boolean getDisableConsumer() {
        return this.disableConsumer;
    }

    public void setDisableConsumer(Boolean disableConsumer2) {
        this.disableConsumer = disableConsumer2;
    }

    public Boolean getDisableProducer() {
        return this.disableProducer;
    }

    public void setDisableProducer(Boolean disableProducer2) {
        this.disableProducer = disableProducer2;
    }

}
