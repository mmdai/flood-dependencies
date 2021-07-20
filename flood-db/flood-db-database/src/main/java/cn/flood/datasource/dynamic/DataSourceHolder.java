/**  
* <p>Title: DataSourceHolder.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月12日  
* @version 1.0  
*/  
package cn.flood.datasource.dynamic;

import cn.flood.datasource.enums.DataSourceEnum;

/**
 * 
* <p>Title: DataSourceHolder</p>  
* <p>Description: 用于数据源切换</p>  
* @author mmdai  
* @date 2019年11月11日
 */
public class DataSourceHolder {
	
	private static final ThreadLocal<DataSourceEnum> dataSourceKey = new ThreadLocal<>();

	public static DataSourceEnum getDataSourceKey() {
        return dataSourceKey.get();
    }

    public static void setDataSourceKey(DataSourceEnum type) {
        dataSourceKey.set(type);
    }

    public static void clearDataSourceKey() {
        dataSourceKey.remove();
    }
	
	

}
