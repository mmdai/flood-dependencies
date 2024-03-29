package cn.flood.db.mybatis.plus.plugins.page;

import cn.flood.base.core.lang.StringUtils;
import cn.flood.db.mybatis.plus.MybatisRowBounds;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 分页插件
 *
 * @author chenzhaoju
 * @author yangjian
 */
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})})
@SuppressWarnings("unchecked")
public class PaginationInterceptor implements Interceptor {

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    //被代理对象
    Object target = invocation.getTarget();
    //方法参数
    Object[] args = invocation.getArgs();
    if (target instanceof Executor) {
      MappedStatement mappedStatement = (MappedStatement) args[0];
      Object parameterObject = args[1];
      RowBounds rowBounds = (RowBounds) args[2];
      BoundSql boundSql = null;
      if (6 != args.length) {
        boundSql = mappedStatement.getBoundSql(parameterObject);
      } else {
        boundSql = (BoundSql) args[5];
      }

      if (null == rowBounds || RowBounds.DEFAULT == rowBounds) {
        // 不用处理分页
        return invocation.proceed();
      }

      if (rowBounds instanceof MybatisRowBounds) {
        Page page = ((MybatisRowBounds) rowBounds).getPage();
        String sql = boundSql.getSql();
        if (sql.contains(" LIMIT ")) {
          sql = StringUtils.subPre(sql, sql.indexOf(" LIMIT"));
        }
        Executor executor = (Executor) target;
        Connection connection = null;
        try {
          connection = executor.getTransaction().getConnection();

          StringBuilder sb = new StringBuilder();
          sb.append("select count(1) from ( ");
          sb.append(sql).append(" ) temp ");

          PreparedStatement preparedStatement = connection.prepareStatement(sb.toString());
          // 处理参数
          LanguageDriver lang = mappedStatement.getLang();
          ParameterHandler parameterHandler = lang
              .createParameterHandler(mappedStatement, parameterObject, boundSql);
          parameterHandler.setParameters(preparedStatement);

          ResultSet resultSet = preparedStatement.executeQuery();
          long total = 0;
          if (resultSet.next()) {
            total = resultSet.getLong(1);
          }
          page.setTotalRecord(total);

          MetaObject boundSqlMetaObject = SystemMetaObject.forObject(boundSql);
          StringBuilder limitSql = buildLimitSql(page, sql);

          boundSqlMetaObject.setValue("sql", limitSql.toString());

          Object proceed = invocation.proceed();
          if (null != proceed && proceed instanceof List) {
            List result = (List) proceed;
            page.setResults(result);
            return proceed;
          }
          return proceed;
        } finally {
        }
      }

    }
    return invocation.proceed();
  }

  private StringBuilder buildLimitSql(Page page, String sql) {
    StringBuilder limit = new StringBuilder();
    limit.append(sql);
    limit.append(" LIMIT ").append(page.getStartRow()).append(" , ").append(page.getPageSize());
    return limit;
  }

  @Override
  public Object plugin(Object target) {
    if (target instanceof CachingExecutor) {
      MetaObject cachingExecutorMetaObject = SystemMetaObject.forObject(target);
      Object delegate = cachingExecutorMetaObject.getValue("delegate");
      Object wrap = Plugin.wrap(delegate, this);
      cachingExecutorMetaObject.setValue("delegate", wrap);
    }
    return Plugin.wrap(target, this);
  }

  @Override
  public void setProperties(Properties properties) {
  }


}
