**版本下载**
---
    <dependency>
    	<groupId>cn.flood</groupId>
    	<artifactId>flood-db-sharding</artifactId>
    	<version>2.0.0</version>
    </dependency>


**配置介绍**
---

---
application.yml
   
########################################################

###sharding database setting. 读写分离

########################################################
sharding:
  jdbc:
    data-sources:
      ds_master: 
        driverClassName: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/flood_mgr?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
        username: 'root'
        password: 'root'
      ds_slave0: 
        driverClassName: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/sharding_flood-mgr?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
        username: 'root'
        password: 'root'
    ###配置读写分离
    master-slave-rule:
      name: ds_ms
      ###配置从库选择策略，提供轮询与随机(ROUND_ROBIN，RANDOM)，这里选择用轮询 如果从做了集群 查询时候做轮训查询
      load-balance-algorithm-type: ROUND_ROBIN
      ####指定主数据库
      masterDataSourceName: ds_master
      ####指定从数据库
      slaveDataSourceNames: 
        - ds_slave0
    props:
      sql.show: true
    druid: #druid连接池配置
      #配置初始化大小/最小/最大
      initial-size: 1
      min-idle: 2
      max-active: 12
      #获取连接等待超时时间
      max-wait: 30000
      validation-query: select 1
      validation-query-timeout: 5
      test-on-borrow: true
      test-on-return: false
      test-while-idle: true
      remove-abandoned: true
      remove-abandoned-timeout: 120
      #间隔多久进行一次检测，检测需要关闭的空闲连接
      time-between-eviction-runs-millis: 30000
      #一个连接在池中最小生存的时间
      min-evictable-idle-time-millis: 60000
      #打开PSCache，并指定每个连接上PSCache的大小。oracle设为true，mysql设为false。分库分表较多推荐设置为false
      pool-prepared-statements: false
      max-pool-prepared-statement-per-connection-size: 20
      #监控统计拦截的filters
      filters: stat,wall
      # 通过connectProperties属性来打开mergeSql功能；慢SQL记录 
      connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=10
      # 合并多个DruidDataSource的监控数据
      use-global-data-source-stat: true
---
spring:      
  pagehelper:
    # 分页注册别名默认mysql（hsqldb、h2、postgresql、phoenix、mysql、mariadb、sqlite、oracle、db2、informix、sqlserver、sqlserver2012、derby、dm-达梦）
    helperDialect: mysql

**批量更新或者插入**      
开发者只需在对应的 mapper 接口方法上添加 @BatchInsert 注解，便可由框架代为批量插入。

代码实现
Mapper接口定义

import cn.flood.model.Student;
import org.mybatis.zebra.annotation.BatchInsert;

import java.util.List;

public interface StudentDao {
    Student get(String name);

    void create(Student student);

    @BatchInsert
    void batchCreate(List<Student> students);
}
如上图，在 batchCreate 的方法上面添加 @BatchInsert 注解，同时必须确保batchCreate这个方法的只有一个且为 List 类型的参数。

Mapper.xml

Mapper 文件中对应的 id 填上对应的 insert 语句，注意该 insert 的参数类型是 List 的元素的类型，而非 List 类型，一般不用配置。

在调用 Mapper batchCreate 接口时，传入多个待插入的对象， 框架会多次调用 insert 语句，最后提交。

<mapper namespace="cn.flood.dao.StudentDao">
    <resultMap type="cn.flood.model.Student" id="StudentMap">
        <result column="name" property="name"/>
        <result column="clz" property="clz"/>
        <result column="age" property="age"/>
    </resultMap>

    <insert id="batchCreate">
        insert into t_student (name, age, clz) values(#{name}, #{age}, #{clz})
    </insert>

</mapper>

  
  
