package cn.flood.db.mybatis.plus.plugins.tenant;

import cn.flood.base.core.Func;
import cn.flood.db.mybatis.plus.util.Reflections;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;



/**
 * 多租户查询过滤 Interceptor
 *
 * @Author iloveoverfly
 * @LocalDateTime 2020/6/10 16:57
 **/
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})})
public class MultiTenancyQueryInterceptor implements Interceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String WHERE_CONDITION = " where ";
    private static final String AND_CONDITION = " and ";
    private static final String FROM_CONDITION = "from";
    private static final String GROUP_CONDITION = "group";
    private static final String ORDER_CONDITION = "order";
    private static final String BY_CONDITION = "by";
    private static final String LIMIT_CONDITION = "limit";
    private static final char WHITE_SPACE = ' ';
    private static final int ORDER_LENGTH = 5;
    private static final int GROUP_LENGTH = 5;
    /**
     * 条件生成Factory
     */
    private final ConditionFactory conditionFactory;
    /**
     * 查询条件生成工厂缓存
     */
    private volatile Map<Class<? extends MultiTenancyQueryValueFactory>, MultiTenancyQueryValueFactory> multiTenancyQueryValueFactoryMap;
    /**
     * 多租户属性
     */
    @Getter
    @Setter
    private MultiTenancyProperties multiTenancyProperties;

    public MultiTenancyQueryInterceptor() {
        this.conditionFactory = new DefaultConditionFactory();
        this.multiTenancyProperties = new MultiTenancyProperties();
        this.multiTenancyQueryValueFactoryMap = new HashMap<>();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        // 获取多租户查询注解
        MultiTenancy multiTenancy = this.getMultiTenancyFromMappedStatementId(mappedStatement.getId());
        if (!this.isMatchMultiTenancy(multiTenancy)) {

            log.info("{} is not match multi tenancy query!", mappedStatement.getId());
            return invocation.proceed();
        }
        // 验证多租户查询数据库字段
        if (StringUtils.isBlank(this.multiTenancyProperties.getMultiTenancyQueryColumn())) {

            throw new IllegalArgumentException(" multi tenancy query column is empty!");
        }
        Object parameter = args[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String originSql = boundSql.getSql().toLowerCase();
        // 验证数据库表查询别名
        if (!this.matchPreTableName(originSql, multiTenancy.preTableName())) {

            log.info("pre table name {} is not matched sql {}!", multiTenancy.preTableName(), originSql);
            return invocation.proceed();
        }
        if (this.isNotContainTableQuery(originSql)) {

            log.info("is not contain table query sql {}", originSql);
            return invocation.proceed();
        }
        // 获取多租户查询条件
        Object queryObject;
        if (Objects.isNull(queryObject = this.getQueryObjectFromMultiTenancyQueryValueFactory(multiTenancy.multiTenancyQueryValueFactory()))) {

            log.info("multi tenancy query value is empty!");
            return invocation.proceed();
        }
        // 默认使用In 条件
        ConditionFactory.ConditionTypeEnum conditionTypeEnum = ConditionFactory.ConditionTypeEnum.IN;
        String conditionType;
        if (StringUtils.isNotBlank(conditionType = this.multiTenancyProperties.getConditionType())) {
            try {
                conditionTypeEnum = ConditionFactory.ConditionTypeEnum.valueOf(conditionType.toUpperCase());
            } catch (Exception e) {
                log.warn("invalid condition type {}!", conditionType);
            }
        }
        MultiTenancyQuery multiTenancyQuery = MultiTenancyQuery.builder()
                .multiTenancyQueryColumn(this.multiTenancyProperties.getMultiTenancyQueryColumn())
                .multiTenancyQueryValue(queryObject)
                .conditionType(conditionTypeEnum)
                .preTableName(multiTenancy.preTableName())
                .build();
        String multiTenancyQueryCondition = this.conditionFactory.buildCondition(multiTenancyQuery);
        String newSql = this.appendWhereCondition(originSql, multiTenancyQueryCondition);
        // 使用反射替换BoundSql的sql语句
        Reflections.setFieldValue(boundSql, "sql", newSql);
        // 需要设置生成的 BoundSql
        if (6 == args.length) {
            args[5] = boundSql;
        }
        // 把新的查询放到statement里
        MappedStatement newMs = copyFromMappedStatement(mappedStatement, parameterObject -> boundSql);
        args[0] = newMs;
        return invocation.proceed();
    }

    private boolean isNotContainTableQuery(String originSql) {
        return StringUtils.isNotBlank(originSql) && !originSql.toLowerCase().contains(FROM_CONDITION);
    }


    private Object getQueryObjectFromMultiTenancyQueryValueFactory(Class<? extends MultiTenancyQueryValueFactory> multiTenancyQueryValueFactoryClass) {

        if (Objects.isNull(multiTenancyQueryValueFactoryClass)) {
            return null;
        }
        MultiTenancyQueryValueFactory multiTenancyQueryValueFactory = this.multiTenancyQueryValueFactoryMap.get(multiTenancyQueryValueFactoryClass);
        if (Objects.nonNull(multiTenancyQueryValueFactory)) {
            return multiTenancyQueryValueFactory.buildMultiTenancyQueryValue();
        }
        synchronized (this.multiTenancyQueryValueFactoryMap) {

            try {
                multiTenancyQueryValueFactory = multiTenancyQueryValueFactoryClass.newInstance();
                multiTenancyQueryValueFactoryMap.putIfAbsent(multiTenancyQueryValueFactoryClass, multiTenancyQueryValueFactory);
                return multiTenancyQueryValueFactory.buildMultiTenancyQueryValue();
            } catch (Exception e) {
                return null;
            }
        }
    }


    /**
     * 从MappedStatement 的id属性获取多租户查询注解 MultiTenancy
     *
     * @param id MappedStatement 的id属性
     * @return MultiTenancy注解
     */
    private MultiTenancy getMultiTenancyFromMappedStatementId(String id) {

        MultiTenancy multiTenancy = null;
        int lastSplitPointIndex = id.lastIndexOf(".");
        String mapperClassName = id.substring(0, lastSplitPointIndex);
        String methodName = id.substring(lastSplitPointIndex + 1, id.length());
        Class mapperClass;
        try {
            mapperClass = Class.forName(mapperClassName);
            Method[] methods = mapperClass.getMethods();
            multiTenancy = this.getMultiTenancyFromMethod(methodName, methods);
            if (Objects.isNull(multiTenancy)) {
                multiTenancy = this.getMultiTenancyFromClass(mapperClass);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return multiTenancy;
    }

    private MultiTenancy getMultiTenancyFromClass(Class mapperClass) {

        Annotation[] annotations = mapperClass.getAnnotations();
        if (Func.isEmpty(annotations)) {
            return null;
        }
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getName().equals(MultiTenancy.class.getName())) {
                return (MultiTenancy) annotation;
            }
        }
        return null;
    }

    private MultiTenancy getMultiTenancyFromMethod(String methodName, Method[] methods) {

        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method.getAnnotation(MultiTenancy.class);
            }
        }
        return null;
    }

    private String appendWhereCondition(String originSql, String condition) {

        if (StringUtils.isBlank(originSql) || StringUtils.isBlank(condition)) {
            return originSql;
        }
        String[] sqlSplit = originSql.toLowerCase().split(WHERE_CONDITION.trim());
        // 没有查询条件
        if (this.noWhereCondition(sqlSplit)) {

            StringBuilder stringBuilder = new StringBuilder(originSql);
            String finalCondition = WHERE_CONDITION + condition + " ";
            if (this.containOrderBy(originSql) && this.containLimit(originSql)) {

                return stringBuilder.insert(this.getOrderByIndex(originSql), finalCondition).toString();
            } else if (this.containOrderBy(originSql)) {

                return stringBuilder.insert(this.getOrderByIndex(originSql), finalCondition).toString();
            } else if (this.containLimit(originSql)) {

                return stringBuilder.insert(originSql.indexOf(LIMIT_CONDITION), finalCondition).toString();
            } else if (this.containGroupBy(originSql)) {

                return stringBuilder.insert(this.getGroupByIndex(originSql), finalCondition).toString();
            } else {
                return originSql + WHERE_CONDITION + condition;
            }
        }
        // 包含查询条件，添加到第一个查询条件的位置
        else {
            String sqlBeforeWhere = sqlSplit[0];
            String sqlAfterWhere = sqlSplit[1];
            return sqlBeforeWhere + WHERE_CONDITION + condition + AND_CONDITION + sqlAfterWhere;
        }
    }

    private boolean containOrderBy(String originSql) {
        return this.containConditionAndBy(originSql, ORDER_CONDITION, ORDER_LENGTH);
    }

    private int getConditionAndByIndex(String originSql, String condition, int conditionLength) {

        if (StringUtils.containsIgnoreCase(originSql, condition)
                && StringUtils.containsIgnoreCase(originSql, BY_CONDITION)) {

            int groupIndex;
            String endOfGroupString = originSql;
            int groupCount = 0;
            while (-1 != (groupIndex = endOfGroupString.indexOf(condition))) {

                groupCount++;
                int endOfGroupIndex = groupIndex + conditionLength;
                if (StringUtils.isBlank(endOfGroupString = endOfGroupString.substring(endOfGroupIndex, endOfGroupString.length()))) {
                    break;
                }
                if (isFollowingByString(endOfGroupString)) {
                    return doGetConditionIndex(originSql, groupCount, condition, conditionLength);
                }
            }
        }
        return -1;
    }

    private int getGroupByIndex(String originSql) {
        return this.getConditionAndByIndex(originSql, GROUP_CONDITION, GROUP_LENGTH);
    }

    private int getOrderByIndex(String originSql) {
        return this.getConditionAndByIndex(originSql, ORDER_CONDITION, ORDER_LENGTH);
    }

    private int doGetConditionIndex(String originSql, int orderCount, String condtion, int conditionLength) {

        int index = 0;
        String endOfOderString = originSql;
        for (int i = 1; i <= orderCount; i++) {

            int oderIndex = endOfOderString.indexOf(condtion);
            index += oderIndex;
            if (i == orderCount) {
                return index;
            }
            int endOfOrderIndex = oderIndex + conditionLength;
            if (shouldAddOrderSelfLength(i)) {
                index += conditionLength;
            }
            endOfOderString = endOfOderString.substring(endOfOrderIndex, endOfOderString.length());
        }
        return index;
    }

    private boolean shouldAddOrderSelfLength(int count) {
        return 0 != count;
    }

    private boolean isFollowingByString(String endOfOrderSql) {

        char[] charArray = endOfOrderSql.toCharArray();
        int i = 0;
        char tempChar;
        boolean containsWhiteSpace = true;
        while (containsWhiteSpace) {

            int byBeginIndex = i;
            int byEndIndex = i + 2;
            tempChar = charArray[i];
            i++;
            if (WHITE_SPACE == tempChar) {
                continue;
            }
            containsWhiteSpace = false;
            String byStr = endOfOrderSql.substring(byBeginIndex, byEndIndex);
            if (byStr.equalsIgnoreCase(BY_CONDITION)) {
                return true;
            }
        }
        return false;
    }

    private boolean containConditionAndBy(String originSql, String condition, int conditionLength) {

        if (StringUtils.containsIgnoreCase(originSql, condition)
                && StringUtils.containsIgnoreCase(originSql, BY_CONDITION)) {

            int index;
            String endOfString = originSql;
            while (-1 != (index = endOfString.indexOf(condition))) {

                int endOfGroupIndex = index + conditionLength;
                if (StringUtils.isBlank(endOfString = endOfString.substring(endOfGroupIndex, endOfString.length()))) {
                    break;
                }
                if (isFollowingByString(endOfString)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containGroupBy(String originSql) {
        return this.containConditionAndBy(originSql, GROUP_CONDITION, GROUP_LENGTH);
    }

    private boolean containLimit(String originSql) {
        return StringUtils.containsIgnoreCase(originSql, LIMIT_CONDITION);
    }

    private boolean noWhereCondition(String[] sqlSplit) {
        return Func.isNotEmpty(sqlSplit) && 1 == sqlSplit.length;
    }

    private boolean matchPreTableName(String sql, String preTableName) {

        if (StringUtils.isBlank(preTableName)) {
            return true;
        } else {
            return StringUtils.containsIgnoreCase(sql, preTableName);
        }
    }

    private boolean isMatchMultiTenancy(MultiTenancy multiTenancy) {

        return Objects.nonNull(multiTenancy)
                && multiTenancy.isFiltered();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

        Object multiTenancyQueryColumn;
        if (Objects.nonNull(multiTenancyQueryColumn = properties.get(MultiTenancyProperties.MULTI_TENANCY_QUERY_COLUMN_PROPERTY))) {
            multiTenancyProperties.setMultiTenancyQueryColumn(multiTenancyQueryColumn.toString());
        }
        Object conditionType;
        if (Objects.nonNull(conditionType = properties.get(MultiTenancyProperties.CONDITION_TYPE_PROPERTY))) {
            multiTenancyProperties.setConditionType(conditionType.toString());
        }
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(ms.getKeyProperties()[0]);
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        if(ms.getLang()!=null){
            builder.lang(ms.getLang());
        }
        return builder.build();
    }

    public static void main(String[] args) {

        MultiTenancyQueryInterceptor multiTenancyQueryInterceptor = new MultiTenancyQueryInterceptor();
        String oderBySql = "select * from product ORDER BY sort DESC";
        String condition = "sc.platform_code ='sn'";
        boolean flag = multiTenancyQueryInterceptor.containOrderBy(oderBySql.toLowerCase());
        String finalSql = multiTenancyQueryInterceptor.appendWhereCondition(oderBySql.toLowerCase(), condition);
        System.out.println(flag);
        System.out.println(finalSql);

        oderBySql = "select * from product ORDER       BY sort DESC";
        flag = multiTenancyQueryInterceptor.containOrderBy(oderBySql.toLowerCase());
        finalSql = multiTenancyQueryInterceptor.appendWhereCondition(oderBySql.toLowerCase(), condition);
        System.out.println(flag);
        System.out.println(finalSql);

        oderBySql = "select * from product Group BY id";
        flag = multiTenancyQueryInterceptor.containOrderBy(oderBySql.toLowerCase());
        finalSql = multiTenancyQueryInterceptor.appendWhereCondition(oderBySql.toLowerCase(), condition);
        System.out.println(flag);
        System.out.println(finalSql);

        oderBySql = "select o.* from order o ORDER BY o.sort DESC";
        flag = multiTenancyQueryInterceptor.containOrderBy(oderBySql.toLowerCase());
        finalSql = multiTenancyQueryInterceptor.appendWhereCondition(oderBySql.toLowerCase(), condition);
        System.out.println(flag);
        System.out.println(finalSql);

        oderBySql = " SELECT sc.platform_code, sc.shop_code,sc.stock_code,sc.operate_type, sc.change_amount,sc.order_num,sc.surplus_amount,sc.reason,sc.remarks,sc.ext, sc.updated_by,sc.update_time, st.spu_code, st.sku_code " +
                "FROM  stock_change_log sc " +
                "LEFT JOIN stock st ON sc.stock_code = st.stock_code " +
                "order by sc.update_time DESC";
        flag = multiTenancyQueryInterceptor.containOrderBy(oderBySql.toLowerCase());
        finalSql = multiTenancyQueryInterceptor.appendWhereCondition(oderBySql.toLowerCase(), condition);
        System.out.println(flag);
        System.out.println(finalSql);

        oderBySql = "select sc.*,sc.updated_by from stock_change_log sc left join stock st on sc.stock_code=st.stock_code order by sc.update_time desc";
        flag = multiTenancyQueryInterceptor.containOrderBy(oderBySql.toLowerCase());
        finalSql = multiTenancyQueryInterceptor.appendWhereCondition(oderBySql.toLowerCase(), condition);
        System.out.println(flag);
        System.out.println(finalSql);

        oderBySql = "select * from oder";
        flag = multiTenancyQueryInterceptor.containOrderBy(oderBySql.toLowerCase());
        System.out.println(flag);
        String oderByAndLimitSql = "select * from product ORDER BY sort DESC Limit 0,2";
        flag = multiTenancyQueryInterceptor.containOrderBy(oderByAndLimitSql.toLowerCase());
        finalSql = multiTenancyQueryInterceptor.appendWhereCondition(oderBySql.toLowerCase(), condition);
        System.out.println(flag);
        System.out.println(finalSql);

        String limitSql = "select * from product Limit 0,2";
        flag = multiTenancyQueryInterceptor.containLimit(limitSql.toLowerCase());
        System.out.println(flag);

    }
}
