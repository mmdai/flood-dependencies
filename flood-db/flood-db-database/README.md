**版本下载**
---
    <dependency>
    	<groupId>cn.flood</groupId>
    	<artifactId>flood-db-database</artifactId>
    	<version>2.0.0</version>
    </dependency>


**配置介绍**
---



**多数据源范例**
---
application.yml
spring:
  datasource:
    # JDBC 配置(驱动类自动从url的mysql识别,数据源类型自动识别)
    #多数据源
    sourceConfig[0]:
      primary: true
      # JDBC 配置(驱动类自动从url的mysql识别,数据源类型自动识别)
      url: jdbc:mysql://127.0.0.1:3306/credit_mgr?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
      username: 'root'
      password: '123456'
      driver-class-name:  com.mysql.cj.jdbc.Driver
    sourceConfig[1]:
      primary: false
      # JDBC 配置(驱动类自动从url的mysql识别,数据源类型自动识别)
      url: jdbc:mysql://127.0.0.1:3306/credit_mgr?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
      username: 'root'
      password: '123456'
      driver-class-name:  com.mysql.cj.jdbc.Driver
      #支持个数据源自定义数据库连接池
      initial-size: 10
      min-idle: 20
      max-active: 120
      #获取连接等待超时时间
      max-wait: 30000
      validation-query: select 1
    druid:
      #配置初始化大小/最小/最大
      initial-size: 10
      min-idle: 20
      max-active: 120
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
      connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
      # 合并多个DruidDataSource的监控数据
      use-global-data-source-stat: true
pagehelper:
  # dialect: ①
  # 分页插件会自动检测当前的数据库链接，自动选择合适的分页方式（可以不设置）
  helper-dialect: mysql
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
  auto-runtime-dialect: false # ⑤
  # 与auto-runtime-dialect配合使用
  close-conn: true
  # 用于控制默认不带 count 查询的方法中，是否执行 count 查询，这里设置为true后，total会为-1
  default-count: false
  #dialect-alias: ⑥

** 支持kylin配置数据库驱动(kylin 走的是http协议)
加入 配置文件：
<dependency>
    <groupId>org.apache.kylin</groupId>
    <artifactId>kylin-jdbc</artifactId>
    <version>${kylin.version}</version>
</dependency>
配置如下
application.yml
spring:
  datasource:
    #数据源single,multi
    isSingle: false
    # JDBC 配置(驱动类自动从url的mysql识别,数据源类型自动识别)
    #多数据源
    sourceConfig[0]:
      url: jdbc:kylin://10.67.31.137:7070/yum_report
      username: ADMIN
      password: KYLIN
      driver-class-name: org.apache.kylin.jdbc.Driver
      thread-pool: false
      
常用数据库 validationQuery 检查语句

数据库	        validationQuery
Oracle	        select 1 from dual
MySQL	        select 1
DB2	            select 1 from sysibm.sysdummy1
MS SQL Server	select 1
HSQLDB	        select 1 from INFORMATION_SCHEMA.SYSTEM_USERS
PostgreSQL	    select version()
Derby	        select 1
H2	            select 1


Mapper或者Service方法加入(对应多数据源下标)
@DataSourceAnnotation(name = "DB0")

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

  
  
