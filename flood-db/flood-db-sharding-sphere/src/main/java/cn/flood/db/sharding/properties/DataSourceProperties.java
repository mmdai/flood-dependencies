package cn.flood.db.sharding.properties;

import lombok.Data;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/4/28 13:33
 */
@Data
public class DataSourceProperties {

  /**
   * driverClassName
   */
  private String driverClassName;
  /**
   * url
   */
  private String url;
  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private String password;
}
