/**  
* <p>Title: ShardingMasterSlaveProperties.java</p>
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月9日  
* @version 1.0  
*/  
package cn.flood.sharding.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.Data;

/**  
* <p>Title: ShardingMasterSlaveProperties</p>
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月9日  
*/

@Data
@ConfigurationProperties(prefix = "sharding.jdbc")
public class ShardingMasterSlaveProperties {

	private Map<String, DruidDataSource> dataSources = new HashMap<>();
	 
    private MasterSlaveRuleProperties masterSlaveRule;
    
}
