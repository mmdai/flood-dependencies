package cn.flood.mybatis.plus.plugins.tenant;

import cn.flood.Func;

/**
 * 相等条件 Factory
 *
 * @Author iloveoverfly
 * @LocalDateTime 2020/6/11 14:56
 **/
public class DefaultConditionFactory implements ConditionFactory {

    private static final String EQUAL_CONDITION = "=";
    private static final String IN_CONDITION = " in ";
    private final DBColumnValueFactory columnValueFactory;

    public DefaultConditionFactory() {
        this.columnValueFactory = new DefaultDBColumnValueFactory();
    }


    @Override
    public String buildCondition(MultiTenancyQuery multiTenancyQuery) {

        StringBuilder stringBuilder = new StringBuilder();
        String columnValue = this.columnValueFactory.buildColumnValue(multiTenancyQuery.getMultiTenancyQueryValue());
        // 根据条件类型设置查询条件
        switch (multiTenancyQuery.getConditionType()) {
            case IN:
                stringBuilder
                        .append(multiTenancyQuery.getMultiTenancyQueryColumn())
                        .append(IN_CONDITION)
                        .append("(")
                        .append(columnValue)
                        .append(")");
                break;
            case EQUAL:
            default:
                stringBuilder
                        .append(multiTenancyQuery.getMultiTenancyQueryColumn())
                        .append(EQUAL_CONDITION)
                        .append(columnValue);
                break;
        }
        // 设置数据库表别名
        String preTableName;
        if (Func.isNotBlank(preTableName = multiTenancyQuery.getPreTableName())) {
            stringBuilder.insert(0, ".")
                    .insert(0, preTableName);
        }
        return stringBuilder.toString();
    }
}
