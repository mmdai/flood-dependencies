package cn.flood.db.mybatis.plus.mapper;


import cn.flood.db.mybatis.plus.util.Misc;
import java.lang.reflect.Field;
import javax.persistence.Column;

/**
 * model 对应的 字段
 *
 * @author mmdai
 */
public class TableField {

  /**
   * 属性名称
   */
  private String fieldName;
  /**
   * 字段名称
   */
  private String columnName;
  /**
   * 是否为 id
   */
  private boolean isId;
  /**
   * 属性的java 类型
   */
  private Class<?> javaType;

  public TableField(Field field) {
    this.fieldName = field.getName();
    this.columnName = Misc.toCamelUnderline(fieldName);
    //如果有添加了注解，则使用注解覆盖
    Column column = field.getAnnotation(Column.class);
    if (null != column) {
      this.columnName = column.name();
    }
    this.javaType = field.getType();
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getColumnName() {
    return columnName;
  }

  public boolean isId() {
    return isId;
  }

  public Class<?> getJavaType() {
    return javaType;
  }

  public void setIsId() {
    this.isId = true;
  }
}
