package cn.flood.mybatis.plus.plugins.tenant;


/**
 * @Author iloveoverfly
 * @LocalDateTime 2020/6/11 14:55
 **/
public interface ConditionFactory {

    /**
     * 生成 where条件
     *
     * @return
     */
    String buildCondition(MultiTenancyQuery multiTenancyQuery);

    enum ConditionTypeEnum {
        /**
         * 相等条件
         */
        EQUAL,
        /**
         * IN条件
         */
        IN;
    }
}
