/**  
* <p>Title: PageHelperConfig.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月12日  
* @version 1.0  
*/  
package cn.flood.datasource.spring;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;

/**  
* <p>Title: PageHelperConfig</p>  
* <p>Description: 分页插件引入</p>  
* @author mmdai  
* @date 2018年12月12日  
*/
//@Configuration
public class PageHelperSpring {
	
//	@Value("${spring.pagehelper.helperDialect:}")
//	private String helperDialect;
//
//	@Bean
//	public PageHelper pageHelper() {
//		PageHelper pageHelper = new PageHelper();
//		Properties properties = new Properties();
//		if(StringUtils.isEmpty(helperDialect)){
//			properties.setProperty("helperDialect","mysql");
//		}
//		properties.setProperty("reasonable","true");
//		properties.setProperty("supportMethodsArguments","true");
//		properties.setProperty("params","count=countSql");
//		pageHelper.setProperties(properties);
//        return pageHelper;
//	}

}
