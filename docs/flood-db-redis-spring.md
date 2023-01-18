# redis-spring-boot-starter redis的spring整合

整合RedisTemplate与StringRedisTemplate，开箱即用，提供更友好更完善的API，更方便的调用，支持Lettuce客户端，并且在非集群模式下支持分片操作

##### 1. 添加maven依赖：
```
    <dependency>
    	<groupId>cn.flood</groupId>
    	<artifactId>redis-spring-boot-starter</artifactId>
    	<version>${flood.version}</version>
    </dependency>
```
##### 2. 使用说明：

@Autowired
private RedisService redisService


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



####多数据源配置
---
spring:
  redis:
    dynamic: 
      enabled: true
      #defaultDataSource可配可不配，不配的话，系统产生一个默认的数据源
      defaultDataSource: redisDataSoure1
      connection:
        ###多数据源固定配置格式
        redisDataSoure1: 
          host: 127.0.0.1
          port: 6379
          database: 0
          password: 123456
        #Redis哨兵模式
  #       database: 1        #选择redis的第二个数据库
  #       password: 123456   #redis密码
  #       sentinel:
  #         master: mymaster #哨兵的名字 #下面是所有哨兵集群节点
  #         nodes: 
  #           - 192.168.217.151:26379
  #           - 192.168.217.129:26379
  #           - 192.168.217.130:26379
        ###redis集群配置
  #      cluster: 
  #        nodes: 
  #          - 47.94.7.243:6001
  #          - 47.94.7.243:6002
  #        max-redirects: 2
          #lettuce连接池配置
          lettuce: 
            pool: 
              max-idle: 10
              max-wait: 100000
              min-idle: 5
        redisDataSoure2: 
          host: 10.218.223.99
          port: 6379
          database: 0
           #lettuce连接池配置
            lettuce: 
              pool: 
                max-idle: 10
                max-wait: 100000
                min-idle: 5

####多数据源使用 默认是默认数据源

    /*RedisTemplaterFactoryBuild多数据源工厂*/
    @Autowired
    private RedisTemplaterFactoryBuild redisTemplaterFactoryBuild;
    
    
     RedisTemplate<String, Object> redis = redisTemplaterFactoryBuild.getRedisTemplaterByName("redisDataSoure1");
     
     对应部分redis访问
     RedisBaseOperation
     
     如：
     T t =  RedisBaseOperation.get(redis, String key)