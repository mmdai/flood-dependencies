/**  
* <p>Title: DataSourceAnnotation.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月12日  
* @version 1.0  
*/  
package cn.flood.db.database.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
* <p>Title: DataSourceAnnotation</p>  
* <p>Description:  数据源选择</p>  
* @author mmdai  
* @date 2019年11月11日
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSourceAnnotation {
	/**
	 * 
	 * <p>Title: name</p>  
	 * <p>Description: 数据源名称</p>  
	 * @return
	 */
	String name();
}
