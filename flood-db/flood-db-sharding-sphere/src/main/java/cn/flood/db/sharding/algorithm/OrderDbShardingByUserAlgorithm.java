package cn.flood.db.sharding.algorithm;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

/**
 * C端维度分库路由算法类
 *
 * @author
 */
public class OrderDbShardingByUserAlgorithm implements ComplexKeysShardingAlgorithm<Comparable<?>> {

  /**
   * order_no 订单号
   */
  private static final String ORDER_NO_COLUMN = "order_no";
  /**
   * user_id 用户id
   */
  private static final String USER_ID_COLUMN = "user_id";
  /**
   * 后三位
   */
  private static final int THREE_NUM = 3;

  @Override
  public Collection<String> doSharding(Collection<String> dbs,
      ComplexKeysShardingValue<Comparable<?>> shardingValue) {
    Collection<Comparable<?>> orderNos = shardingValue.getColumnNameAndShardingValuesMap()
        .get(ORDER_NO_COLUMN);
    Collection<Comparable<?>> userIds = shardingValue.getColumnNameAndShardingValuesMap()
        .get(USER_ID_COLUMN);
    Set<String> actualDbNames = null;
    if (CollectionUtils.isNotEmpty(orderNos)) {
      actualDbNames = orderNos.stream()
          .map(orderNo -> getActualDbName(String.valueOf(orderNo), dbs))
          .collect(Collectors.toSet());
    } else if (CollectionUtils.isNotEmpty(userIds)) {
      actualDbNames = userIds.stream()
          .map(userId -> getActualDbName(String.valueOf(userId), dbs))
          .collect(Collectors.toSet());
    }

    return actualDbNames;
  }

  public String getActualDbName(String shardingValue, Collection<String> dbs) {
    //获取userId后三位
    String userIdSuffix = StringUtils.substring(shardingValue, shardingValue.length() - THREE_NUM);
    //使用userId后三位进行路由
    int dbSuffix = userIdSuffix.hashCode() % dbs.size();
    for (String db : dbs) {
      if (db.endsWith(String.valueOf(dbSuffix))) {
        return db;
      }
    }
    return null;
  }

}