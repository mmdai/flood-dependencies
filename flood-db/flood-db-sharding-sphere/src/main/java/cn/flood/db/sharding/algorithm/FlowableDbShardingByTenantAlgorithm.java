package cn.flood.db.sharding.algorithm;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

/**
 * C端维度分库路由算法类
 *
 * @author mmdai
 * @version 1.0
 * @date 2022/8/4 10:04
 */
public class FlowableDbShardingByTenantAlgorithm implements
    ComplexKeysShardingAlgorithm<Comparable<?>> {

  @Override
  public Collection<String> doSharding(Collection<String> dbs,
      ComplexKeysShardingValue<Comparable<?>> shardingValue) {
    Collection<Comparable<?>> tenantIds = shardingValue.getColumnNameAndShardingValuesMap()
        .get("TENANT_ID_");
    Set<String> actualDbNames = null;
    if (CollectionUtils.isNotEmpty(tenantIds)) {
      actualDbNames = tenantIds.stream()
          .map(orderNo -> getActualDbName(String.valueOf(tenantIds), dbs))
          .collect(Collectors.toSet());
    }

    return actualDbNames;
  }

  public String getActualDbName(String shardingValue, Collection<String> dbs) {
    //使用TENANT_ID_进行路由
    int dbSuffix = shardingValue.hashCode() % dbs.size();
    for (String db : dbs) {
      if (db.endsWith(String.valueOf(dbSuffix))) {
        return db;
      }
    }
    return dbs.iterator().next();
  }
}
