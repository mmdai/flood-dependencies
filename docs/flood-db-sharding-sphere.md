# flood-db-sharding-sphere 分库分表

##### 1. 添加maven依赖：
```
    <dependency>
    	<groupId>cn.flood</groupId>
    	<artifactId>flood-db-sharding-sphere</artifactId>
    	<version>${flood.version}</version>
    </dependency>
```

##### 2. 使用说明：
---
application.yml
   
########################################################

###sharding database setting. 分库分表8库8表的配置

########################################################
sharding:
  jdbc:
    data-sources[0]:
      driverClassName: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/order?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
      username: 'root'
      password: 'root'
    data-sources[1]:
      driverClassName: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/order_db_0?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
      username: 'root'
      password: 'root'
    data-sources[2]:
      driverClassName: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/order_db_1?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
      username: 'root'
      password: 'root'
    data-sources[3]:
      driverClassName: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/order_db_2?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
      username: 'root'
      password: 'root'
    data-sources[4]:
      driverClassName: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/order_db_3?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
      username: 'root'
      password: 'root'
    data-sources[5]:
      driverClassName: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/order_db_4?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
      username: 'root'
      password: 'root'
    data-sources[6]:
      driverClassName: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/order_db_5?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
      username: 'root'
      password: 'root'
    data-sources[7]:
      driverClassName: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/order_db_6?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
      username: 'root'
      password: 'root'
    data-sources[8]:
      driverClassName: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/order_db_7?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
      username: 'root'
      password: 'root'
    ###配置#order_no表分片规则
    table-rules[0]:
      #逻辑表名
      logic-table: order_info_sharded_by_user_id_
      #库分片列名称，多个列以逗号分隔
      db-sharding-columns: order_no,user_id
      #库分片策略类
      db-sharding-algorithm: OrderDbShardingByUserAlgorithm
      #表分片列名称，多个列以逗号分隔
      table-sharding-columns: order_no,user_id
      #表分片策略类
      table-sharding-algorithm: OrderTableShardingByUserAlgorithm
    ###配置#order_item_detail表分片规则
    table-rules[1]:
      #逻辑表名
      logic-table: order_item_detail_sharded_by_user_id_
      #库分片列名称，多个列以逗号分隔
      db-sharding-columns: order_no,user_id
      #库分片策略类
      db-sharding-algorithm: OrderDbShardingByUserAlgorithm
      #表分片列名称，多个列以逗号分隔
      table-sharding-columns: order_no,user_id
      #表分片策略类
      table-sharding-algorithm: OrderTableShardingByUserAlgorithm
    #是否显示shardingsphere sql执行日志
    sql-show: true
    #每个逻辑库中表的数量
    table-num: 8
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
pagehelper:
  # dialect: ①
  # 分页插件会自动检测当前的数据库链接，自动选择合适的分页方式（可以不设置）
  #helper-dialect: mysql
  # 上面数据库设置后，下面的设置为true不会改变上面的结果（默认为true）
  auto-dialect: true
  page-size-zero: false # ②
  reasonable: true # ③
  # 默认值为 false，该参数对使用 RowBounds 作为分页参数时有效。（一般用不着）
  offset-as-page-num: false
  # 默认值为 false，RowBounds是否进行count查询（一般用不着）
  row-bounds-with-count: false
  #params: ④
  #support-methods-arguments: 和params配合使用，具体可以看下面的讲解
  ############ 默认值为 false。设置为 true 时，允许在运行时根据多数据源自动识别对应方言的分页（主要设置该值）
  auto-runtime-dialect: true # ⑤
  # 与auto-runtime-dialect配合使用
  close-conn: true
  # 用于控制默认不带 count 查询的方法中，是否执行 count 查询，这里设置为true后，total会为-1
  default-count: false
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

  
  
