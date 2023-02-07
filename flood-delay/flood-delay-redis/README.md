[RedisDelayQueue接入方法](https://blog.csdn.net/tuixiusongwaimai/article/details/103527358)




# 应用场景
---

 - 创建订单10分钟之后自动支付
 - 叫预约单专车出行前30分钟发送短信提示
 - 订单超时取消
 - .......等等...


# 实现方式
---

 - 最简单的方式,定时扫表;例如每分钟扫表一次十分钟之后未支付的订单进行主动支付 ; 
 **优点:** 简单
 **缺点:** 每分钟全局扫表,浪费资源,有一分钟延迟
 
 - 使用RabbitMq 实现 [RabbitMq实现延迟队列](https://blog.csdn.net/u014308482/article/details/53036770)
    **优点:** 开源,现成的稳定的实现方案;
    **缺点:** RabbitMq是一个消息中间件;延迟队列只是其中一个小功能,如果团队技术栈中本来就是使用RabbitMq那还好,如果不是,那为了使用延迟队列而去部署一套RabbitMq成本有点大;
    
 - 使用Java中的延迟队列,DelayQueue
   **优点:** java.util.concurrent包下一个延迟队列,简单易用;拿来即用
   **缺点:** 单机、不能持久化、宕机任务丢失等等;

# 基于Redis自研延迟队列
---
既然上面没有很好的解决方案,因为Redis的zset、list的特性,我们可以利用Redis来实现一个延迟队列 **RedisDelayQueue**

## 设计目标

 - 实时性: 允许存在一定时间内的秒级误差
 - 高可用性:支持单机,支持集群
 - 支持消息删除:业务费随时删除指定消息
 - 消息可靠性: 保证至少被消费一次
 - 消息持久化: 基于Redis自身的持久化特性,上面的消息可靠性基于Redis的持久化,所以如果redis数据丢失,意味着延迟消息的丢失,不过可以做主备和集群保证;

## 数据结构

 - **Redis_Delay_Table**: 是一个Hash_Table结构；里面存储了所有的延迟队列的信息;KV结构；K=TOPIC:ID    V=CONENT;  V由客户端传入的数据,消费的时候回传；
 - **RD_ZSET_BUCKET**: 延迟队列的有序集合; 存放member=TOPIC:ID 和score=执行时间戳; 根据时间戳排序;
 - **RD_LIST_TOPIC**: list结构; 每个Topic一个list；list存放的都是当前需要被消费的延迟Job;

**设计图**
![设计图](https://img-blog.csdnimg.cn/20190808141929705.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)




## 任务的生命周期

 1. 新增一个**Job**,会在**Redis_Delay_Table**中插入一条数据,记录了业务消费方的 数据结构; **RD_ZSET_BUCKET** 也会插入一条数据,记录了执行时间戳;
 2. 搬运线程会去**RD_ZSET_BUCKET**中查找哪些执行时间戳**runTimeMillis**比现在的时间小;将这些记录全部删除;同时会解析出来每个任务的**Topic**是什么,然后将这些任务**push**到**Topic**对应的列表**RD_LIST_TOPIC**中;
 3. 每个Topic的List都会有一个监听线程去批量获取List中的待消费数据;获取到的数据全部扔给这个**Topic**的消费线程池
 4. 消息线程池执行会去Redis_Delay_Table查找数据结构,返回给回调接口,执行回调方法;

***以上所有操作,都是基于Lua脚本做的操作,Lua脚本执行的优点在于,批量命令执行具有原子性,事务性, 并且降低了网络开销,毕竟只有一次网络开销;***

### Push message

```java
@Autowired
private RDQueueTemplate rdQueueTemplate;

@GetMapping("/push")
public String push(String id) throws RDQException {
    Message<String> message = new Message<>();
    message.setTopic("order-cancel");
    message.setPayload(id);
    message.setDelayTime(10);
    rdQueueTemplate.asyncPush(message, (s, throwable) -> {
      // TODO async push result
    });
    return "推送成功";
}
```

### Subscribe topic

You need to implement `MessageListener`, subscribe to the related topic, process delay messages in the execute method.

> Ensure that the class was Spring managed.

```java
@Component
public class OrderCancelListener implements MessageListener<String> {
    
    @Override
    public String topic() {
        return "order-cancel";
    }
    
    @Override
    public ConsumeStatus execute(String data) {
        log.info("取消订单: {}", data);
        return ConsumeStatus.CONSUMED;
    }
    
}
```



### 不足

1. 因为Redis的持久化特性,做不到消息完全不丢失,如果要保证完成不丢失,Redis的持久化刷盘策略要收紧
2. 因为Codis不能使用BLPOP这种阻塞的形式,在获取消费任务的时候用了每秒一次去获取,有点浪费性能;
3. 支持消费者多实例部署,但是可能存在不能均匀的分配到每台机器上去消费;
4. 虽然支持redis集群,但是其实是伪集群,因为Lua脚本的原因,让他们都只能落在一台机器上;




###  总结
 1. **实时性**
   正常情况下 消费的时间误差不超过1秒钟; 极端情况下,一台实例宕机,另外的实例nextTime很迟; 那么最大误差是1分钟; 真正的误差来自于业务方的接口的消费速度
   
 2. **QPS** 
     完全视业务方的消费速度而定; 延迟队列不是瓶颈










