package cn.flood.db.mybatis.plus;

import cn.flood.db.mybatis.plus.util.Snowflake;
import java.lang.reflect.Method;
import java.util.Map;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.SqlSession;

/**
 * @author mmdai
 */
@SuppressWarnings("unchecked")
public class MybatisMapperProxy<T> extends MapperProxy<T> {

  private final SqlSession sqlSession;
  private final Class<T> mapperInterface;
  private final Map<Method, MapperMethod> methodCache;

  public MybatisMapperProxy(SqlSession sqlSession, Class mapperInterface, Map methodCache) {
    super(sqlSession, mapperInterface, methodCache);
    this.sqlSession = sqlSession;
    this.mapperInterface = mapperInterface;
    this.methodCache = methodCache;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (Object.class.equals(method.getDeclaringClass())) {
      try {
        return method.invoke(this, args);
      } catch (Throwable t) {
        throw ExceptionUtil.unwrapThrowable(t);
      }
    }
    if ("getNewId".equals(method.getName())) {
      return Snowflake.getInstance().nextId();
    } else if ("getNewIdStr".equals(method.getName())) {
      return Snowflake.getInstance().nextIdStr();
    } else if ("getNewIdHex".equals(method.getName())) {
      return Snowflake.getInstance().nextIdHex();
    }
    final MapperMethod mapperMethod = cachedMapperMethod(method);
    return mapperMethod.execute(sqlSession, args);
  }

  private MapperMethod cachedMapperMethod(Method method) {
    MapperMethod mapperMethod = methodCache.get(method);
    if (mapperMethod == null) {
      mapperMethod = new MybatisMapperMethod(mapperInterface, method,
          sqlSession.getConfiguration());
      methodCache.put(method, mapperMethod);
    }
    return mapperMethod;
  }

}
