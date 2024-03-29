package cn.flood.mq.rocketmq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by yipin on 2017/6/28. RocketMQ的配置参数
 */
@ConfigurationProperties(prefix = "spring.rocketmq")
public class MQProperties {

  /**
   * config name server address
   */
  private String nameServerAddress;
  /**
   * config producer group , default to DPG+RANDOM UUID like DPG-fads-3143-123d-1111
   */
  private String producerGroup;
  /**
   * config send message timeout
   */
  private Integer sendMsgTimeout = 5000;
  //去重存储类型(redis)
  private String dedupType = "redis";
  /**
   * switch of trace message consumer: send message consumer info to topic: rmq_sys_TRACE_DATA
   */
  private Boolean traceEnabled = Boolean.TRUE;

  /**
   * switch of send message with vip channel
   */
  private Boolean vipChannelEnabled = Boolean.TRUE;

  /**
   * 默认存在生产者
   */
  private Boolean existProducer = Boolean.TRUE;
  /**
   * config 消费最大重试次数
   */
  private Integer maxReconsumeTimes = 6;
  /**
   * 消息发送失败重试次数
   */
  private Integer retryTimesWhenSendFailed = 3;
  /**
   * 异步发送失败重试次数
   */
  private Integer retryTimesWhenSendAsyncFailed = 3;

  public String getNameServerAddress() {
    return nameServerAddress;
  }

  public void setNameServerAddress(String nameServerAddress) {
    this.nameServerAddress = nameServerAddress;
  }

  public String getProducerGroup() {
    return producerGroup;
  }

  public void setProducerGroup(String producerGroup) {
    this.producerGroup = producerGroup;
  }

  public Integer getSendMsgTimeout() {
    return sendMsgTimeout;
  }

  public void setSendMsgTimeout(Integer sendMsgTimeout) {
    this.sendMsgTimeout = sendMsgTimeout;
  }

  public Boolean getTraceEnabled() {
    return traceEnabled;
  }

  public void setTraceEnabled(Boolean traceEnabled) {
    this.traceEnabled = traceEnabled;
  }

  public Boolean getVipChannelEnabled() {
    return vipChannelEnabled;
  }

  public void setVipChannelEnabled(Boolean vipChannelEnabled) {
    this.vipChannelEnabled = vipChannelEnabled;
  }

  public Boolean getExistProducer() {
    return existProducer;
  }

  public void setExistProducer(Boolean existProducer) {
    this.existProducer = existProducer;
  }

  public String getDedupType() {
    return dedupType;
  }

  public void setDedupType(String dedupType) {
    this.dedupType = dedupType;
  }

  public Integer getMaxReconsumeTimes() {
    return maxReconsumeTimes;
  }

  public void setMaxReconsumeTimes(Integer maxReconsumeTimes) {
    this.maxReconsumeTimes = maxReconsumeTimes;
  }

  public Integer getRetryTimesWhenSendFailed() {
    return retryTimesWhenSendFailed;
  }

  public void setRetryTimesWhenSendFailed(Integer retryTimesWhenSendFailed) {
    this.retryTimesWhenSendFailed = retryTimesWhenSendFailed;
  }

  public Integer getRetryTimesWhenSendAsyncFailed() {
    return retryTimesWhenSendAsyncFailed;
  }

  public void setRetryTimesWhenSendAsyncFailed(Integer retryTimesWhenSendAsyncFailed) {
    this.retryTimesWhenSendAsyncFailed = retryTimesWhenSendAsyncFailed;
  }
}
