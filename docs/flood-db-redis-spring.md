# redis-spring-boot-starter redis的spring整合

整合RedisTemplate与StringRedisTemplate，开箱即用，提供更友好更完善的API，更方便的调用，支持Jedis、Lettuce、Redisson等主流客户端，并且在非集群模式下支持分片操作

##### 1. 添加maven依赖：
```
    <dependency>
    	<groupId>cn.flood</groupId>
    	<artifactId>redis-spring-boot-starter</artifactId>
    	<version>${flood.version}</version>
    </dependency>
```
##### 2. 使用说明：

yml方式：
```yaml
# 默认配置
spring:
  redis:
    database: 0
    host: localhost
    password: 
    port: 6379
    timeout: 0
    ssl: false
    lettuce:
      pool:
        max-wait: -1ms
        max-active: 8
        max-idle: 8
        min-idle: 0
```
#集群配置    
---
spring:
  redis:
    password: 
    encode: utf-8
    database: 0   #Redis默认情况下有16个分片，这里配置具体使用的分片，默认是0
    timeout: 10s  # 数据库连接超时时间，2.0 中该参数的类型为Duration，这里在配置的时候需要指明单位
    cluster:
        max-redirects: 3
        nodes: 
          - 47.94.7.243:6001
          - 47.94.7.243:6002
          - 47.94.7.243:6003
          - 47.94.7.243:6004
          - 47.94.7.243:6005
          - 47.94.7.243:6006
    # 连接池配置，2.0中直接使用jedis或者lettuce配置连接池
    lettuce:
      pool:
       # 连接池最大活跃连接数（使用负值表示没有限制） 默认 8,负数为不限制
       max-active: 20
       # 连接池中的最大空闲连接 默认 8
       max-idle: 20
       # 最小空闲连接数
       min-idle: 15
       # 连接池最大阻塞等待时间（使用负值表示没有限制） 默认 -1
       max-wait: 30000


properties方式：
```properties
# 默认配置
spring.redis.database=0
spring.redis.host=localhost
spring.redis.password=
spring.redis.port=6379
spring.redis.timeout=0
spring.redis.ssl=false
spring.redis.lettuce.pool.max-wait=1ms
spring.redis.lettuce.pool.max-active=8
spring.redis.lettuce.pool.max-idle=8
spring.redis.lettuce.pool.min-idle=0
```

##### 二、开始使用

获取操作实例：
```
// 获取默认数据库实例(DB)
DBHandler dbHandler = RedisUtil.getDBHandler();
...

// 获取数据库索引为2的数据库实例(DB)
DBHandler dbHandler = RedisUtil.getDBHandler(2);
...
```
#### 实例说明
| 实例 | 数据类型 | 获取方式 |
| :------: | :------: | :------: |
| NumberHandler | 数字(Number) | RedisUtil.getNumberHandler()<br>RedisUtil.getNumberHandler(dbIndex) |
| StringHandler | 字符串(String) | RedisUtil.getStringHandler()<br>RedisUtil.getStringHandler(dbIndex) |
| HashHandler | 哈希(Hash) | RedisUtil.getHashHandler()<br>RedisUtil.getHashHandler(dbIndex) |
| SetHandler | 无序集合(Set) | RedisUtil.getSetHandler()<br>RedisUtil.getSetHandler(dbIndex) |
| ZsetHandler | 有序集合(Zset) | RedisUtil.getZsetHandler()<br>RedisUtil.getZsetHandler(dbIndex) |
| HyperLogLogHandler | 基数(HyperLogLog) | RedisUtil.getHyperLogLogHandler()<br>RedisUtil.getHyperLogLogHandler(dbIndex) |
| BitmapHandler | 位图(Bitmap) | RedisUtil.getBitmapHandler()<br>RedisUtil.getBitmapHandler(dbIndex) |
| GeoHandler | 地理位置(Geo) | RedisUtil.getGeoHandler()<br>RedisUtil.getGeoHandler(dbIndex) |
| KeyHandler | 键(Key) | RedisUtil.getKeyHandler()<br>RedisUtil.getKeyHandler(dbIndex) |
| ScriptHandler | 脚本(Lua Script) | RedisUtil.getScriptHandler()<br>RedisUtil.getScriptHandler(dbIndex) |
| PubSubHandler | 发布订阅(Pubsub) | RedisUtil.getPubSubHandler()<br>RedisUtil.getPubSubHandler(dbIndex) |
| StreamHandler | 流(Stream) | RedisUtil.getStreamHandler()<br>RedisUtil.getStreamHandler(dbIndex)<br>RedisUtil.getStreamHandler(dbIndex, mapper) |
| DBHandler | 数据库(DB) | RedisUtil.getDBHandler()<br>RedisUtil.getDBHandler(dbIndex) |
| SentinelHandler | 哨兵(Sentinel) | RedisUtil.getSentinelHandler()<br>RedisUtil.getSentinelHandler(dbIndex) |
| ClusterHandler | 集群(Cluster) | RedisUtil.getClusterHandler() |
| CustomCommandHandler | 自定义命令(CustomCommand) | RedisUtil.getCustomCommandHandler()<br>RedisUtil.getCustomCommandHandler(dbIndex) |
| RedisLockHandler | 分布式锁(Lock) | RedisUtil.getRedisLockHandler()<br>RedisUtil.getRedisLockHandler(dbIndex) |
| TransactionHandler | 事务(Transaction) | RedisUtil.getTransactionHandler()<br>RedisUtil.getTransactionHandler(dbIndex) |

#### 事务使用示例
```
List execute = RedisUtil.getTransactionHandler(2).execute(handler -> {
    // 开启监控
    handler.watch("xx", "test");
    // 开启事务
    handler.beginTransaction();
    // 获取对应事务字符串助手
    StringHandler stringHandler = handler.getStringHandler();
    // 执行操作
    stringHandler.set("xx", "hello");
    stringHandler.append("xx", "world");
    stringHandler.append("xx", "!");
    // 获取对应事务数字助手
    NumberHandler numberHandler = handler.getNumberHandler();
    numberHandler.addLong("test", 100);
    numberHandler.incrementLong("test");
    numberHandler.incrementLong("test");
    numberHandler.incrementLong("test");
    // 提交事务返回结果
    return handler.commit();
});
```
