package cn.flood.db.mongodb;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
/**
 * 
* <p>Title: MongoPoolAutoConfiguration</p>  
* <p>Description: Mongodb数据库连接池信息自动配置</p>  
* @author mmdai  
* @date 2020年8月23日
 */
@AutoConfigureBefore({MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@SuppressWarnings("unchecked")
public class MongoPoolAutoConfiguration {
	
	@Bean
	public MongoPoolInit mongoPoolInit() {
		return new MongoPoolInit();
	}

}
