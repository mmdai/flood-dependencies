# spring boot starter for RocketMQ [![Build Status](https://travis-ci.org/maihaoche/rocketmq-spring-boot-starter.svg?branch=master)](https://travis-ci.org/maihaoche/rocketmq-spring-boot-starter) [![Coverage Status](https://coveralls.io/repos/github/maihaoche/rocketmq-spring-boot-starter/badge.svg?branch=master)](https://coveralls.io/github/maihaoche/rocketmq-spring-boot-starter?branch=master)

<p><a href="http://search.maven.org/#search%7Cga%7C1%7Ccom.maihaoche"><img src="https://maven-badges.herokuapp.com/maven-central/com.maihaoche/spring-boot-starter-rocketmq/badge.svg" alt="Maven Central" style="max-width:100%;"></a><a href="https://github.com/maihaoche/rocketmq-spring-boot-starter/releases"><img src="https://camo.githubusercontent.com/795f06dcbec8d5adcfadc1eb7a8ac9c7d5007fce/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f72656c656173652d646f776e6c6f61642d6f72616e67652e737667" alt="GitHub release" data-canonical-src="https://img.shields.io/badge/release-download-orange.svg" style="max-width:100%;"></a>


### 项目介绍

[Rocketmq](https://github.com/apache/rocketmq) 是由阿里巴巴团队开发并捐赠给apache团队的优秀消息中间件，承受过历年双十一大促的考验。

你可以通过本项目轻松的集成Rocketmq到你的SpringBoot项目中。
本项目主要包含以下特性

* [x] 同步发送消息
* [x] 异步发送消息
* [x] 广播发送消息
* [x] 有序发送和消费消息
* [x] 发送延时消息
* [x] 消息tag和key支持
* [x] 自动序列化和反序列化消息体
* [x] 消息的实际消费方IP追溯
* [x] 发送事务消息(NEW)



### 简单入门实例


##### 1. 添加maven依赖：

```java
<dependency>
    <groupId>cn.flood</groupId>
    <artifactId>flood-spring-cloud-starter-rocketmq</artifactId>
    <version>2.0.0</version>
</dependency>
```

##### 2. 添加配置：

```java
spring:
    rocketmq:
      name-server-address: 172.21.10.111:9876
      # 可选, 如果无需发送消息则忽略该配置
      producer-group: local_pufang_producer
      # 发送超时配置毫秒数, 可选, 默认3000
      send-msg-timeout: 5000
      # 追溯消息具体消费情况的开关，默认打开
      #trace-enabled: false
      # 是否启用VIP通道，默认打开
      #vip-channel-enabled: false
```

##### 4. 构建消息体

通过我们提供的`Builder`类创建消息对象:

除了消息对象，支持定义的有：

 topic : 消息发送的topic
 tag : 消息携带的tag
 key : 消息对应的key
 delayTimeLevel : 消息延时级别
其中，消息延时级别定义在枚举类DelayTimeLevel中。

举个栗子：
 MessageBuilder.of("message body").topic("suclogger-test-cluster").build();
 MessageBuilder.of(new MSG_POJO()).topic("some-msg-topic").build();
构建的结果是消息体是message body，topic是suclogger-test-cluster。


##### 5. 创建发送方
通用准则
同一个应用使用同一个pruducerGroup，放在spring配置文件中
打印消息日志，务必要打印 sendresult：sync方式重写doAfterSynSend方法，async方法在callback中处理
如果不关注消息是否成功发送到broker，使用sendOneWay方法发送消息
一个应用尽可能用一个 Topic，消息子类型用 tags 来标识，因为Topic过多会影响Broker性能
全局单例的Producer
你可以全局只继承一次AbstractMQProducer，然后用这个producer发送不同topic和tag的消息：


@MQProducer
class DemoProducerWithTopicAndTag : AbstractMQProducer()

...

@Autowired
DemoProducerWithTopicAndTag yourProducer;

yourProducer.sendOneWay(String topic, String tag, Object msgObj)
携带Topic(和Tag)的Producer
因为通常topic是写在配置文件中的，如果维护全局单例的Producer就需要多个地方注入topic，如果你觉得这样比较麻烦，你可以创建多个producer，每个producer专用于发送同一个topic(和Tag)的消息，放心，底层的的producer依然是单例：



//创建一条消息对象，指定其主题、标签和消息内容
yourProducer.syncSend(MessageBuilder.of("Spring RocketMQ demo 10").key("key").topic("some-msg-topic").delayTimeLevel(DelayTimeLevel.MINUTE_1).
        build());
```java
@MQProducer
public class DemoProducer extends AbstractMQProducer{
}
```

##### 6. 创建消费方

**支持springEL风格配置项解析**，如存在`suclogger-test-cluster`配置项，会优先将topic解析为配置项对应的值。
通用准则
消费代码要做到幂等，因为在某些极端情况下，同一条消息在同一个consumerGroup中会被多次消费

```java
@MQConsumer(topic = "${suclogger-test-cluster}", consumerGroup = "local_sucloger_dev")
public class DemoConsumer extends AbstractMQPushConsumer {

    @Override
    public boolean process(Object message, Map extMap) {
        // extMap 中包含messageExt中的属性和message.properties中的属性
        System.out.println(message);
        return true;
    }
}
```

##### 7. 发送消息：


```java

// 注入发送者
@Autowired
private DemoProducer demoProducer;
    
...
    
// 发送
demoProducer.syncSend(msg)
    
```



------
##### 8. 广播消息：
在消费者的注解中添加声明：messageMode=MessageExtConst.MESSAGE_MODE_BROADCASTING

@MQConsumer(topic = "suclogger", consumerGroup = "wms.tms.topic", messageMode=MessageExtConst.MESSAGE_MODE_BROADCASTING)
class DemoConsumer : AbstractMQPushConsumer<DemoMessage>() {
    override fun process(message:DemoMessage) : Boolean {
        return true
    }
}


##### 9. 顺序消息
发送有序消息
封装的逻辑是通过hashKey来选择queue来确保分区有序。

使用接口中定义的以Orderly结尾的方法如：syncSendOrderly(String topic, String tag, Object msgObj, String hashKey)，传入的hashKey用于选择发送消息使用的queue。

消费时保证有序
在消费者的注解中添加声明：consumeMode=CONSUME_MODE_ORDERLY

@MQConsumer(topic = "suclogger", consumerGroup = "wms.tms.topic", consumeMode=CONSUME_MODE_ORDERLY)
class DemoConsumer : AbstractMQPushConsumer<DemoMessage>() {
    override fun process(message:DemoMessage) : Boolean {
        return true
    }
}



##### 5.1 事务消息发送方#####

```java
@MQTransactionProducer(producerGroup = "${camaro.mq.transactionProducerGroup}")
public class DemoTransactionProducer extends AbstractMQTransactionProducer {

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        // executeLocalTransaction
        return LocalTransactionState.UNKNOW;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        // LocalTransactionState.ROLLBACK_MESSAGE
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
```

