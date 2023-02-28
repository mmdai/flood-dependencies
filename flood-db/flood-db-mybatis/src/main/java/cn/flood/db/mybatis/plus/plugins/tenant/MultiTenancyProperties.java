package cn.flood.db.mybatis.plus.plugins.tenant;


import java.io.Serializable;
import lombok.Data;

/**
 * @Author iloveoverfly
 * @LocalDateTime 2020/6/11 15:43
 **/
@Data
public class MultiTenancyProperties implements Serializable {

  public static final String MULTI_TENANCY_QUERY_COLUMN_PROPERTY = "multiTenancyQueryColumn";
  public static final String CONDITION_TYPE_PROPERTY = "conditionType";
  private static final long serialVersionUID = -1982635513027523884L;
  /**
   * 租户的字段名称
   */
  private String multiTenancyQueryColumn;
  /**
   * 租户字段查询条件 {@link ConditionFactory.ConditionTypeEnum}
   */
  private String conditionType;

  public MultiTenancyProperties() {
    // 默认使用IN 条件，例如 id in(1,2,3)
    this.conditionType = ConditionFactory.ConditionTypeEnum.IN.name();
  }
}
