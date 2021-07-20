package cn.flood.delay.entity;


import cn.flood.delay.common.GlobalConstants;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * @Description 入参
 * @Author daimi
 * @Date 2019/8/1 2:01 PM
 **/
public class DelayQueueJob implements Serializable {

    private static final long serialVersionUID = 66666L;

    /**唯一键 不能为空**/
    private String id;
    /** 任务类型 **/
    private String topic;
    /** 等待时间(ms) **/
    private long delay;
    /** 超时时间(ms) **/
    private long ttr;
    /** 创建时间 **/
    private long createTime = Clock.systemDefaultZone().millis();
    /** 执行时间 **/
    private long executionTime;
    /** 内容 **/
    private String body;
    /** 状态 **/
    private String status = GlobalConstants.STATUS_DELAY;
    /** 入库时间 **/
    private LocalDateTime createDate;
    /** 修改时间 **/
    private LocalDateTime updateDate;
    /**
     * 已经重试的次数:
     * 重试机制: 默认重试2次; 总共最多执行3次
     * 添加任务的时候可以设置为<0 的值;则表示不希望重试;
     * 回调接口自己做好幂等
     ***/
    private int retryCount;


    /**
     * 重入次数:
     * 这里标记的是当前Job某些异常情况导致并没有真正消费到,然后重新放入待消费池的次数;
     * 比如: BLPOP出来了之后,在去获取Job的时候redis超时了,导致没有正常消费掉;
     * 重入次数最大 3次; 避免某些不可控因素出现,超过3次则丢弃
     */
    private int reentry;

    public DelayQueueJob() {
    }

    public DelayQueueJob(String id) {
        this.id = id;
    }

    public DelayQueueJob(String id, int retryCount) {
        this.id = id;
        this.retryCount = retryCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getReentry() {
        return reentry;
    }

    public void setReentry(int reentry) {
        this.reentry = reentry;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getTtr() {
        return ttr;
    }

    public void setTtr(long ttr) {
        this.ttr = ttr;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "DelayQueueJob{" +
                "id='" + id + '\'' +
                ", topic='" + topic + '\'' +
                ", delay=" + delay +
                ", ttr=" + ttr +
                ", createTime=" + createTime +
                ", executionTime=" + executionTime +
                ", body='" + body + '\'' +
                ", status='" + status + '\'' +
                ", retryCount=" + retryCount +
                ", reentry=" + reentry +
                '}';
    }
}
