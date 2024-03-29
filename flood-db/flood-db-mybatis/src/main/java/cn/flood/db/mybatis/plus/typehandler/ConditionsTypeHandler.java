package cn.flood.db.mybatis.plus.typehandler;

import cn.flood.db.mybatis.plus.support.Conditions;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmdai
 */
@MappedTypes(Conditions.class)
public class ConditionsTypeHandler extends BaseTypeHandler<Object> {

  Logger logger = LoggerFactory.getLogger(ConditionsTypeHandler.class);

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setObject(i, parameter);
  }

  @Override
  public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return null;
  }

  @Override
  public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return null;
  }

  @Override
  public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return null;
  }
}
