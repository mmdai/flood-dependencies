package cn.flood.sharding.algorithm;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * C端维度分库路由算法类
 *
 * @author
 */
public class OrderDbShardingByUserAlgorithm implements ComplexKeysShardingAlgorithm<Comparable<?>> {

    @Override
    public Collection<String> doSharding(Collection<String> dbs, ComplexKeysShardingValue<Comparable<?>> shardingValue) {
        Collection<Comparable<?>> orderNos = shardingValue.getColumnNameAndShardingValuesMap().get("order_no");
        Collection<Comparable<?>> userIds = shardingValue.getColumnNameAndShardingValuesMap().get("user_id");
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
            String userIdSuffix = StringUtils.substring(shardingValue, shardingValue.length() - 3);
            //使用userId后三位进行路由
            int dbSuffix = userIdSuffix.hashCode() % dbs.size();
            for(String db : dbs) {
                if(db.endsWith(String.valueOf(dbSuffix))) {
                    return db;
                }
            }
            return null;
    }

}