/**  
* <p>Title: DatabaseShardingAlgorithm.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月9日  
* @version 1.0  
*/  
package cn.flood.config.sharding;

import java.util.Collection;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;


/**  
* <p>Title: DatabaseShardingAlgorithm</p>  
* <p>Description: 分库算法</p>  
* @author mmdai  
* @date 2019年7月9日  
*/
public class DatabaseShardingAlgorithm implements PreciseShardingAlgorithm<Integer> {
    
    @Override
    public String doSharding(final Collection<String> availableTargetNames, final PreciseShardingValue<Integer> shardingValue) {
        int size = availableTargetNames.size();
        for (String each : availableTargetNames) {
            if (each.endsWith(shardingValue.getValue() % size + "")) {
                return each;
            }
        }
        throw new UnsupportedOperationException();
    }

}
