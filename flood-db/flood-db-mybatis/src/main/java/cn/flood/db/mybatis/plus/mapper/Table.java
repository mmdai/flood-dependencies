package cn.flood.db.mybatis.plus.mapper;

import cn.flood.db.mybatis.plus.util.Misc;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Id;
import javax.persistence.Transient;
import org.apache.ibatis.builder.MapperBuilderAssistant;

/**
 * model 对应的 表信息
 * <p>
 * 数据库的表、字段命名只支持 驼峰式+下划线的方式
 * </p>
 *
 * @author mmdai
 */
public class Table {

  public final static Map<Class<?>, Table> TABLES = new HashMap<>();

  private Class<?> modelClass;
  /**
   * 表名称
   */
  private String name;
  /**
   * 对应mapper 的命名空间
   */
  private String currentNamespace;
  /**
   * id 属性
   */
  private TableField idTableField;
  /**
   * 字段信息
   */
  private List<TableField> tableFields;

  public Table(Class<?> modelClass, String currentNamespace) {
    this.modelClass = modelClass;
    this.name = Misc.toCamelUnderline(modelClass.getSimpleName());
    this.currentNamespace = currentNamespace;
    this.tableFields = new ArrayList<>();
    init();
  }

  public static Table valueOf(Class<?> clazz, String currentNamespace) {
    Table table = TABLES.get(clazz);
    if (null == table) {
      table = new Table(clazz, currentNamespace);
      TABLES.put(clazz, table);
    }
    return table;
  }

  public static Table valueOf(MapperBuilderAssistant builderAssistant, Class<?> clazz) {
    return valueOf(clazz, builderAssistant.getCurrentNamespace());
  }

  private void init() {
    initTableField(modelClass);
  }

  private void initTableField(Class<?> modelClazz) {
    Class<?> superclass = modelClazz.getSuperclass();
    if (Object.class != superclass) {
      initTableField(superclass);
    }
    //init Model class Annotations
    javax.persistence.Table table = modelClazz.getAnnotation(javax.persistence.Table.class);
    if (null != table) {
      this.name = table.name();
    }

    Field[] fields = modelClazz.getDeclaredFields();
    for (Field field : fields) {
      if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
        continue;
      }
      Transient transientA = field.getAnnotation(Transient.class);
      if (null != transientA) {
        continue;
      }
      Id id = field.getAnnotation(Id.class);
      // added by yangjian at 2018-08-11
      // null == this.idTableField 保证一个 table 只有一个主键 id
      // 添加 @Id 注解支持, 如果没有添加任何注解，则默认 primaryKey 字段为 id
      boolean isId = null == this.idTableField && (null != id || "id".equals(field.getName()));

      addTableFields(field, isId);
    }
  }

  public String getName() {
    return name;
  }

  public String getWrapName() {
    return '`' + getName() + '`';
  }

  public String getCurrentNamespace() {
    return currentNamespace;
  }

  public TableField getIdTableField() {
    return idTableField;
  }

  public List<TableField> getTableFields() {
    return tableFields;
  }

  public Table addTableFields(Field field, boolean isId) {
    TableField tableField = new TableField(field);
    if (isId) {
      tableField.setIsId();
      this.idTableField = tableField;
    }
    this.tableFields.add(tableField);
    return this;
  }

  public String getNotNullUpdateSetSql() {
    StringBuilder sb = new StringBuilder();
    sb.append("<set>");
    for (TableField field : getTableFields()) {
      if (!field.isId()) {
        sb.append("<if test=\"").append(field.getFieldName()).append(" != null\">")
            .append(field.getColumnName()).append("=#{").append(field.getFieldName())
            .append("},</if>");
      }
    }
    sb.append("</set>");
    return sb.toString();
  }

  public String getUpdateSetSql() {
    StringBuilder sb = new StringBuilder();
    sb.append("<set>");
    for (TableField field : getTableFields()) {
      if (!field.isId()) {
        sb.append(field.getColumnName()).append("=#{").append(field.getFieldName()).append("},");
      }
    }
    sb.append("</set>");
    return sb.toString();
  }

  public String getFieldSql() {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (TableField field : getTableFields()) {
      if (1 < sb.length()) {
        sb.append(" , ");
      }
      sb.append("#{").append(field.getFieldName()).append("}");
    }
    sb.append(")");
    return sb.toString();
  }

  public String getColumnsSql() {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (TableField field : getTableFields()) {
      if (1 < sb.length()) {
        sb.append(" , ");
      }
      sb.append(field.getColumnName());
    }
    sb.append(")");
    return sb.toString();
  }

  public String getColumnsSqlAs() {
    StringBuilder sb = new StringBuilder();
    sb.append(" ");
    for (TableField field : getTableFields()) {
      if (1 < sb.length()) {
        sb.append(" , ");
      }
      sb.append(field.getColumnName()).append(" AS ").append(field.getFieldName());
    }
    sb.append(" ");
    return sb.toString();
  }

  public String getResultMap() {
    StringBuilder sb = new StringBuilder();
    sb.append("<resultMap id=\"").append(this.modelClass.getSimpleName().toLowerCase())
        .append("ResultMap\" type=\"").append(this.modelClass.getSimpleName()).append("\">");
    for (TableField field : getTableFields()) {
      if (field.isId()) {
        sb.append("\n<id property=\"").append(field.getFieldName()).append("\" column=\"")
            .append(field.getColumnName()).append("\" />");
      } else {
        sb.append("\n<result property=\"").append(field.getFieldName()).append("\" column=\"")
            .append(field.getColumnName()).append("\" />");
      }
    }
    sb.append("\n</resultMap>");
    return sb.toString();
  }

  @Override
  public String toString() {
    return "Table{" +
        "currentNamespace='" + currentNamespace + '\'' +
        ", name='" + name + '\'' +
        ", idTableField=" + idTableField +
        '}';
  }
}
