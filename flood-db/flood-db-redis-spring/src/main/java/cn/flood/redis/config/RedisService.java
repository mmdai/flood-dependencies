/**  
* <p>Title: RedisService1.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月8日  
* @version 1.0  
*/  
package cn.flood.redis.config;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**  
* <p>Title: RedisService1</p>  
* <p>Description: redis服务类</p>  
* @author mmdai  
* @date 2018年12月8日  
*/
public interface RedisService {
	
	
	RedisTemplate<String, Object> getRedisTemplate();
	//////////////////////////////////////////////STRING///////////////////////////////////////////////////
	/**  
	* @Title: remove  
	* @Description: TODO(批量删除对应的value)  
	* @param @param keys    参数  
	* @return void    返回类型  
	* @throws  
	*/ 
	void remove(final String... keys);
	/**  
	* @Title: removePattern  
	* @Description: TODO(批量删除key)  
	* @param @param pattern    参数  
	* @return void    返回类型  
	* @throws  
	*/ 
	void removePattern(final String pattern);
	/**  
	* @Title: exists  
	* @Description: TODO(判断缓存中是否有对应的value)  
	* @param @param key
	* @param @return    参数  
	* @return boolean    返回类型  
	* @throws  
	*/ 
	boolean exists(final String key);
	/**  
	* @Title: remove  
	* @Description: TODO(删除对应的value)  
	* @param @param key    参数  
	* @return void    返回类型  
	* @throws  
	*/ 
	boolean remove(final String key);
	/**  
	* @Title: get  
	* @Description: TODO(读取缓存)  
	* @param @param key
	* @param @return    参数  
	* @return String    返回类型  
	* @throws  
	*/ 
	<T> T get(final String key);
	/**  
	* @Title: set  
	* @Description: TODO(写入缓存，永久)  
	* @param @param key
	* @param @param value
	* @param @return    参数  
	* @return boolean    返回类型  
	* @throws  
	*/ 
	<T> boolean set(final String key, T value);
	/**  
	* @Title: set  
	* @Description: TODO(写入缓存（有时间限制）)  
	* @param @param key
	* @param @param value
	* @param @param expireTime
	* @param @param timeUnit
	* @param @return    参数  
	* @return boolean    返回类型  
	* @throws  
	*/ 
	<T> boolean set(final String key, T value, Long expireTime, TimeUnit timeUnit);
	
	/**  
	* @Title: setIfPresent  
	* @Description: TODO(如果存在，则更新时间）)  
	* @param @param key
	* @param @param expireTime(单位秒)
	* @param @param timeUnit
	* @param @return    参数  
	* @return boolean    返回类型  
	* @throws  
	*/ 
	<T> boolean setIfPresent(final String key,T value, Long expireTime, TimeUnit timeUnit);
	
	//////////////////////////////////////////////HASH///////////////////////////////////////////////////
	/**  
	* @Title: exists  
	* @Description: TODO(判断缓存中是否有对应的value)  
	* @param @param key
	* @param @param field
	* @param @return    参数  
	* @return boolean    返回类型  
	* @throws  
	*/ 
	boolean exists(final String key, final String field);
	/**  
	* @Title: existsHash  
	* @Description: TODO(判断缓存中是否有对应的value)  
	* @param @param key
	* @param @param field
	* @param @return    参数  
	* @return boolean    返回类型  
	* @throws  
	*/ 
	boolean remove(final String key, final String field);
	/**  
	* @Title: getHash  
	* @Description: TODO(读取缓存)  
	* @param @param key
	* @param @param field
	* @param @return    参数  
	* @return String    返回类型  
	* @throws  
	*/ 
	String getHash(final String key, final String field);
	
	/**  
	* @Title: getAllHash  
	* @Description: TODO(读取缓存)  
	* @param @param key
	* @param @param field
	* @param @return    参数  
	* @return String    返回类型  
	* @throws  
	*/ 
	Map<String, Object> getAllHash(final String key);
	/**  
	* @Title: setHash  
	* @Description: TODO(写入缓存，永久)  
	* @param @param key
	* @param @param field
	* @param @param value
	* @param @return    参数  
	* @return boolean    返回类型  
	* @throws  
	*/ 
	<T> boolean setHash(final String key, final String field, T value);
	/**  
	* @Title: setHash  
	* @Description: TODO(写入缓存（有时间限制）)  
	* @param @param key
	* @param @param value
	* @param @param expireTime
	* @param @param timeUnit
	* @param @return    参数  
	* @return boolean    返回类型  
	* @throws  
	*/ 
	<T> boolean setHash(final String key,final String field, T value, Long expireTime, TimeUnit timeUnit);
	/**  
	* @Title: setHash  
	* @Description: TODO(写入缓存，永久)  
	* @param @param key
	* @param @param value
	* @param @return    参数  
	* @return boolean    返回类型  
	* @throws  
	*/ 
	boolean setAllHash(final String key, Map<String, Object> value);
	/**  
	* @Title: setAllHash  
	* @Description: TODO(写入缓存（有时间限制）)  
	* @param @param key
	* @param @param value
	* @param @param expireTime
	* @param @param timeUnit
	* @param @return    参数  
	* @return boolean    返回类型  
	* @throws  
	*/ 
	boolean setAllHash(final String key, Map<String, Object> value, Long expireTime, TimeUnit timeUnit);
	
	/**  
	* @Title: hIncrBy  
	* @Description: TODO(将哈希字段的整数值按给定数字增加,为散了中某个值加上 long delta)  
	* @param @param hKey  (key对应的字段)
	* @param @param field (value对应的字段)
	* @param @param value (加，减值)
	* @param @return    参数  
	* @return Long    返回类型  
	* @throws  
	*/ 
	Long hIncrBy(String hKey, final String field, Long value);

	/**  
	* @Title: hIncrBy  
	* @Description: TODO(将哈希字段的整数值按给定数字增加,为散了中某个值加上 double delta)  
	* @param @param hKey  (key对应的字段)
	* @param @param field (value对应的字段)
	* @param @param value (加，减值)
	* @param @return    参数  
	* @return Long    返回类型  
	* @throws  
	*/ 
	Double hIncrBy(String hKey, final String field, Double value);
	/**  
	* @Title: setLPush  
	* @Description: TODO(将一个值插入到列表头部，value可以重复，返回列表的长度)  
	* @param @param lKey  (key对应的字段)
	* @param @param lValue (value对应的字段)
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/ 
	long setLPush(String lKey, final String lValue);
	/**  
	* @Title: setLPush  
	* @Description: TODO(将多个值插入到列表头部，value可以重复，返回列表的长度)  
	* @param @param lKey  (key对应的字段)
	* @param @param lValue (value对应的字段)
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/ 
	long setLPush(String lKey, final String[] lValue);
	/**  
	* @Title: setRPush  
	* @Description: TODO(在列表中的尾部添加一个值，返回列表的长度)  
	* @param @param lKey  (key对应的字段)
	* @param @param lValue (value对应的字段)
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/ 
	long setRPush(String lKey, final String lValue);
	/**  
	* @Title: setRPush  
	* @Description: TODO(在列表中的尾部添加多个值，返回列表的长度)  
	* @param @param lKey  (key对应的字段)
	* @param @param lValue (value对应的字段)
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/ 
	long setRPush(String lKey, final String[] lValue);
	
	/**  
	* @Title: getLLen  
	* @Description: TODO(获取列表长度，key为空时返回0)  
	* @param @param lKey  (key对应的字段)
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/ 
	long getLLen(String lKey);
	/**  
	* @Title: getLpop  
	* @Description: TODO(移出并获取列表的第一个元素，当列表不存在或者为空时，返回Null)  
	* @param @param lKey  (key对应的字段)	
	* @param @return    参数  
	* @return String    返回类型  
	* @throws  
	*/ 
	String getLpop(String lKey);
	/**  
	* @Title: getRpop  
	* @Description: TODO(移除并获取列表最后一个元素，当列表不存在或者为空时，返回Null)  
	* @param @param lKey  (key对应的字段)	
	* @param @return    参数  
	* @return String    返回类型  
	* @throws  
	*/ 
	String getRpop(String lKey);
	/**  
	* @Title: incr  
	* @Description: TODO(递增)  
	* @param @param key  (key对应的字段)	
	* @param @return    参数  
	* @return long    总值  
	* @throws  
	*/ 
	long incr(String key, long delta);
	/**  
	* @Title: decr  
	* @Description: TODO(递减)  
	* @param @param key  (key对应的字段)	
	* @param @return    参数  
	* @return long    总值  
	* @throws  
	*/ 
	long decr(String key, long delta);
	
}
