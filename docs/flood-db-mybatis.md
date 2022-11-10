# flood-MyBatis
MyBatis 增强工具包，实现了便捷的单表 CRUD，各种自定义条件查询，单表查询甚至可以做到无 XML，像使用 PHP 操作数据库一样简单。

--------------------------------------------------
##### 1. 添加maven依赖：

```
<dependency>
  <groupId>cn.flood</groupId>
  <artifactId>flood-db-mybatis</artifactId>
  <version>${flood.version}</version>
</dependency>
```

##### 2. 使用说明：
```xml
<dependency>
  <groupId>cn.flood</groupId>
  <artifactId>flood-spring-cloud-starter-mysql</artifactId>
  <version>${flood.version}</version>
</dependency>
```
> Note: SpringBoot 项目不需要再引入 flood-db-mybatis 了，只导入 flood-spring-cloud-starter-mysql 一个构件就 OK 了。

# 小试牛刀
假设我们已存在一张 User 表，且已有对应的实体类 User，实现 User 表的 CRUD 操作我们只需创建一个对应的 Mapper 接口就行了。

```java
public interface UserMapper extends BaseMapper<User> { }
```

#### 基本 CRUD 操作 

```java
// 影响行数
int affactRows = 0;
// 初始化 User 实体对象, 如果是非自增ID需要初始化ID
// 系统有内置的分布式 ID 生成工具
User user = new User(userMapper.getNewId());

// 插入 User (如果是自增ID的话，插入成功会自动回写ID到实体类)
user.setName("Rock");
affactRows = userMapper.add(user);

// 更新 User
user.setAge(18);
affactRows = userMapper.update(user);
// 你也可以这样
affactRows = userMapper.updateAll(user);
/*
   注意：update() 方法只更新 User 实体的非 null 字段，是增量更新
   而 updateAll() 会更新所有字段，包括值为 null 的字段
   
   应用需求：比如你知道用户ID，只想更新 Name 字段，常规方法是下面这样的 
 */
String userId = "abc";
User user = userMapper.get(userId);
user.setName("New name");
userMapper.updateAll(user);
// 或则你也可以这样实现
User user = new User(userId);
user.setName("New name");
userMapper.update(user); // 这样可以少一次查询
// 所以如果你就是要把某个字段更新成 null, 请使用 updateAll()

// 查询 User
User userItem = userMapper.get(user.getId());
// 查询ID > 100 所有用户列表
List<User> userList = userMapper.searchByConditions(
        new Conditions.gt("id", 100)
);
// 如果只想取列表中的一条
User u = userMapper.getByCondition(new Conditions.gt("id", 100));

// 删除 User
affactRows = userMapper.delete(user.getId());
// 删除 id > 100 and id < 200 的所有用户
Conditions condi = new Conditions();
condi.gt("id", 100).lt("id", 200);
affactRows = UserMapper.deleteByConditions(condi);
```

#### 分页查询（所有分页均为物理分页）

```java
// 查询所有记录并分页
Page<User> page = new Page<>();
page.setPageSize(10);
page = userMapper.search(page);

for (User user : page.getResults()) {
	logger.info("结果：{}",user);
}

// 按照条件查询分页
Conditions condi = new Conditions();
condi.eq("name", "Rock");
Page<User> page = new Page<>();
page.setPageSize(10);
page = userMapper.searchByConditions(page, condi);

for (User user : page.getResults()) {
	logger.info("结果：{}",user);
}

```

#### 条件组合

Conditions 查询异常强大，几乎可以满足你单表查询的所有需求。

先来个简单的：

```java
Conditions conditions = new Conditions();
conditions.eq("name", "Rock")
    .ge("age", 18)
    .lt("age", 30)
    .ne("address", "Addres_A");
// output: name='Rock' AND age > 18 AND age < 30 AND address != 'Addres_A'
```

再来个稍微复杂一点的：

```java
Conditions conditions = new Conditions();
conditions.add(Restrictions.and(Restrictions.eq("age",18),Restrictions.eq("name","zhangsan")));
conditions.add(Restrictions.or(Restrictions.eq("count",18),Restrictions.eq("count",29)));
// output: ((age=18 AND name='zhangsan') AND (count=18 OR count=29))
```

@Test
public void testSimpleExpression()
{
    Conditions conditions = new Conditions();
    conditions.add(Restrictions.eq("count", 2));
    conditions.add(Restrictions.eq("name", "名字"));
    conditions.add(Restrictions.ge("age", 18));
    conditions.add(Restrictions.lt("age", 30));
    conditions.add(Restrictions.ge("num",8));
    conditions.add(Restrictions.le("num", 130));
    conditions.add(Restrictions.ne("address", "这个地址"));
    System.out.println(conditions.toSqlString());

    Conditions conditions1 = new Conditions();
    conditions1.eq("name", "名字")
            .ge("age", 18)
            .lt("age", 30)
            .ne("address", "这个地址");
    System.out.println(conditions1.toSqlString());
}

@Test
public void testBetweenExpression()
{
    Conditions conditions = new Conditions();
    conditions.add(Restrictions.eq("count", 2));
    conditions.add(Restrictions.between("age",20,30));
    System.out.println(conditions.toSqlString());
}

@Test
public void testConjunction()
{
    Conditions conditions = new Conditions();
    conditions.add(Restrictions.and(Restrictions.eq("age",18),Restrictions.eq("name","李四")));
    conditions.add(Restrictions.or(Restrictions.eq("count",18),Restrictions.eq("count",29)));
    System.out.println(conditions.toSqlString());
}

@Test
public void testOneConjunction()
{
    Conditions conditions = new Conditions();
    conditions.add(Restrictions.or(Restrictions.eq("count", 18), Restrictions.eq("count", 29)));
    System.out.println(conditions.toSqlString());
}

@Test
public void testSqlRestriction()
{
    Conditions conditions = new Conditions();

    conditions.add(Restrictions.conjunction(Restrictions.eq("xxx",1), Restrictions.eq("yyy", 2)));
    System.out.println(conditions.toSqlString());
}

上面的所有功能都是不需要创建 Mapper.xml 文档就可以轻松实现的，如果你需要增加联合查询功能，只需增加相应的 UserMapper.xml，在里面实现就好了。
完全兼容原生 MyBatis 的所有功能。

MathOptVo 用法

<update id="mathOpt" parameterType="MathOptVo">
        update user
        <set>
            <if test="opt == 'add'">
                ${field} = ${field} + #{offset} WHERE id = #{id}
            </if>
            <if test="opt == 'subtract'">
                ${field} = ${field} - #{offset} WHERE id = #{id} AND ${field} > #{offset}
            </if>
            <if test="opt == 'multiply'">
                ${field} = ${field} * #{offset} WHERE id = #{id} AND ${offset} != 0
            </if>
            <if test="opt == 'divide'">
                ${field} = ${field} / #{offset} WHERE id = #{id} AND ${offset} != 0
            </if>
        </set>
    </update>