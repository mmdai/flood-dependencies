package cn.flood.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
/**
 * 
* <p>Title: PageHelperConfig</p>  
* <p>Description: 配置分页</p>  
* @author mmdai  
* @date 2018年11月12日
 */
@Configuration
public class PageHelperConfig {
	
	@Value("${spring.pagehelper.helperDialect:}")
	private String helperDialect;
	
	@Bean
	public PageHelper pageHelper() {  
		PageHelper pageHelper=new PageHelper();
		Properties properties=new Properties();
		if(StringUtils.isEmpty(helperDialect)){
			properties.setProperty("helperDialect","mysql");
		}
		properties.setProperty("reasonable","true");
		properties.setProperty("supportMethodsArguments","true");
		properties.setProperty("params","count=countSql");
		pageHelper.setProperties(properties);
        return pageHelper;  
	}
}
