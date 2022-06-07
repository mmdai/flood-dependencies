package cn.flood.datasource.spring;

import java.util.List;

import javax.sql.DataSource;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import cn.flood.datasource.DataSourceRegisterInfo;
import cn.flood.datasource.MultiDataSourceRegister;
import cn.flood.datasource.dynamic.DynamicDataSource;
import cn.flood.datasource.enums.DataSourceEnum;


@AutoConfiguration
@EnableConfigurationProperties(value= {MultiDataSourceRegister.class})
@SuppressWarnings("unchecked")
public class DataSourceSpring {
	
	@Autowired
	private MultiDataSourceRegister multiDataSourceRegister;

	
 	@Bean(name="dataSource") // 只需要纳入动态数据源到spring容器
    @Primary
    public DataSource dataSource() {
        DynamicDataSource dataSource = new DynamicDataSource();
    	List<DataSourceRegisterInfo> list = multiDataSourceRegister.getSourceConfig();
    	
    	List<DataSource> dataSourceList = multiDataSourceRegister.getDataSourceList();
    	DataSource coreDataSource = dataSourceList.get(0);
        for(int i=0; i<dataSourceList.size(); i++) {
        	//添加数据源
        	dataSource.addDataSource(DataSourceEnum.valueOfEnum(i), dataSourceList.get(i));
        	if(list.get(i).isPrimary()) {
        		coreDataSource = dataSourceList.get(i);
        	}
        }
 
        //设置默认数据源
        dataSource.setDefaultTargetDataSource(coreDataSource);
        return dataSource;
    }

    /**
     * 注册一个StatViewServlet
     * @return
     */
    @Bean
    public ServletRegistrationBean druidStatViewServlet(){
        //org.springframework.boot.context.embedded.ServletRegistrationBean提供类的进行注册.
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(),"/druid/*");
        //添加初始化参数：initParams
        //白名单：
        //servletRegistrationBean.addInitParameter("allow","127.0.0.1");
        //IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
        //servletRegistrationBean.addInitParameter("deny","192.168.1.73");
        //登录查看信息的账号密码.
        servletRegistrationBean.addInitParameter("loginUsername","admin");
        servletRegistrationBean.addInitParameter("loginPassword","admin");
        //是否能够重置数据.
        servletRegistrationBean.addInitParameter("resetEnable","false");
        return servletRegistrationBean;
    }

    /**
     * 注册一个：filterRegistrationBean
     * @return
     */
    @Bean
    public FilterRegistrationBean druidStatFilter(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        //添加过滤规则.
        filterRegistrationBean.addUrlPatterns("/*");
        //添加不需要忽略的格式信息.
        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }


}
