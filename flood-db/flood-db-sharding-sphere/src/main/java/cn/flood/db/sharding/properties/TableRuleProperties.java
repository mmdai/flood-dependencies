package cn.flood.db.sharding.properties;

import lombok.Data;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/4/28 12:05
 */
@Data
public class TableRuleProperties {

  /**
   * 逻辑表名
   */
  private String logicTable;
  /**
   * 库分片列名称,多个列以逗号分隔
   */
  private String dbShardingColumns;
  /**
   * 库分片策略类,全限定类名
   */
  private String dbShardingAlgorithm;
  /**
   * 表分片列名称,多个列以逗号分隔
   */
  private String tableShardingColumns;
  /**
   * 表分片策略类,全限定类名
   */
  private String tableShardingAlgorithm;
}
