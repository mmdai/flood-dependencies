package cn.flood.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

/**
 * 
* <p>Title: SpringContextManager</p>  
* <p>Description: Spring上下文管理工具</p>  
* @author mmdai  
* @date 2018年10月25日
 */
public class SpringContextManager implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;

	public SpringContextManager(ApplicationContext applicationContext){
		this.applicationContext = applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if(SpringContextManager.applicationContext == null) {
			SpringContextManager.applicationContext = applicationContext;
        }
	}

	/**
	 * 返回ApplicationContext关联的Environment
	 */
	public static Environment getEnvironment() {
		return applicationContext.getEnvironment();
	}
	/**
	 * 获取上下文
	 * 
	 * @return Spring上下文
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 获取Spring配置
	 * @param key 配置名称
	 * @return 配置值
	 */
	public static String getProperties(String key) {
		return applicationContext.getEnvironment().getProperty(key);
	}

	/**
	 * 
	 * <p>Title: getProperties 获取Spring配置</p>  
	 * <p>Description: 没有配置时，返回默认值 </p>  
	 * @param key 配置名称
	 * @param defaultValue 默认值
	 * @return 配置值
	 */
	public static String getProperties(String key, String defaultValue) {
		return applicationContext.getEnvironment().getProperty(key, defaultValue);
	}
	
	/**
	 * 
	 * <p>Title: getBean</p>  
	 * <p>Description: 通过name获取 Bean</p>  
	 * @param name
	 * @return
	 */
    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }

    /**
     * 
     * <p>Title: getBean</p>  
     * <p>Description: 通过class获取Bean</p>  
     * @param clazz
     * @return
     */
    public static <T> T getBean(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }

    /**
     * 
     * <p>Title: getBean</p>  
     * <p>Description: 通过name,以及Clazz返回指定的Bean</p>  
     * @param name
     * @param clazz
     * @return
     */
    public static <T> T getBean(String name, Class<T> clazz){
        return applicationContext.getBean(name, clazz);
    }

}
