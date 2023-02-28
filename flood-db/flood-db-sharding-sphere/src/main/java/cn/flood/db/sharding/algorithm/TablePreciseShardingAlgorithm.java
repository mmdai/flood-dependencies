/**
 * <p>Title: TablePreciseShardingAlgorithm.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author mmdai
 * @date 2019年7月9日
 * @version 1.0
 */
package cn.flood.db.sharding.algorithm;

import java.util.Collection;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;


/**
 * <p>Title: TablePreciseShardingAlgorithm</p>  
 * <p>Description: 分表算法:==和IN的分表算法实现</p>  
 * @author mmdai
 * @date 2019年7月9日
 */
public class TablePreciseShardingAlgorithm implements PreciseShardingAlgorithm<Long> {

  @Override
  public String doSharding(final Collection<String> availableTargetNames,
      final PreciseShardingValue<Long> shardingValue) {
    int size = availableTargetNames.size();
    for (String each : availableTargetNames) {
      if (each.endsWith(shardingValue.getValue() % size + "")) {
        return each;
      }
    }
    throw new UnsupportedOperationException();
  }

}
