# flood-MyBatis
MyBatis 增强工具包，实现了便捷的单表 CRUD，各种自定义条件查询，单表查询甚至可以做到无 XML，像使用 PHP 操作数据库一样简单。

--------------------------------------------------
##### 1. 添加maven依赖：

```
<dependency>
  <groupId>cn.flood</groupId>
  <artifactId>flood-db-mybatis-plus</artifactId>
  <version>${flood.version}</version>
</dependency>
```

##### 2. 使用说明：
配置
# MyBatis Plus 的配置项
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true # 虽然默认为 true ，但是还是显示去指定下。
  global-config:
    db-config:
      id-type: NONE # “智能”模式，基于 IdTypeEnvironmentPostProcessor + 数据源的类型，自动适配成 AUTO、INPUT 模式。
      #      id-type: AUTO # 自增 ID，适合 MySQL 等直接自增的数据库
      #      id-type: INPUT # 用户输入 ID，适合 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库
      #      id-type: ASSIGN_ID # 分配 ID，默认使用雪花算法。注意，Oracle、PostgreSQL、Kingbase、DB2、H2 数据库时，需要去除实体类上的 @KeySequence 注解
      logic-delete-value: 1 # 逻辑已删除值(默认为 1)
      logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
    banner: false # 关闭控制台的 Banner 打印
  type-aliases-package: ${flood.info.base-package}.dal.dataobject
  encryptor:
    password: XDV71a+xqStEA3WH # 加解密的秘钥，可使用 https://www.imaegoo.com/2020/aes-key-generator/ 网站生成
    
mybatis-plus-join:
    banner: false # 关闭控制台的 Banner 打印


########################################################

### database setting.

########################################################
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/credit_accesses?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true&rewriteBatchedStatements=true # MySQL Connector/J 8.X 连接的示例
    username: root
    password: 123456
    druid: # Druid 【监控】相关的全局配置
      initial-size: 5 # 初始连接数
      min-idle: 10 # 最小连接池数量
      max-active: 20 # 最大连接池数量
      max-wait: 600000 # 配置获取连接等待超时的时间，单位：毫秒
      time-between-eviction-runs-millis: 60000 # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位：毫秒
      min-evictable-idle-time-millis: 300000 # 配置一个连接在池中最小生存的时间，单位：毫秒
      max-evictable-idle-time-millis: 900000 # 配置一个连接在池中最大生存的时间，单位：毫秒
      validation-query: SELECT 1 FROM DUAL # 配置检测连接是否有效
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      web-stat-filter:
        enabled: true
      stat-view-servlet:
        enabled: true
        allow: # 设置白名单，不填则允许所有访问
        url-pattern: /druid/*
        login-username: # 控制台管理用户名和密码
        login-password:
      filter:
        stat:
          enabled: true
          log-slow-sql: true # 慢 SQL 记录
          slow-sql-millis: 100
          merge-sql: true
        wall:
          config:
            multi-statement-allow: true

--- #################### 数据库相关配置 ####################
spring:
  # 数据源配置项
  autoconfigure:
    exclude:
      - de.codecentric.boot.admin.client.config.SpringBootAdminClientAutoConfiguration # 禁用 Spring Boot Admin 的 Client 的自动配置
  datasource:
    druid: # Druid 【监控】相关的全局配置
      web-stat-filter:
        enabled: true
      stat-view-servlet:
        enabled: true
        allow: # 设置白名单，不填则允许所有访问
        url-pattern: /druid/*
        login-username: # 控制台管理用户名和密码
        login-password:
      filter:
        stat:
          enabled: true
          log-slow-sql: true # 慢 SQL 记录
          slow-sql-millis: 100
          merge-sql: true
        wall:
          config:
            multi-statement-allow: true
    dynamic: # 多数据源配置
      druid: # Druid 【连接池】相关的全局配置
        initial-size: 1 # 初始连接数
        min-idle: 1 # 最小连接池数量
        max-active: 20 # 最大连接池数量
        max-wait: 600000 # 配置获取连接等待超时的时间，单位：毫秒
        time-between-eviction-runs-millis: 60000 # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位：毫秒
        min-evictable-idle-time-millis: 300000 # 配置一个连接在池中最小生存的时间，单位：毫秒
        max-evictable-idle-time-millis: 900000 # 配置一个连接在池中最大生存的时间，单位：毫秒
        validation-query: SELECT 1 FROM DUAL # 配置检测连接是否有效
        test-while-idle: true
        test-on-borrow: false
        test-on-return: false
      primary: master
      datasource:
        master:
          url: jdbc:mysql://127.0.0.1:3306/ruoyi-vue-pro?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true # MySQL Connector/J 8.X 连接的示例
          #          url: jdbc:mysql://127.0.0.1:3306/ruoyi-vue-pro?useSSL=true&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai # MySQL Connector/J 5.X 连接的示例
          #          url: jdbc:postgresql://127.0.0.1:5432/ruoyi-vue-pro # PostgreSQL 连接的示例
          #          url: jdbc:oracle:thin:@127.0.0.1:1521:xe # Oracle 连接的示例
          #          url: jdbc:sqlserver://127.0.0.1:1433;DatabaseName=ruoyi-vue-pro # SQLServer 连接的示例
          #          url: jdbc:dm://10.211.55.4:5236?schema=RUOYI_VUE_PRO # DM 连接的示例
          username: root
          password: 123456
          #          username: sa # SQL Server 连接的示例
          #          password: JSm:g(*%lU4ZAkz06cd52KqT3)i1?H7W # SQL Server 连接的示例
          #          username: SYSDBA # DM 连接的示例
          #          password: SYSDBA # DM 连接的示例
        slave: # 模拟从库，可根据自己需要修改
          lazy: true # 开启懒加载，保证启动速度
          url: jdbc:mysql://127.0.0.1:3306/ruoyi-vue-pro?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true
          username: root
          password: 123456


# 小试牛刀
假设我们已存在一张 User 表，且已有对应的实体类 User，实现 User 表的 CRUD 操作我们只需创建一个对应的 Mapper 接口就行了。

```java

@Schema(description = "管理后台 - 用户分页 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPageReqVO extends PageDTO {

    @Schema(description = "用户账号，模糊匹配", example = "flood")
    private String username;

    @Schema(description = "手机号码，模糊匹配", example = "flood")
    private String mobile;

    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "1")
    private Integer status;

    @Schema(description = "创建时间", example = "[2022-07-01 00:00:00, 2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "部门编号，同时筛选子部门", example = "1024")
    private Long deptId;

}


@Mapper
public interface AdminUserMapper extends BaseMapperX<AdminUserDO> {

    default AdminUserDO selectByUsername(String username) {
        return selectOne(AdminUserDO::getUsername, username);
    }

    default AdminUserDO selectByEmail(String email) {
        return selectOne(AdminUserDO::getEmail, email);
    }

    default AdminUserDO selectByMobile(String mobile) {
        return selectOne(AdminUserDO::getMobile, mobile);
    }

    default PageResult<AdminUserDO> selectPage(UserPageReqVO reqVO, Collection<Long> deptIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserDO>()
                .likeIfPresent(AdminUserDO::getUsername, reqVO.getUsername())
                .likeIfPresent(AdminUserDO::getMobile, reqVO.getMobile())
                .eqIfPresent(AdminUserDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(AdminUserDO::getCreateTime, reqVO.getCreateTime())
                .inIfPresent(AdminUserDO::getDeptId, deptIds)
                .orderByDesc(AdminUserDO::getId));
    }

    default List<AdminUserDO> selectListByNickname(String nickname) {
        return selectList(new LambdaQueryWrapperX<AdminUserDO>().like(AdminUserDO::getNickname, nickname));
    }

    default List<AdminUserDO> selectListByStatus(Integer status) {
        return selectList(AdminUserDO::getStatus, status);
    }

    default List<AdminUserDO> selectListByDeptIds(Collection<Long> deptIds) {
        return selectList(AdminUserDO::getDeptId, deptIds);
    }

}


