# idempotent 幂等处理方案


### 1.原理

1.请求开始前，根据key查询
查到结果：报错
未查到结果：存入key-value-expireTime
key=ip+url+args

2.请求结束后，直接删除key
不管key是否存在，直接删除
是否删除，可配置

3.expireTime过期时间，防止一个请求卡死，会一直阻塞，超过过期时间，自动删除
过期时间要大于业务执行时间，需要大概评估下;

4.此方案直接切的是接口请求层面。

5.过期时间需要大于业务执行时间，否则业务请求1进来还在执行中，前端未做遮罩，或者用户跳转页面后再回来做重复请求2，在业务层面上看，结果依旧是不符合预期的。

6.建议delKey = false。即使业务执行完，也不删除key，强制锁expireTime的时间。预防5的情况发生。

7.实现思路：同一个请求ip和接口，相同参数的请求，在expireTime内多次请求，只允许成功一次。

8.页面做遮罩，数据库层面的唯一索引，先查询再添加，等处理方式应该都处理下。

9.此注解只用于幂等，不用于锁，100个并发这种压测，会出现问题，在这种场景下也没有意义，实际中用户也不会出现1s或者3s内手动发送了50个或者100个重复请求,或者弱网下有100个重复请求；


### 2.使用

- 1. 引入依赖

```java
<dependency>
    <groupId>cn.flood</groupId>
    <artifactId>flood-db-redis-lock</artifactId>
    <version>2.0.0</version>
</dependency>
```

- 2. 配置 redis 链接相关信息

```yaml
spring.lock.address: redis://192.168.1.204:6379
# 使用参数说明

> 配置参数说明

```properties
spring.lock.address: redis链接地址
spring.lock.password: redis密码
spring.lock.database: redis数据索引
spring.lock.waitTime: 获取锁最长阻塞时间（默认：60，单位：秒）
spring.lock.leaseTime: 已获取锁后自动释放时间（默认：60，单位：秒）
spring.lock.cluster-server.node-addresses : redis集群配置 如 127.0.0.1:7000,127.0.0.1:7001，127.0.0.1:7002
spring.lock.address 和 spring.lock.cluster-server.node-addresses 选其一即可
```

理论是支持 [redisson-spring-boot-starter](https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter) 全部配置


- 3. 接口设置注解

```java
@Idempotent(key = "#demo.username", expireTime = 3, info = "请勿重复查询")
@GetMapping("/test")
public String test(Demo demo) {
    return "success";
}
```


### idempotent 注解 配置详细说明


- 1. 幂等操作的唯一标识，使用spring el表达式 用#来引用方法参数 。 可为空则取 当前 url + args 做表示
    
```java
String key();
```


- 2. 有效期 默认：1 有效期要大于程序执行时间，否则请求还是可能会进来

```java
	int expireTime() default 1;
```

- 3. 时间单位 默认：s （秒）

```java
TimeUnit timeUnit() default TimeUnit.SECONDS;
```

- 4. 幂等失败提示信息，可自定义

```java
String info() default "重复请求，请稍后重试";
```

- 5. 是否在业务完成后删除key true:删除 false:不删除

```java
boolean delKey() default false;
```


redis 锁
1.在需要加分布式锁的方法上，添加注解@Rlock，如：
```java
@Service
public class TestService {

    @Rlock(waitTime = Long.MAX_VALUE)
    public String getValue(String param) throws Exception {
        if ("sleep".equals(param)) {//线程休眠或者断点阻塞，达到一直占用锁的测试效果
            Thread.sleep(1000 * 50);
        }
        return "success";
    }
}

```

2. 支持锁指定的业务key，如同一个方法ID入参相同的加锁，其他的放行。业务key的获取支持Spel，具体使用方式如下

@Rlock(keys = {"#key"}, lockType = LockType.Fair, waitTime=2, leaseTime=60, lockTimeoutStrategy= LockTimeoutStrategy.FAIL_FAST)
public void lockCode2(String key, @RlockKey int id) {
    System.out.println(key+id+"进来了。。。。"+Thread.currentThread().getName());
    try{
        Thread.sleep(55000);
    }catch (InterruptedException e){

    }
}

@Rlock(keys = {"#user.id", "user.name}, lockType = LockType.Fair, waitTime=2, leaseTime=60, lockTimeoutStrategy= LockTimeoutStrategy.FAIL_FAST)
public void lockCode2(User user) {
    System.out.println(key+id+"进来了。。。。"+Thread.currentThread().getName());
    try{
        Thread.sleep(55000);
    }catch (InterruptedException e){

    }
}



```
> @Rlock注解参数说明
```
@Rlock可以标注四个参数，作用分别如下

name：lock的name，对应redis的key值。默认为：类名+方法名

lockType：锁的类型，目前支持（可重入锁，公平锁，读写锁）。默认为：可重入锁

waitTime：获取锁最长等待时间。默认为：60s。同时也可通过spring.lock.waitTime统一配置

leaseTime：获得锁后，自动释放锁的时间。默认为：60s。同时也可通过spring.lock.leaseTime统一配置

keys:自定义业务key

lockTimeoutStrategy: 加锁超时的处理策略，可配置为不做处理、快速失败、阻塞等待的处理策略，默认策略为不做处理

customLockTimeoutStrategy: 自定义加锁超时的处理策略，需指定自定义处理的方法的方法名，并保持入参一致。

releaseTimeoutStrategy: 释放锁时，持有的锁已超时的处理策略，可配置为不做处理、快速失败的处理策略，默认策略为不做处理

customReleaseTimeoutStrategy: 自定义释放锁时，需指定自定义处理的方法的方法名，并保持入参一致。
```
# 锁超时说明
因为基于redis实现分布式锁，如果使用不当，会在以下场景下遇到锁超时的问题：
![锁超时处理逻辑](https://wx1.sinaimg.cn/large/7dfa0a7bly1g24obim6cnj20u80jzgnf.jpg "锁超时处理逻辑.jpg")

加锁超时处理策略(**LockTimeoutStrategy**)：
- **NO_OPERATION** 不做处理，继续执行业务逻辑
- **FAIL_FAST** 快速失败，会抛出KlockTimeoutException
- **KEEP_ACQUIRE** 阻塞等待，一直阻塞，直到获得锁，但在太多的尝试后，会停止获取锁并报错，此时很有可能是发生了死锁。
- **自定义(customLockTimeoutStrategy)** 需指定自定义处理的方法的方法名，并保持入参一致，指定自定义处理方法后，会覆盖上述三种策略，且会拦截业务逻辑的运行。

释放锁时超时处理策略(**ReleaseTimeoutStrategy**)：
- **NO_OPERATION** 不做处理，继续执行业务逻辑
- **FAIL_FAST** 快速失败，会抛出KlockTimeoutException
- **自定义(customReleaseTimeoutStrategy)** 需指定自定义处理的方法的方法名，并保持入参一致，指定自定义处理方法后，会覆盖上述两种策略, 执行自定义处理方法时，业务逻辑已经执行完毕，会在方法返回前和throw异常前执行。

**希望使用者清楚的意识到，如果没有对加锁超时进行有效的设置，那么设置释放锁时超时处理策略是没有意义的。**

*在测试模块中已集成锁超时策略的使用用例*
# 关于测试
工程test模块下，为分布式锁的测试模块。可以快速体验分布式锁的效果。