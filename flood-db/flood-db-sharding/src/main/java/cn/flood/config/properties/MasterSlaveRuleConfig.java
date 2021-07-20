/**  
* <p>Title: MasterSlaveRuleConfig.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2020年3月2日  
* @version 1.0  
*/  
package cn.flood.config.properties;

import java.util.List;


import lombok.Data;
import org.apache.shardingsphere.underlying.common.config.RuleConfiguration;

/**  
* <p>Title: MasterSlaveRuleConfig</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2020年3月2日  
*/
@Data
public class MasterSlaveRuleConfig implements RuleConfiguration {
    
    private String name;
    
    private String masterDataSourceName;
    
    private List<String> slaveDataSourceNames;
    
    private String loadBalanceAlgorithmType;
    

}
