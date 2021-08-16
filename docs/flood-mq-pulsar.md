# spring boot starter for pulsar 
<p><a href="http://search.maven.org/#search%7Cga%7C1%7Ccom.maihaoche"><img src="https://maven-badges.herokuapp.com/maven-central/com.maihaoche/spring-boot-starter-rocketmq/badge.svg" alt="Maven Central" style="max-width:100%;"></a><a href="https://github.com/maihaoche/rocketmq-spring-boot-starter/releases"><img src="https://camo.githubusercontent.com/795f06dcbec8d5adcfadc1eb7a8ac9c7d5007fce/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f72656c656173652d646f776e6c6f61642d6f72616e67652e737667" alt="GitHub release" data-canonical-src="https://img.shields.io/badge/release-download-orange.svg" style="max-width:100%;"></a>


### 项目介绍



### 简单入门实例


##### 1. 添加maven依赖：

```java
<dependency>
    <groupId>cn.flood</groupId>
    <artifactId>flood-spring-cloud-starter-pulsar</artifactId>
    <version>2.0.0</version>
</dependency>
```

##### 2. 添加配置：

```java
spring:
  pulsar:
    serviceUrl: pulsar://172.25.218.214:6650,172.25.218.215:6650,172.25.218.216:6650
    authentication: eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJwYXlodWItdWF0LXRva2VuIn0.ItF4xvplE4HSPgK_yKZ8o4qRXbIc9fZmaYxLEvKziewaTX_ei8lLG8cVLwAZeNydiJ_8J91BZ13nDC_yYNwAjAkpEqN3oq2JNSECO39pyR_WTkGabPygsCNTs66TUBn4BdioI4ja3omX7dyjClT3tYXndDlqxue-bhdZCZ7iBvN8Gfj5HofgGcJH75NzDKp1x7UP1uJVgzic_0xrzeNv-oe1V11RwfKaxbmtER72BsbMoo7C3XBfeOgJyp3TCZyVwzhziUsniZ39hX742B9ty1lzMgofgkr-EtvwtYhnr-Nw6GvEi7-Vqi0A678FtDg6mH8AtBxoaX80wJnfxKjy1w
    topic:
      notifyUpperReachesDelayQueue: persistent://payhub/${pulsar_namespace.execute_env}/notifyUpperReachesDelayQueue
      notifyOrderFailureRetryQueue: persistent://payhub/${pulsar_namespace.execute_env}/notifyOrderFailureRetryQueue
      tradePreHandlerDBQueue: persistent://payhub/${pulsar_namespace.execute_env}/tradePreHandlerDBQueue
      createTransactionQueue: persistent://payhub/${pulsar_namespace.execute_env}/createTransactionQueue
      tradeCloseOrder: persistent://payhub/${pulsar_namespace.execute_env}/tradeCloseOrder
```
##### 3. 程序入口添加注解开启自动装配

 /**
     *   券码核销接口通知上游系统失败放入队列
     *   topic: notifyUpperReachesDelayQueue
     *   msgClass: TradeCouponNotifyDTO
     *   producerName: producer-notifyUpperReachesDelayQueue
     */
    @PulsarProducer(topic = "${yum.pulsar.topic.notifyUpperReachesDelayQueue}",
            msgClass = TradeCouponNotifyDTO.class)
    private Producer<TradeCouponNotifyDTO> notifyUpperReachesDelayQueue;
    
    
     /**
         * 调用上游系统
         */
        public void callUpperReaches(TradeCouponNotifyDTO  tradeCouponNotifyDTO){
            try{
                tradeCouponNotifyDTO.setStatus(tradeCouponNotifyDTO.getConsumeStatus());
                String responseEntity = channelRestTemplate.postForObject(tradeCouponNotifyDTO.getNotifyUrl(),tradeCouponNotifyDTO, String.class);
                log.info("调用业务系统回调通知，transactionNum = {},url = {}，系统返回值:{},", tradeCouponNotifyDTO.getTransactionNum(),tradeCouponNotifyDTO.getNotifyUrl(), responseEntity);
            } catch(Exception exception){
                log.error("调用业务系统回调异常，transactionNum = {},url = {}，错误信息 = {}",tradeCouponNotifyDTO.getTransactionNum(),tradeCouponNotifyDTO.getNotifyUrl(), exception.getMessage());
                // T放入延迟队列
                // TODO 写入异常表
                ExecTradeIssueReqDTO execTradeIssueReqDTO = new ExecTradeIssueReqDTO();
                execTradeIssueReqDTO.setBusiness(tradeCouponNotifyDTO.getBusiness());
                execTradeIssueReqDTO.setOrderId(tradeCouponNotifyDTO.getOrderId());
                execTradeIssueReqDTO.setTransactionNum(tradeCouponNotifyDTO.getTransactionNum());
                execTradeIssueReqDTO.setExceptionReason("1001");
                execTradeIssueReqDTO.setExceptionCode("业务方通知异常");
                execTradeIssueReqDTO.setUpdateUser(TradeConstants.TRADE_DATA_URL.CREATE_USER);
                execTradeIssueReqDTO.setCreateUser(TradeConstants.TRADE_DATA_URL.CREATE_USER);
                execTradeIssueReqDTO.setDeleteFlag(0);
                //正向券码核销
                execTradeIssueReqDTO.setOriginFrom(1);
                ExecTradeIssueResDTO issueResDTO = restTemplate.postForObject(com.yumchina.payhub.common.constants.TradeConstants.ExecTradeIssueService.SERVICE_NAME +
                        com.yumchina.payhub.common.constants.TradeConstants.ExecTradeIssueService.BASE_PATH +
                        com.yumchina.payhub.common.constants.TradeConstants.ExecTradeIssueService.EXEC_TRADE_ISSUE_SAVE,execTradeIssueReqDTO, ExecTradeIssueResDTO.class);
                log.info("写入异常表返回值{}",JSONObject.toJSONString(issueResDTO));
                try {
                    tradeCouponNotifyDTO.setConsumeAfterTime(consumeAfterTime);
                    notifyUpperReachesDelayQueue.newMessage().deliverAfter(consumeAfterTime, TimeUnit.MILLISECONDS).value(tradeCouponNotifyDTO).sendAsync().
                            thenAccept(messageId -> {
                                log.info("------------发送延时队列，消息体 = {}，MessageId = {}", JSONObject.toJSON(tradeCouponNotifyDTO),messageId);
                            });
                } catch (Exception e) {
                    log.error("调用pulsar 异常{}",e.getMessage());
                }
            }
        }
        
        
        
        

public class EcpayPaymentInfoCustomer {

	@Autowired
	IBillCollectDataListenerService billListenerService;

	/**
	 * 发送Ecpay支付数据失败死信队列
	 *   topic: billSendEcpayPaymentInfoDeadQueue
	 *   msgClass: PaymentMsgDTO
	 *   producerName: producer-sendEcpayPaymentInfoDeadQueue
	 */
	@PulsarProducer(topic = "${yum.pulsar.topic.billSendPaymentInfoFromEcpayDeadQueue}",
			msgClass = PaymentMsgDTO.class)
	Producer<PaymentMsgDTO> sendEcpayPaymentInfoDeadQueueProducer;

	/**
	 * 消费Ecpay支付信息
	 *   topic: persistent://payhub/uat/sendEcpayPaymentInfo
	 *   msgClass: PaymentMsgDTO
	 *   consumerName: consumer-sendEcpayPaymentInfo
	 *   subscriptionName: sendEcpayPaymentInfoSub
	 *   subscriptionType: Shared
	 *   receiveThreads: 处理消息线程数 默认：10
	 */
	@PulsarConsumer(topic = "${yum.pulsar.topic.billReceivePaymentInfoFromEcpay}",
			msgClass = PaymentMsgDTO.class,
			consumerName = "consumer-billReceivePaymentInfoFromEcpay",
			subscriptionName = "billReceivePaymentInfoFromEcpaySub",
			subscriptionType = SubscriptionType.Shared)
	public void receiveEcpayPaymentMsg(PaymentMsgDTO paymentMsgDTO) throws Exception{
		log.info("receiveEcpayPaymentMsg paymentMsgDTO = {} ",JSONObject.toJSONString(paymentMsgDTO));
		try {
			billListenerService.receiveEcpayPaymentMessage(paymentMsgDTO);
		} catch (Exception e) {
			log.error("receiveEcpayPaymentMsg",e);
			try {
				sendEcpayPaymentInfoDeadQueueProducer.send(paymentMsgDTO);
			} catch (PulsarClientException ex) {
				log.error("receiveEcpayPaymentMsg 放入死信队列", e);
			}
		}
	}

}

```

