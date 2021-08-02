# redis-spring-boot-starter

<p align="center">
    <img src="https://img.shields.io/badge/JDK-1.8+-green.svg" />
    <img src="https://img.shields.io/maven-central/v/wiki.xsx/redis-spring-boot-starter.svg?label=Maven%20Central" />
    <img src="https://img.shields.io/:license-apache-blue.svg" />
    <a href='https://gitee.com/xsxgit/redis-spring-boot-starter/stargazers'>
        <img src='https://gitee.com/xsxgit/redis-spring-boot-starter/badge/star.svg?theme=dark' alt='star' />
    </a>
    <a target="_blank" href="//shang.qq.com/wpa/qunwpa?idkey=32947ea46f9f67f9de4ecf02e09b359ef89b02c56470272810c9776a15626518">
<img border="0" src="//pub.idqqimg.com/wpa/images/group.png" alt="Redis通用包组件讨论组" title="Redis通用包组件讨论组">
    </a>
</p>



#### 介绍
整合RedisTemplate与StringRedisTemplate，开箱即用，提供更友好更完善的API，更方便的调用，支持Jedis、Lettuce、Redisson等主流客户端，并且在非集群模式下支持分片操作

#### 软件架构
依赖spring-boot-starter-data-redis

#### 当前版本
*2.2.0* (已提交中央仓库)

注：Stream API需使用 spring-boot **2.2.0及以上版本**

#### 安装教程

```
mvn clean install
```

#### 文档地址
https://apidoc.gitee.com/xsxgit/redis-spring-boot-starter

#### 使用说明
##### 一、准备工作
1. 添加依赖：

```
<dependency>
    <groupId>wiki.xsx</groupId>
    <artifactId>redis-spring-boot-starter</artifactId>
    <version>X.X.X</version>
</dependency>
```
2. redis配置：

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

#### 特别说明

1. @since 为redis最低版本所支持的方法，例如@since redis 1.0.0表示1.0.0的redis版本即可使用该方法
2. XXXAsObj为对象类型序列化相关方法，XXX为字符串类型序列化相关方法
3. 默认使用 **JsonRedisSerializer**(自定义的json序列化器) 作为对象序列化工具
4. 分布式锁需依赖 **redisson** ，如需使用，请添加对应依赖

**注**：如**只使用分布式锁**，则**只需引入依赖**即可；如需**使用redisson客户端**，请先**排除Lettuce客户端**，然后可参照以下配置

redisson依赖：
```
<dependency>
     <groupId>org.redisson</groupId>
     <artifactId>redisson-spring-data-21</artifactId>
     <version>X.X.X</version>
</dependency>
```

[redisson配置参考（点击查看请详配置）](https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95)：

yml方式：
```yaml
# common spring boot settings
spring:
  redis:
    database: 0
    host: localhost
    password:
    port: 6379
    timeout: 0
    ssl: false

    # Redisson settings
    #path to redisson.yaml or redisson.json
    redisson:
      config: classpath:redisson.yaml
```

properties方式：
```properties
# 默认配置
# common spring boot settings
spring.redis.database=0
spring.redis.host=localhost
spring.redis.password=
spring.redis.port=6379
spring.redis.timeout=0
spring.redis.ssl=false

# Redisson settings
#path to redisson.yaml or redisson.json
spring.redis.redisson.config=classpath:redisson.yaml
```
