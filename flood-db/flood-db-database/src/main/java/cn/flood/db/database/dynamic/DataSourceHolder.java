/**
 * <p>Title: DataSourceHolder.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author mmdai
 * @date 2018年12月12日
 * @version 1.0
 */
package cn.flood.db.database.dynamic;

import cn.flood.db.database.enums.DataSourceEnum;

/**
 *
 * <p>Title: DataSourceHolder</p>  
 * <p>Description: 用于数据源切换</p>  
 * @author mmdai
 * @date 2019年11月11日
 */
public class DataSourceHolder {

  private static final ThreadLocal<DataSourceEnum> DATA_SOURCE_KEY = new ThreadLocal<>();

  public static DataSourceEnum getDataSourceKey() {
    return DATA_SOURCE_KEY.get();
  }

  public static void setDataSourceKey(DataSourceEnum type) {
    DATA_SOURCE_KEY.set(type);
  }

  public static void clearDataSourceKey() {
    DATA_SOURCE_KEY.remove();
  }


}
