# flood-mq-pulsar 

##### 1. 添加maven依赖：
```
<dependency>
    <groupId>cn.flood</groupId>
    <artifactId>flood-spring-cloud-starter-pulsar</artifactId>
    <version>${flood.version}</version>
</dependency>
```

##### 2. 使用说明：

`一、背景
 为保持统一，方便使用，需要封装一个Pulsar的公共组件，用于消息发送和接收，减少重复造轮子。
 
 二、生产者
 
 1、支持同步消息发送
 
 2、支持异步消息发送
 
 3、支持同步延时消息发送
 
 4、支持异步延时消息发送
 
 延时概念
 部分消息并不需要即时消费，而是在等待一段时间再进行消费，起到延时队列的作用
 延时队列在项目中的应用还是比较多的，尤其像电商类平台：
 
 订单成功后，在30分钟内没有支付，自动取消订单
 外卖平台发送订餐通知，下单成功后60s给用户推送短信。
 如果订单一直处于某一个未完结状态时，及时处理关单，并退还库存
 淘宝新建商户一个月内还没上传商品信息，将冻结商铺等
 生产者使用示例
 注入{#PulsarTemplate}，参数{tenancy、namespace}为空时默认使用配置文件指定的值
 
 /**
  * 生产者发送消息示例
  * @author 
  * @date 2020/8/7 5:45 下午
  */
 @Component
 @Slf4j
 public class ProducerSend {
     @Autowired
     private PulsarTemplate pulsarTemplate;
 
     /**
      * 同步发送消息
      */
     public void send() throws PulsarClientException {
         for(int i=1; i<=1000; i++) {
             MessageVo messageVo = new MessageVo();
             messageVo.setName("hello pulsar, index is " + i);
             Map<String, MessageId> result = pulsarTemplate.createBuilder()
                     .persistent(true)
                     .tenancy("GEO_shopline")
                     .namespace("GEO_EC_product")
                     .topics("sl-test")
                     .send(JsonUtil.toJson(messageVo));
             log.info("信息发送成功, result={}", JsonUtil.toJson(result));
         }
     }
 
     /**
      * 同步发送延时消息
      */
     public void sendByDelay() throws PulsarClientException {
         for(int i=1; i<=1000; i++) {
             MessageVo messageVo = new MessageVo();
             messageVo.setName("hello pulsar, index is " + i);
             Map<String, MessageId> result = pulsarTemplate.createBuilder()
                     .persistent(true)
                     .tenancy("GEO_shopline")
                     .namespace("GEO_EC_product")
                     .topics("sl-test")
                     .delayAfter(30, TimeUnit.SECONDS)
                     .send(JsonUtil.toJson(messageVo));
             log.info("信息发送成功, result={}", JsonUtil.toJson(result));
         }
     }
 }
 
 三、消费者
 
 1. 支持同步接收
 当前基于有界队列的线程池接收，最大线程数16个，即每个服务最多支持16个消费者
 
 2. 支持异步接收
 建议使用异步模式接收消息
 
 3. 支持消费者拦截
 在消息消费前后允许进行拦截，做一些定制化操作
 
 4. 支持消息重试和死信
 如果需要做消息重试，建议分两个队列，正常队列消费失败直接进入重试队列（此重试队列即相对于正常队列的死信队列），在重试队列进行消息重试，防止阻塞正常队列消费，导致消息堆积。重试队列达到最大重试次数后，放入重试队列的死信队列，告警并人工干预
 
 5. 支持注解
 继承{#ConsumerMessageListener}，或实现{#MessageListener}类，并配置@PulsarListener注解即可实现消息订阅及监听
 
 四、使用指北
 
 1、引入注解
 继承{#ConsumerMessageListener}，或实现{#MessageListener}类，并配置@PulsarListener注解
 
 2、配置文件属性说明
 #Pulsar服务器地址
 pulsar.serviceUrl=pulsar://127.0.0.1:6650
 #主题是否需要持久化，默认true
 pulsar.persistent=true
 #租户，配置的话只支持配置单租户，多租户的话可以通过实现多个消息监听，在注解中指定
 pulsar.tenancy=GEO_shopline
 #命名空间，配置的话只支持配置单个命名空间，多个命名空间的话可以通过实现多个消息监听，在注解中指定
 pulsar.namespace=GEO_EC_product
 #重试消费最大次数
 pulsar.consumer.maxRedeliverCount=5
 #消费者订阅名
 pulsar.consumer.subscriptionName=sl-product-service
 #消费类型，当前对Shared支持较好，其它模式的参数配置还不齐全，默认Shared
 pulsar.consumer.subscriptionType=Shared
 #订阅模式，默认持久化
 pulsar.consumer.subscriptionMode=Durable
 #指定消费者缓冲队列大小
 pulsar.consumer.receiverQueueSize=100
 #ACK确认超时时间，默认30s，单位毫秒
 pulsar.consumer.ackTimeout=30000
 #是否开启异步消费，默认开启，建议使用异步
 pulsar.consumer.enableAsync=true
 #是否开启消息重试，指定是否进入重试队列的重试
 pulsar.consumer.enableRetry=false
 #消费者否认ACK确认后，下次延迟一定时间后再重新消费
 pulsar.consumer.negativeAckRedeliveryDelay=5000
 #消费者名称
 pulsar.consumer.consumerName=sl-test-consumer
 #生产者发送消息超时时间，默认5s，单位毫秒，当前暂不支持单独指定某一生产者发送超时时间
 pulsar.producer.sendTimeout=5000
 
 配置项	示例值	说明
 pulsar.serviceUrl	pulsar://127.0.0.1:6650	Pulsar服务器地址
 pulsar.persistent	true	主题是否需要持久化，默认true
 pulsar.tenancy	GEO_shopline	租户，配置的话只支持配置单租户，多租户的话可以通过实现多个消息监听，在注解中指定
 pulsar.namespace	GEO_EC_product	命名空间，配置的话只支持配置单个命名空间，多个命名空间的话可以通过实现多个消息监听，在注解中指定
 pulsar.consumer.maxRedeliverCount	3	重试消费最大次数，默认3次
 pulsar.consumer.subscriptionName	sl-product-service	消费者订阅名
 pulsar.consumer.subscriptionType	Shared	消费类型，当前对Shared支持较好，其它模式的参数配置还不齐全，默认Shared
 pulsar.consumer.subscriptionMode	Durable	订阅模式，默认持久化
 pulsar.consumer.receiverQueueSize	100	指定消费者缓冲队列大小，默认100
 pulsar.consumer.ackTimeout	30000	ACK确认超时时间，默认30s，单位毫秒
 pulsar.consumer.enableAsync	true	是否开启异步消费，默认开启，建议使用异步
 pulsar.consumer.enableRetry	false	是否开启消息重试，指定是否进入重试队列的重试，默认false
 pulsar.consumer.negativeAckRedeliveryDelay	5000	消费者否认ACK确认后，下次延迟一定时间后再重新消费，单位毫秒
 pulsar.consumer.consumerName	sl-test-consumer	消费者名称
 pulsar.producer.sendTimeout	5000	生产者发送消息超时时间，默认5s，单位毫秒，当前暂不支持单独指定某一生产者发送超时时间
 #除了pulsar.serviceUrl为必传项，其它配置都可以不传，在{@PulsarListener}注解中指定即可
 #消费者消息监听已经集成，参考{DemoMessageListener}
 #消息发送模板已经集成，参考{PulsarTemplate}
 #消息者拦截器已经集成，参考{DemoConsumerInterceptor}
 #支持消息延迟发送，参考{ProducerSend}
 
 消费者使用示例一：简单配置
 #引用pulsar公共包设定参数，commons-pulsar会加载到配置中
 pulsar.serviceUrl=pulsar://127.0.0.1:6650
 pulsar.tenancy=GEO_shopline
 pulsar.namespace=GEO_EC_product
 /**
  * 消息监听器示例，需要引入注解，继承{#ConsumerMessageListener}，或实现{#MessageListener}类
  * commons-pulsar会自动扫描{'@PulsarListener'}注解，在消费成功后，触发消息处理事件
  * @author 
  * @date 2020/8/6 2:15 下午
  */
 @Slf4j
 @PulsarListener(
     bindings = {@TopicBinding(@Consume("sl-test"))}
 )
 public class DemoMessageListener extends ConsumerMessageListener {
     /**
      * 消息处理
      *
      * @param consumer 消费者
      * @param message  消息
      */
     @Override
     public void handle(Consumer<String> consumer, Message<String> message) {
         log.info("处理消息，topic：{}", message.getTopicName());
     }
 }
 
 消费者使用示例二：重试和死信配置
 #引用pulsar公共包设定参数，commons-pulsar会加载到配置中
 pulsar.serviceUrl=pulsar://127.0.0.1:6650
 pulsar.tenancy=GEO_shopline
 pulsar.namespace=GEO_EC_product
 /**
  * 消息监听器示例，需要引入注解，继承{#ConsumerMessageListener}，或实现{#MessageListener}类
  * commons-pulsar会自动扫描{'@PulsarListener'}注解，在消费成功后，触发消息处理事件
  * @author 
  * @date 2020/8/6 2:15 下午
  */
 @Slf4j
 @PulsarListener(
     bindings = {
         @TopicBinding(
             @Consume(
                 value = "sl-test",
                 enableRetry = "true",  //开启消息重试，必须配置为开启重试队列才生效
                 retryTopic = "sl-test-RETRY", //默认格式：<tenancy>/<namespace>/<topicName>-RETRY
                 deadLetterTopic = "sl-test_DLQ", //默认格式：<tenancy>/<namespace>/<topicName>-DLQ
                 maxRedeliverCount = 5	//重试次数，若已在配置文件指定，注解配置会覆盖配置文件配置
             )
         )
     }
 )
 public class DemoMessageListener extends ConsumerMessageListener {
     /**
      * 消息处理
      *
      * @param consumer 消费者
      * @param message  消息
      */
     @Override
     public void handle(Consumer<String> consumer, Message<String> message) {
         if(message.getTopicName().contains(PulsarConstants.RETRY)) {
             log.info("重试消息，topic：{}", message.getTopicName());
         } else {
             log.info("正常消息，topic：{}", message.getTopicName());
         }
     }
 
     /**
      * confirm the message
      *
      * @param consumer pulsar consumer
      * @param msg      new message
      */
     @Override
     public void acknowledge(Consumer<String> consumer, Message<String> msg) throws PulsarClientException {
 		//本次消费失败，指定延迟多久后重试消费，消息仍然发回当前主题队列       
         consumer.reconsumeLater(msg, 5000, TimeUnit.MILLISECONDS);
     }
 
 }
 
 
 @Slf4j
 @PulsarListener(bindings = @TopicBinding(@Consume("sl-test-DLQ")))
 public class DeadMessageListener extends ConsumerMessageListener {
     /**
      * 消息处理
      *
      * @param consumer 消费者
      * @param message  消息
      */
     @Override
     public void handle(Consumer<String> consumer, Message<String> message) {
         log.info("死信消息消费成功，topic：{}", message.getTopicName());
     }
 
     /**
      * confirm the message
      *
      * @param consumer pulsar consumer
      * @param msg      new message
      * @throws PulsarClientException ack error
      */
     @Override
     public void acknowledge(Consumer<String> consumer, Message<String> msg) throws PulsarClientException {
         consumer.acknowledge(msg);
     }
 }
 
 消费者使用示例三：消费拦截
 #引用pulsar公共包设定参数，commons-pulsar会加载到配置中
 pulsar.serviceUrl=pulsar://127.0.0.1:6650
 pulsar.tenancy=GEO_shopline
 pulsar.namespace=GEO_EC_product
 package com.shopline.common.pulsar.demo;
 
 import lombok.extern.slf4j.Slf4j;
 import org.apache.pulsar.client.api.Consumer;
 import org.apache.pulsar.client.api.ConsumerInterceptor;
 import org.apache.pulsar.client.api.Message;
 import org.apache.pulsar.client.api.MessageId;
 
 import java.util.Set;
 
 /**
  * 消费者拦截器示例，可以在消息消费之前进行修改，也可以对特定事件处理
  * @author 
  * @date 2020/8/8 10:34 下午
  */
 @Slf4j
 public class DemoConsumerInterceptor implements ConsumerInterceptor<String> {
     @Override
     public void close() {
 
     }
 
     @Override
     public Message<String> beforeConsume(Consumer<String> consumer, Message<String> message) {
         log.info("消费之前，topic: {}, message: {}", message.getTopicName(), message.getValue());
         return message;
     }
 
     @Override
     public void onAcknowledge(Consumer<String> consumer, MessageId messageId, Throwable exception) {
 
     }
 
     @Override
     public void onAcknowledgeCumulative(Consumer<String> consumer, MessageId messageId, Throwable exception) {
 
     }
 
     @Override
     public void onNegativeAcksSend(Consumer<String> consumer, Set<MessageId> messageIds) {
 
     }
 
     @Override
     public void onAckTimeoutSend(Consumer<String> consumer, Set<MessageId> messageIds) {
 
     }
 }
 
 /**
  * 消息监听器示例，需要引入注解，继承{#ConsumerMessageListener}，或实现{#MessageListener}类
  * commons-pulsar会自动扫描{'@PulsarListener'}注解，在消费成功后，触发消息处理事件
  * @author 
  * @date 2020/8/6 2:15 下午
  */
 @Slf4j
 @PulsarListener(
     bindings = {
         @TopicBinding(
             @Consume(
                 value = "sl-test",
                 consumerInterceptors = {DemoConsumerInterceptor.class}
             )
         )
     }
 )
 public class DemoMessageListener extends ConsumerMessageListener {
     /**
      * 消息处理
      *
      * @param consumer 消费者
      * @param message  消息
      */
     @Override
     public void handle(Consumer<String> consumer, Message<String> message) {
         if(message.getTopicName().contains(PulsarConstants.RETRY)) {
             log.info("重试消息，topic：{}", message.getTopicName());
         } else {
             log.info("正常消息，topic：{}", message.getTopicName());
         }
     }
 }
 
 五、注意点
 1、注解配置比配置文件优先
 2、多个消费者属性不同时，应在注解指定，配置文件各项配置均为统一指定
