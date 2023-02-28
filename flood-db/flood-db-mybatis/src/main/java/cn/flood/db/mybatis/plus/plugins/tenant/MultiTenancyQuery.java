package cn.flood.db.mybatis.plus.plugins.tenant;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author iloveoverfly
 * @LocalDateTime 2020/6/10 17:11
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiTenancyQuery implements Serializable {

  private static final long serialVersionUID = -5841093611020112607L;
  /**
   * 多租户过滤值
   */
  private Object multiTenancyQueryValue;
  /**
   * 多租户过滤数据字段
   */
  private String multiTenancyQueryColumn;
  /**
   * 数据库表前缀名
   */
  private String preTableName;
  /**
   * sql查询条件类型
   */
  private ConditionFactory.ConditionTypeEnum conditionType;
}
