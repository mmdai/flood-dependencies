package cn.flood.mq.rocketmq.base;

import cn.flood.base.core.context.SpringBeanManager;
import cn.flood.mq.rocketmq.dedup.persist.DedupElement;
import cn.flood.mq.rocketmq.dedup.persist.IPersist;
import cn.flood.mq.rocketmq.dedup.persist.JDBCPersit;
import cn.flood.mq.rocketmq.dedup.persist.RedisPersist;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by yipin on 2017/6/27.
 * RocketMQ的消费者(Push模式)处理消息的接口
 */
public abstract class AbstractMQPushConsumer<T> extends AbstractMQConsumer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMQPushConsumer.class);

    /**
     * 对于消费中的消息，多少毫秒内认为重复，默认一分钟，即一分钟内的重复消息都会串行处理（等待前一个消息消费成功/失败），超过这个时间如果消息还在消费就不认为重复了（为了防止消息丢失）
     */
    private long dedupProcessingExpireMilliSeconds = 60 * 1000;

    /**
     * 消息消费成功后，记录保留多少分钟，默认一天，即一天内的消息不会重复
     */
    private long dedupRecordReserveMinutes = 60 * 24;

    private DefaultMQPushConsumer consumer;

    public AbstractMQPushConsumer() {
    }

    /**
     * 继承这个方法处理消息
     *
     * @param message 消息范型
     * @param extMap  存放消息附加属性的map, map中的key存放在 @link MessageExtConst 中
     * @return 处理结果
     * @see MessageExtConst
     */
    public abstract boolean process(T message, Map<String, Object> extMap);

    /**
     * 继承这个方法用于幂等性key
     * @param message
     * @param extMap
     * @return
     */
    public abstract String dedupKey(T message, Map<String, Object> extMap);

    /**
     *  默认uniqkey 作为去重的标识
     */
    protected String dedupMessageKey(final MessageExt messageExt) {
        // parse message body
        T t = parseMessage(messageExt);
        // parse ext properties
        Map<String, Object> ext = parseExtParam(messageExt);
        String uniqID = dedupKey(t, ext);
        if (uniqID == null) {
            return messageExt.getMsgId();
        } else {
            return uniqID;
        }
    }

    /**
     * 原生dealMessage方法，可以重写此方法自定义序列化和返回消费成功的相关逻辑
     *
     * @param list                       消息列表
     * @param consumeConcurrentlyContext 上下文
     * @return 消费状态
     */
    public ConsumeConcurrentlyStatus dealMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        for (MessageExt messageExt : list) {
            LOGGER.debug("receive msgId: {}, tags : {}", messageExt.getMsgId(), messageExt.getTags());
            // parse message body
            T t = parseMessage(messageExt);
            // parse ext properties
            Map<String, Object> ext = parseExtParam(messageExt);
            if (null != t && !process(t, ext)) {
                LOGGER.warn("consume fail , ask for re-consume , msgId: {}", messageExt.getMsgId());
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 原生dealMessage方法，可以重写此方法自定义序列化和返回消费成功的相关逻辑
     *
     * @param list                  消息列表
     * @param consumeOrderlyContext 上下文
     * @return 处理结果
     */
    public ConsumeOrderlyStatus dealMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
        for (MessageExt messageExt : list) {
            LOGGER.info("receive msgId: {}, tags : {}", messageExt.getMsgId(), messageExt.getTags());
            T t = parseMessage(messageExt);
            Map<String, Object> ext = parseExtParam(messageExt);
            if (null != t && !process(t, ext)) {
                LOGGER.warn("consume fail , ask for re-consume , msgId: {}", messageExt.getMsgId());
                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }


    /**
     * 原生dealMessage方法，可以重写此方法自定义序列化和返回消费成功的相关逻辑
     *
     * @param list                       消息列表
     * @param consumeConcurrentlyContext 上下文
     * @return 消费状态
     */
    public ConsumeConcurrentlyStatus dealDedupMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        boolean hasConsumeFail = false;
        int ackIndexIfFail = -1;
        for (int i = 0; i < list.size(); i++) {
            MessageExt msg = list.get(i);
            try {
                hasConsumeFail = !handleMsgInner(msg);
            } catch (Exception ex) {
                LOGGER.warn("Throw Exception when consume {}, ex", msg, ex);
                hasConsumeFail = true;
            }

            //如果前面出现消费失败的话，后面也不用消费了，因为都会重发
            if (hasConsumeFail) {
                break;
            } else { //到现在都消费成功
                ackIndexIfFail = i;
            }
        }

        if (!hasConsumeFail) {//全都消费成功
            LOGGER.info("consume [{}] msg(s) all successfully", list.size());
        } else {//存在失败的
            consumeConcurrentlyContext.setAckIndex(ackIndexIfFail);//标记成功位，后面的会重发以重新消费，在这个位置之前的不会重发。 详情见源码：ConsumeMessageConcurrentlyService#processConsumeResult
            LOGGER.warn("consume [{}] msg(s) fails, ackIndex = [{}] ", list.size(), consumeConcurrentlyContext.getAckIndex());
        }


        //无论如何最后都返回成功
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    //消费消息，带去重的逻辑
    private boolean handleMsgInner(final MessageExt messageExt) {
        //去重介质
        String type = SpringBeanManager.getProperties("spring.rocketmq.dedup-type");
        IPersist persist = null;
        if("db".equalsIgnoreCase(type)){
            persist = new JDBCPersit(SpringBeanManager.getBean(JdbcTemplate.class));
        }else{
            persist = new RedisPersist(SpringBeanManager.getBean(StringRedisTemplate.class));
        }
        DedupElement dedupElement = new DedupElement(SpringBeanManager.getProperties("spring.application.name"), messageExt.getTopic(), messageExt.getTags()==null ? "" : messageExt.getTags(), dedupMessageKey(messageExt));
        Boolean shouldConsume = true;

        if (dedupElement.getMsgUniqKey() != null) {
            shouldConsume = persist.setConsumingIfNX(dedupElement, dedupProcessingExpireMilliSeconds);
        }

        //设置成功，证明应该要消费
        if (shouldConsume != null && shouldConsume) {
            //开始消费
            return doHandleMsgAndUpdateStatus(messageExt, dedupElement, persist);
        } else {//有消费过/中的，做对应策略处理
            String val = persist.get(dedupElement);
            if (IPersist.CONSUME_STATUS_CONSUMING.equals(val)) {//正在消费中，稍后重试
                LOGGER.warn("the same message is considered consuming, try consume later dedupKey : {}, {}, {}", persist.toPrintInfo(dedupElement), messageExt.getMsgId(), persist.getClass().getSimpleName());
                return false;
            } else if(IPersist.CONSUME_STATUS_CONSUMED.equals(val)){//证明消费过了，直接消费认为成功
                LOGGER.warn("message has been consumed before! dedupKey : {}, msgId : {} , so just ack. {}", persist.toPrintInfo(dedupElement), messageExt.getMsgId(), persist.getClass().getSimpleName());
                return true;
            } else {//非法结果，降级，直接消费
                LOGGER.warn("[NOTIFYME]unknown consume result {}, ignore dedup, continue consuming,  dedupKey : {}, {}, {} ", val, persist.toPrintInfo(dedupElement), messageExt.getMsgId(), persist.getClass().getSimpleName());
                return doHandleMsgAndUpdateStatus(messageExt, dedupElement, persist);
            }
        }
    }

    /**
     *     消费消息，末尾消费失败会删除消费记录，消费成功则更新消费状态
     */
    private boolean doHandleMsgAndUpdateStatus(final MessageExt messageExt, final DedupElement dedupElement, final IPersist persist) {
        // parse message body
        T t = parseMessage(messageExt);
        // parse ext properties
        Map<String, Object> ext = parseExtParam(messageExt);

        if (dedupElement.getMsgUniqKey()==null) {
            LOGGER.warn("dedup key is null , consume msg but not update status{}", messageExt.getMsgId());
            return process(t, ext);
        } else {
            boolean consumeRes = false;
            try {
                consumeRes = process(t, ext);
            } catch (Throwable e) {
                //消费失败了，删除这个key
                try {
                    persist.delete(dedupElement);
                } catch (Exception ex) {
                    LOGGER.error("error when delete dedup record {}", dedupElement, ex);
                }
                throw e;
            }

            //没有异常，正常返回的话，判断消费结果
            try {
                if (consumeRes) {//标记为这个消息消费过
                    LOGGER.debug("set consume res as CONSUME_STATUS_CONSUMED , {}", dedupElement);
                    persist.markConsumed(dedupElement, dedupRecordReserveMinutes);
                } else {
                    LOGGER.info("consume Res is false, try deleting dedup record {} , {}", dedupElement, persist);
                    persist.delete(dedupElement);//消费失败了，删除这个key
                }
            } catch (Exception e) {
                LOGGER.error("消费去重收尾工作异常 {}，忽略异常", messageExt.getMsgId(), e);
            }
            return consumeRes;
        }

    }


    public DefaultMQPushConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(DefaultMQPushConsumer consumer) {
        this.consumer = consumer;
    }

}
