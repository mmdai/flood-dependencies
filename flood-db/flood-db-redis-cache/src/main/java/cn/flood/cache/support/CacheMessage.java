/**  
* <p>Title: CacheMessage.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月7日  
* @version 1.0  
*/  
package cn.flood.cache.support;

import java.io.Serializable;

/**  
* <p>Title: CacheMessage</p>  
* <p>Description: redis消息发布/订阅，传输的消息类</p>  
* @author mmdai  
* @date 2018年12月7日  
*/
public class CacheMessage implements Serializable {

	/** */
	private static final long serialVersionUID = 5987219310442078193L;

	private String cacheName;
	
	private Object key;

	public CacheMessage(String cacheName, Object key) {
		super();
		this.cacheName = cacheName;
		this.key = key;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

}
