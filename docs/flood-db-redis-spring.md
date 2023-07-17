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
        max-redirects: 3 #在群集中执行命令时要遵循的最大重定向数目
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
  #       sentinel:ster #哨兵的名字 #下面是所有哨兵
  #         master: myma集群节点
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



#自定义配置。expire统一单位为分钟
spring:
  cache:
    multi:
      cacheNames: cache1,cache2,cache3
      caffeine:
        expireAfterWrite: 30
        maximumSize: 1000
      redis:
        defaultExpiration: 60
        expires: 
          cache1: 180 
          cache2: 180
          cache3: 180  


## 扩展
Spring 缓存注解简述如下：
  Spring为我们提供了几个注解来支持Spring Cache。其核心主要是@Cacheable和@CacheEvict。使用@Cacheable标记的方法在执行后Spring Cache将缓存其返回结果，而使用@CacheEvict标记的方法会在方法执行前或者执行后移除Spring Cache中的某些元素。下面我们将来详细介绍一下Spring基于注解对Cache的支持所提供的几个注解。
1.1    @Cacheable

       @Cacheable可以标记在一个方法上，也可以标记在一个类上。当标记在一个方法上时表示该方法是支持缓存的，当标记在一个类上时则表示该类所有的方法都是支持缓存的。对于一个支持缓存的方法，Spring会在其被调用后将其返回值缓存起来，以保证下次利用同样的参数来执行该方法时可以直接从缓存中获取结果，而不需要再次执行该方法。Spring在缓存方法的返回值时是以键值对进行缓存的，值就是方法的返回结果，至于键的话，Spring又支持两种策略，默认策略和自定义策略，这个稍后会进行说明。需要注意的是当一个支持缓存的方法在对象内部被调用时是不会触发缓存功能的。@Cacheable可以指定三个属性，value、key和condition。
1.1.1  value属性指定Cache名称

       value属性是必须指定的，其表示当前方法的返回值是会被缓存在哪个Cache上的，对应Cache的名称。其可以是一个Cache也可以是多个Cache，当需要指定多个Cache时其是一个数组。

   @Cacheable("cache1")//Cache是发生在cache1上的

   public User find(Integer id) {

      returnnull;

   }

 

   @Cacheable({"cache1", "cache2"})//Cache是发生在cache1和cache2上的

   public User find(Integer id) {

      returnnull;

   }

 
1.1.2  使用key属性自定义key

       key属性是用来指定Spring缓存方法的返回结果时对应的key的。该属性支持SpringEL表达式。当我们没有指定该属性时，Spring将使用默认策略生成key。我们这里先来看看自定义策略，至于默认策略会在后文单独介绍。

       自定义策略是指我们可以通过Spring的EL表达式来指定我们的key。这里的EL表达式可以使用方法参数及它们对应的属性。使用方法参数时我们可以直接使用“#参数名”或者“#p参数index”。下面是几个使用参数作为key的示例。

   @Cacheable(value="users", key="#id")

   public User find(Integer id) {

      returnnull;

   }

 

   @Cacheable(value="users", key="#p0")

   public User find(Integer id) {

      returnnull;

   }

 

   @Cacheable(value="users", key="#user.id")

   public User find(User user) {

      returnnull;

   }

 

   @Cacheable(value="users", key="#p0.id")

   public User find(User user) {

      returnnull;

   }

 

       除了上述使用方法参数作为key之外，Spring还为我们提供了一个root对象可以用来生成key。通过该root对象我们可以获取到以下信息。

属性名称
	

描述
	

示例

methodName
	

当前方法名
	

#root.methodName

method
	

当前方法
	

#root.method.name

target
	

当前被调用的对象
	

#root.target

targetClass
	

当前被调用的对象的class
	

#root.targetClass

args
	

当前方法参数组成的数组
	

#root.args[0]

caches
	

当前被调用的方法使用的Cache
	

#root.caches[0].name

 

       当我们要使用root对象的属性作为key时我们也可以将“#root”省略，因为Spring默认使用的就是root对象的属性。如：

   @Cacheable(value={"users", "xxx"}, key="caches[1].name")

   public User find(User user) {

      returnnull;

   }

 
1.1.3  condition属性指定发生的条件

       有的时候我们可能并不希望缓存一个方法所有的返回结果。通过condition属性可以实现这一功能。condition属性默认为空，表示将缓存所有的调用情形。其值是通过SpringEL表达式来指定的，当为true时表示进行缓存处理；当为false时表示不进行缓存处理，即每次调用该方法时该方法都会执行一次。如下示例表示只有当user的id为偶数时才会进行缓存。

   @Cacheable(value={"users"}, key="#user.id", condition="#user.id%2==0")

   public User find(User user) {

      System.out.println("find user by user " + user);

      return user;

   }

 
1.2     @CachePut

       在支持Spring Cache的环境下，对于使用@Cacheable标注的方法，Spring在每次执行前都会检查Cache中是否存在相同key的缓存元素，如果存在就不再执行该方法，而是直接从缓存中获取结果进行返回，否则才会执行并将返回结果存入指定的缓存中。@CachePut也可以声明一个方法支持缓存功能。与@Cacheable不同的是使用@CachePut标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，并将执行结果以键值对的形式存入指定的缓存中。

       @CachePut也可以标注在类上和方法上。使用@CachePut时我们可以指定的属性跟@Cacheable是一样的。

   @CachePut("users")//每次都会执行方法，并将结果存入指定的缓存中

   public User find(Integer id) {

      returnnull;

   }

 
1.3     @CacheEvict

       @CacheEvict是用来标注在需要清除缓存元素的方法或类上的。当标记在一个类上时表示其中所有的方法的执行都会触发缓存的清除操作。@CacheEvict可以指定的属性有value、key、condition、allEntries和beforeInvocation。其中value、key和condition的语义与@Cacheable对应的属性类似。即value表示清除操作是发生在哪些Cache上的（对应Cache的名称）；key表示需要清除的是哪个key，如未指定则会使用默认策略生成的key；condition表示清除操作发生的条件。下面我们来介绍一下新出现的两个属性allEntries和beforeInvocation。
1.3.1  allEntries属性

       allEntries是boolean类型，表示是否需要清除缓存中的所有元素。默认为false，表示不需要。当指定了allEntries为true时，Spring Cache将忽略指定的key。有的时候我们需要Cache一下清除所有的元素，这比一个一个清除元素更有效率。

   @CacheEvict(value="users", allEntries=true)

   public void delete(Integer id) {

      System.out.println("delete user by id: " + id);

   }

 
1.3.2  beforeInvocation属性

       清除操作默认是在对应方法成功执行之后触发的，即方法如果因为抛出异常而未能成功返回时也不会触发清除操作。使用beforeInvocation可以改变触发清除操作的时间，当我们指定该属性值为true时，Spring会在调用该方法之前清除缓存中的指定元素。

   @CacheEvict(value="users", beforeInvocation=true)

   public void delete(Integer id) {

      System.out.println("delete user by id: " + id);

   }

 

       其实除了使用@CacheEvict清除缓存元素外，当我们使用Ehcache作为实现时，我们也可以配置Ehcache自身的驱除策略，其是通过Ehcache的配置文件来指定的。由于Ehcache不是本文描述的重点，这里就不多赘述了，想了解更多关于Ehcache的信息，请查看我关于Ehcache的专栏。

 
1.4     @Caching

       @Caching注解可以让我们在一个方法或者类上同时指定多个Spring Cache相关的注解。其拥有三个属性：cacheable、put和evict，分别用于指定@Cacheable、@CachePut和@CacheEvict。

   @Caching(cacheable = @Cacheable("users"), evict = { @CacheEvict("cache2"),

         @CacheEvict(value = "cache3", allEntries = true) })

   public User find(Integer id) {

      returnnull;

   }