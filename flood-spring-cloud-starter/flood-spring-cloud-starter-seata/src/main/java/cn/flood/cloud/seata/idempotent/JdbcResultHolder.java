package cn.flood.cloud.seata.idempotent;

import cn.flood.base.core.Func;
import cn.flood.base.core.lang.StringUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/5/6 15:44
 */
public class JdbcResultHolder implements ResultHolder {

  /**
   * sql
   */
  private static final String UPDATE_FIELDS = "action_class, xid, context";
  /**
   * 插入
   */
  private static final String SQL_INSERT =
      "insert into seata_tcc (" + UPDATE_FIELDS + ") values (?, ?, ?) ";
  /**
   * 删除
   */
  private static final String SQL_DELETE_BY_XID = "delete from seata_tcc where action_class = ? and xid = ? ";
  /**
   * 查询
   */
  private static final String SQL_SELECT_BY_XID = "select count(1) from seata_tcc where action_class = ? and xid = ? ";
  private final JdbcTemplate jdbcTemplate;


  public JdbcResultHolder(DataSource dataSource) {
    Assert.notNull(dataSource, "DataSource required");
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  /**
   * @param actionClass
   * @param xid
   * @param context
   */
  @Override
  public void setResult(String actionClass, String xid, String context) {
    List<Object> objects = getFieldsForUpdate(actionClass, xid, context);
    jdbcTemplate.update(SQL_INSERT, StringUtils.objectListToArray(objects));
  }

  /**
   * @param actionClass
   * @param xid
   * @return
   */
  @Override
  public boolean getResult(String actionClass, String xid) {
    Integer num = jdbcTemplate.queryForObject(SQL_SELECT_BY_XID, Integer.class, actionClass, xid);
    if (Func.isNotEmpty(num) && num > 0) {
      return true;
    }
    return false;
  }

  /**
   * @param actionClass
   * @param xid
   */
  @Override
  public void removeResult(String actionClass, String xid) {
    jdbcTemplate.update(SQL_DELETE_BY_XID, actionClass, xid);
  }

  /**
   * 插入、修改操作的sql参数
   *
   * @param actionClass
   * @param xid
   * @param context
   * @return
   */
  private List<Object> getFieldsForUpdate(String actionClass, String xid, String context) {
    List<Object> objects = new ArrayList<>();
    // 类名
    objects.add(actionClass);
    // xid
    objects.add(xid);
    // TCC信息
    objects.add(context);
    return objects;
  }


  @PostConstruct
  public void init() {
    String sql = "CREATE TABLE IF NOT EXISTS `seata_tcc`  (\n" +
        "  `id` int(32) NOT NULL AUTO_INCREMENT COMMENT '自增 id',\n" +
        "  `action_class` varchar(256) NOT NULL COMMENT '类名',\n" +
        "  `xid` varchar(128) NOT NULL COMMENT 'xid',\n" +
        "  `context` varchar(512) DEFAULT NULL COMMENT 'TCC信息',\n" +
        "  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',\n"
        +
        "  PRIMARY KEY(id)," +
        "  KEY `index_seata_tcc_actionClass` (`action_class`)," +
        "  KEY `index_seata_tcc_xid` (`xid`)" +
        ")COMMENT='seata_tcc信息表',ENGINE = INNODB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;";
    this.jdbcTemplate.execute(sql);
  }
}
