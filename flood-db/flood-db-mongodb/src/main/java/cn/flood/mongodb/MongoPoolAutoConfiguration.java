package cn.flood.mongodb;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
/**
 * 
* <p>Title: MongoPoolAutoConfiguration</p>  
* <p>Description: Mongodb数据库连接池信息自动配置</p>  
* @author mmdai  
* @date 2020年8月23日
 */
@SuppressWarnings("unchecked")
@Configuration
@Component
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class MongoPoolAutoConfiguration {
	
	@Bean
	public MongoPoolInit mongoPoolInit() {
		return new MongoPoolInit();
	}

}
