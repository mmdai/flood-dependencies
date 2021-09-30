package cn.flood.mybatis.plus.plugins.tenant;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author iloveoverfly
 * @LocalDateTime 2020/6/11 15:05
 **/
public class DefaultDBColumnValueFactory implements DBColumnValueFactory {
    @Override
    public String buildColumnValue(Object queryValue) {

        if (Objects.isNull(queryValue)) {
            return null;
        }
        // 查询值数组转换为，分隔符的值
        if (queryValue instanceof List) {
            List<Object> queryValueList = (List<Object>) queryValue;
            List<String> queryStrValueList = queryValueList.stream().map(this::doBuildColumnValue).collect(Collectors.toList());
            return queryStrValueList.stream().collect(Collectors.joining(","));
        } else {
            return doBuildColumnValue(queryValue);
        }
    }

    private String doBuildColumnValue(Object queryValue) {

        String valueStr;
        if (queryValue instanceof String) {
            valueStr = "'" + queryValue + "'";
        } else {
            valueStr = String.valueOf(queryValue);
        }
        return valueStr;
    }
}
