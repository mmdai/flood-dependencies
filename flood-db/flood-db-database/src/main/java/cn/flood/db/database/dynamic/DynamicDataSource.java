/**  
* <p>Title: DynamicDataSource.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月12日  
* @version 1.0  
*/  
package cn.flood.db.database.dynamic;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import cn.flood.db.database.enums.DataSourceEnum;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


/**
 * 
* <p>Title: DynamicDataSource</p>  
* <p>Description: spring动态数据源（需要继承AbstractRoutingDataSource）</p>  
* @author mmdai  
* @date 2019年11月11日
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
	
	private Map<Object, Object> datasources;

	public DynamicDataSource() {
        datasources = new HashMap<>();
        super.setTargetDataSources(datasources);
    }
	
	public <T extends DataSource> void addDataSource(DataSourceEnum key, T data) {
        datasources.put(key, data);
    }
	/* (non-Javadoc)  
	 * <p>Title: determineCurrentLookupKey</p>  
	 * <p>Description: </p>  
	 * @return  
	 * @see org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource#determineCurrentLookupKey()  
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceHolder.getDataSourceKey();
	}

}
