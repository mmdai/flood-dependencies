package cn.flood.db;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import com.alibaba.druid.support.http.WebStatFilter;


/**
 *  TODO Druid拦截器，用于查看Druid监控
 * <p>
 * Created on 2017年6月19日 
 * <p>
 * @author mmdai
 * @date 2017年6月19日
 */
//@WebFilter(filterName="druidWebStatFilter",urlPatterns="/*",
//initParams={
//    @WebInitParam(name="exclusions",value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")// 忽略资源
//})
public class DruidStatFilter extends WebStatFilter {

	/*@Bean(name="druidWebStatFilter")  
    public FilterRegistrationBean filterRegistrationBean() {  
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();  
        filterRegistrationBean.setFilter(new WebStatFilter());  
        filterRegistrationBean.addUrlPatterns("/*");  
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*"); //忽略资源  
        filterRegistrationBean.addInitParameter("profileEnable", "true");  
        filterRegistrationBean.addInitParameter("principalCookieName", "USER_COOKIE");  
        filterRegistrationBean.addInitParameter("principalSessionName", "USER_SESSION");  
        return filterRegistrationBean;  
    }  */
}
