package cn.flood.db.mybatis.plus.typehandler;

import cn.flood.base.core.enums.annotation.EnumHandler;
import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;


public class EnumKeyTypeHandler<E extends Enum> extends BaseTypeHandler<E> {

  private final Class<E> type;

  public EnumKeyTypeHandler(Class<E> type) {
    if (type == null) {
      throw new RuntimeException("EnumKeyTypeHandler...枚举对象为空");
    }
    this.type = type;
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType)
      throws SQLException {
    EnumHandler annotation = type.getAnnotation(EnumHandler.class);
    String keyField = annotation.value();
    try {
      Field field = type.getDeclaredField(keyField);
      field.setAccessible(true);
      ps.setObject(i, field.get(parameter));
    } catch (NoSuchFieldException e) {
      throw new RuntimeException("EnumKeyTypeHandler...未正确指定映射字段");
    } catch (IllegalAccessException e) {
      // field 权限已经更改
    }
  }

  @Override
  public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
    String code = rs.getString(columnName);
    return rs.wasNull() ? null : getEnumByKey(code);
  }

  @Override
  public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    String code = rs.getString(columnIndex);
    return rs.wasNull() ? null : getEnumByKey(code);
  }

  @Override
  public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    String code = cs.getString(columnIndex);
    return cs.wasNull() ? null : getEnumByKey(code);
  }

  public E getEnumByKey(String key) {
    EnumHandler annotation = type.getAnnotation(EnumHandler.class);
    String keyField = annotation.value();
    return getEnumByKey(type, key, keyField);
  }

  public E getEnumByKey(Class<E> clazz, Object key, String keyName) {
    if (key == null) {
      return null;
    }

    try {
      E[] enums = clazz.getEnumConstants();
      if (enums == null) {
        return null;
      }

      Field field = clazz.getDeclaredField(keyName);
      field.setAccessible(true);

      for (E e : enums) {
        Object tempKey = field.get(e);
        if (tempKey != null && tempKey.equals(key)) {
          return e;
        }
      }
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }

    return null;
  }
}
