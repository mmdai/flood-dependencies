package com.github.pagehelper;

import cn.flood.db.mybatis.plus.MybatisRowBounds;
import com.github.pagehelper.cache.Cache;
import com.github.pagehelper.cache.CacheFactory;
import com.github.pagehelper.util.ExecutorUtil;
import com.github.pagehelper.util.MSUtils;
import com.github.pagehelper.util.StringUtil;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * Mybatis - 通用分页拦截器
 * <p>
 * GitHub: https://github.com/pagehelper/Mybatis-PageHelper
 * <p>
 * Gitee : https://gitee.com/free/Mybatis_PageHelper
 *
 * @author liuzh/abel533/isea533
 * @version 5.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Intercepts(
    {
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class,
            Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class,
            Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
    }
)
public class PageInterceptor implements Interceptor {

  protected Cache<String, MappedStatement> msCountMap = null;
  private volatile Dialect dialect;
  private String countSuffix = "_COUNT";
  private String default_dialect_class = "com.github.pagehelper.PageHelper";

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    try {
      Object[] args = invocation.getArgs();
      MappedStatement ms = (MappedStatement) args[0];
      Object parameter = args[1];
      RowBounds rowBounds = (RowBounds) args[2];
      //自己mysql分页---不用pagehelper
      if (rowBounds instanceof MybatisRowBounds) {
        return invocation.proceed();
      }
      ResultHandler resultHandler = (ResultHandler) args[3];
      Executor executor = (Executor) invocation.getTarget();
      CacheKey cacheKey;
      BoundSql boundSql;

      //由于逻辑关系，只会进入一次
      if (args.length == 4) {
        //4 个参数时
        boundSql = ms.getBoundSql(parameter);
        cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
      } else {
        //6 个参数时
        cacheKey = (CacheKey) args[4];
        boundSql = (BoundSql) args[5];
      }
      checkDialectExists();
      //对 boundSql 的拦截处理
      if (dialect instanceof BoundSqlInterceptor.Chain) {
        boundSql = ((BoundSqlInterceptor.Chain) dialect)
            .doBoundSql(BoundSqlInterceptor.Type.ORIGINAL, boundSql, cacheKey);
      }
      List resultList;
      //调用方法判断是否需要进行分页，如果不需要，直接返回结果
      if (!dialect.skip(ms, parameter, rowBounds)) {
        //判断是否需要进行 count 查询
        if (dialect.beforeCount(ms, parameter, rowBounds)) {
          //查询总数
          Long count = count(executor, ms, parameter, rowBounds, null, boundSql);
          //处理查询总数，返回 true 时继续分页查询，false 时直接返回
          if (!dialect.afterCount(count, parameter, rowBounds)) {
            //当查询总数为 0 时，直接返回空的结果
            return dialect.afterPage(new ArrayList(), parameter, rowBounds);
          }
        }
        resultList = ExecutorUtil.pageQuery(dialect, executor,
            ms, parameter, rowBounds, resultHandler, boundSql, cacheKey);
      } else {
        //rowBounds用参数值，不使用分页插件处理时，仍然支持默认的内存分页
        resultList = executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
      }
      return dialect.afterPage(resultList, parameter, rowBounds);
    } finally {
      if (dialect != null) {
        dialect.afterAll();
      }
    }
  }


  /**
   * Spring bean 方式配置时，如果没有配置属性就不会执行下面的 setProperties 方法，就不会初始化
   * <p>
   * 因此这里会出现 null 的情况 fixed #26
   */
  private void checkDialectExists() {
    if (dialect == null) {
      synchronized (default_dialect_class) {
        if (dialect == null) {
          setProperties(new Properties());
        }
      }
    }
  }

  private Long count(Executor executor, MappedStatement ms, Object parameter,
      RowBounds rowBounds, ResultHandler resultHandler,
      BoundSql boundSql) throws SQLException {
    String countMsId = ms.getId() + countSuffix;
    Long count;
    //先判断是否存在手写的 count 查询
    MappedStatement countMs = ExecutorUtil
        .getExistedMappedStatement(ms.getConfiguration(), countMsId);
    if (countMs != null) {
      count = ExecutorUtil
          .executeManualCount(executor, countMs, parameter, boundSql, resultHandler);
    } else {
      if (msCountMap != null) {
        countMs = msCountMap.get(countMsId);
      }
      //自动创建
      if (countMs == null) {
        //根据当前的 ms 创建一个返回值为 Long 类型的 ms
        countMs = MSUtils.newCountMappedStatement(ms, countMsId);
        if (msCountMap != null) {
          msCountMap.put(countMsId, countMs);
        }
      }
      count = ExecutorUtil
          .executeAutoCount(this.dialect, executor, countMs, parameter, boundSql, rowBounds,
              resultHandler);
    }
    return count;
  }

  /**
   * 拦截器对应的封装原始对象的方法
   */
  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  /**
   * 设置注册拦截器时设定的属性
   */
  @Override
  public void setProperties(Properties properties) {
    //缓存 count ms
    msCountMap = CacheFactory.createCache(properties.getProperty("msCountCache"), "ms", properties);
    String dialectClass = properties.getProperty("dialect");
    if (StringUtil.isEmpty(dialectClass)) {
      dialectClass = default_dialect_class;
    }
    try {
      Class<?> aClass = Class.forName(dialectClass);
      dialect = (Dialect) aClass.newInstance();
    } catch (Exception e) {
      throw new PageException(e);
    }
    dialect.setProperties(properties);

    String countSuffix = properties.getProperty("countSuffix");
    if (StringUtil.isNotEmpty(countSuffix)) {
      this.countSuffix = countSuffix;
    }
  }

}
