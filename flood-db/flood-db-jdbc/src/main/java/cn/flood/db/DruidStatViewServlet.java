package cn.flood.db;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.alibaba.druid.support.http.StatViewServlet;

/**
 * TODO Druid的Servlet
 * <p>
 * Created on 2017年6月19日 
 * <p>
 * @author mmdai
 * @date 2017年6月19日
 */

//@WebServlet(urlPatterns = "/druid/*",
//initParams={
////        @WebInitParam(name="allow",value="127.0.0.1,192.168.1.126"),// IP白名单 (没有配置或者为空，则允许所有访问)
////        @WebInitParam(name="deny",value="192.168.1.111"),// IP黑名单 (存在共同时，deny优先于allow)
//        @WebInitParam(name="loginUsername",value="admin"),// 用户名
//        @WebInitParam(name="loginPassword",value="admin"),// 密码
//        @WebInitParam(name="resetEnable",value="true")// 禁用HTML页面上的“Reset All”功能
//})
public class DruidStatViewServlet extends StatViewServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8227562165467625784L;

	/*@Bean  
    public ServletRegistrationBean druidServlet() {  
        ServletRegistrationBean reg = new ServletRegistrationBean();  
        reg.setServlet(new StatViewServlet());  
        reg.addUrlMappings("/druid/*");  //url 匹配  
//        reg.addInitParameter("allow", "192.168.16.110,127.0.0.1"); // IP白名单 (没有配置或者为空，则允许所有访问)  
//        reg.addInitParameter("deny", "192.168.16.111"); //IP黑名单 (存在共同时，deny优先于allow)  
        reg.addInitParameter("loginUsername", "flood");//登录名  
        reg.addInitParameter("loginPassword", "A#flood8");//登录密码  
        reg.addInitParameter("resetEnable", "false"); // 禁用HTML页面上的“Reset All”功能  
        return reg;  
    }  */
}
