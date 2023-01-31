# flood-db-redis-lock redis锁

##### 1. 添加maven依赖：
---
    <dependency>
    	<groupId>cn.flood</groupId>
    	<artifactId>flood-mongodb</artifactId>
    	<version>${flood.version}</version>
    </dependency>


##### 2. 使用说明：
---
application.yml
   
#多数据源配置点
spring:
  data:
    mongodb:
      testMongoTemplate:
        host: localhost
        port: 27017
        database: backup
        showClass: false
        gridFsTemplateName: gridFsTemplate
      logsMongoTemplate:
        host: localhost
        port: 27017
        database: logs
        gridFsTemplateName: logsGridFsTemplate


**java范例**
---@EnableMongoPool
启动
@SpringBootApplication
@EnableMongoPool
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}

--DAO层：
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String>{
	
}

--entity实体：
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public class User {
	private String id;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}


--TestClient

import java.io.File;
import java.io.FileInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mongodb.BasicDBObject;

@RestController
public class MongoController {

	@Autowired
	@Qualifier("testMongoTemplate")
	private MongoTemplate testMongoTemplate;
	
	@Autowired
	@Qualifier("logsMongoTemplate")
	private MongoTemplate logsMongoTemplate;
	
	@Autowired
	@Qualifier("gridFsTemplate")
	private GridFsTemplate gridFsTemplate;
	
	@Autowired
	@Qualifier("logsGridFsTemplate")
	private GridFsTemplate logsGridFsTemplate;
	
	@GetMapping("/")
	public Object get() throws Exception {
		gridFsTemplate.store(new FileInputStream(new File("D:\\course\\mongodb\\po\\Advertisement.java")), new BasicDBObject("name", "yinjihuan"));
		testMongoTemplate.getCollectionNames().forEach(System.out::println);
		return "success";
	}
	
	@GetMapping("/slave")
	public Object getSlave() throws Exception  {
		logsGridFsTemplate.store(new FileInputStream(new File("D:\\course\\mongodb\\po\\Advertisement.java")), new BasicDBObject("name", "yinjihuan222"));
		logsMongoTemplate.getCollectionNames().forEach(System.out::println);
		return "success";
	}
	
	@GetMapping("/save")
	public Object save() {
		User u = new User();
		u.setName("yinjihuan");
		testMongoTemplate.save(u);
		return "success";
	}
}

  
  
