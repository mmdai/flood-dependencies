/**  
* <p>Title: CookieStore.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月22日  
* @version 1.0  
*/  
package cn.flood.okhttp.cookie;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**  
* <p>Title: CookieStore</p>  
* <p>Description: 定义Cookie存储机制</p>  
* @author mmdai  
* @date 2019年7月22日  
*/
public interface CookieStore {
	/**
	 * 
	 * <p>Title: add</p>  
	 * <p>Description: 为请求地址{@code url}增加Cookie</p>  
	 * @param uri  请求地址
	 * @param cookies Cookie列表
	 */
	void add(HttpUrl uri, List<Cookie> cookies);
	/**
	 * 
	 * <p>Title: get</p>  
	 * <p>Description: 获取某个请求地址的Cookie列表</p>  
	 * @param uri 请求地址
	 * @return Cookie列表
	 */
	List<Cookie> get(HttpUrl uri);
	/**
	 * 
	 * <p>Title: getCookies</p>  
	 * <p>Description: 获取所有Cookie列表</p>  
	 * @return {@link Cookie}
	 */
	List<Cookie> getCookies();
	/**
	 * 
	 * <p>Title: remove</p>  
	 * <p>Description: 删除请求的某个Cookie</p>  
	 * @param uri 请求地址
	 * @param cookie Cookie对象
	 * @return 删除成功则返回{@code true}，否则返回{@code false}
	 */
	boolean remove(HttpUrl uri, Cookie cookie);
	/**
	 * 
	 * <p>Title: removeAll</p>  
	 * <p>Description: 清空所有Cookie列表</p>  
	 * @return 清空成功则返回{@code true}，否则返回{@code false}
	 */
	boolean removeAll();
}
