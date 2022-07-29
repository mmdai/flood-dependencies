package cn.flood.sharding.algorithm;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * C端维度分表路由算法类
 *
 * @author
 */
public class OrderTableShardingByUserAlgorithm implements ComplexKeysShardingAlgorithm<Comparable<?>> {

    @Override
    public Collection<String> doSharding(Collection<String> tables, ComplexKeysShardingValue<Comparable<?>> shardingValue) {
        Collection<Comparable<?>> orderNos = shardingValue.getColumnNameAndShardingValuesMap().get("order_no");
        Collection<Comparable<?>> userIds = shardingValue.getColumnNameAndShardingValuesMap().get("user_id");
        Set<String> actualTableNames = null;
        if (CollectionUtils.isNotEmpty(orderNos)) {
            actualTableNames = orderNos.stream()
                    .map(orderNo -> getActualTableName(String.valueOf(orderNo), tables))
                    .collect(Collectors.toSet());
        } else if (CollectionUtils.isNotEmpty(userIds)) {
            actualTableNames = userIds.stream()
                    .map(userId -> getActualTableName(String.valueOf(userId), tables))
                    .collect(Collectors.toSet());
        }

        return actualTableNames;
    }

        public String getActualTableName(String shardingValue, Collection<String> tables) {
            //获取userId后三位
            String userIdSuffix = StringUtils.substring(shardingValue, shardingValue.length() - 3);
            //使用userId后三位进行路由
            int tableSuffix = userIdSuffix.hashCode() / tables.size() % tables.size();
            for(String table : tables) {
                if(table.endsWith(String.valueOf(tableSuffix))) {
                    return table;
                }
            }
            return null;
    }

}