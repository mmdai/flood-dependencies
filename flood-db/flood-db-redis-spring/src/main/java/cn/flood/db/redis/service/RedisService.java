/**  
* <p>Title: RedisService1.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月8日  
* @version 1.0  
*/  
package cn.flood.db.redis.service;

import cn.flood.db.redis.util.ConvertUtil;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;
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

	/**
	 * @Title: incrementLong
	 * @Description: (将key的整数值按给定数字增加,为散了中某个值加上 long delta)
	 * @param @param key  (key对应的字段)
	 * @param @param value (加，减值)
	 * @return Long    返回类型
	 * @throws
	 */
	Long incrementLong(final String key, Long value);

	/**
	 * @Title: incrementDouble
	 * @Description: (将key的整数值按给定数字增加,为散了中某个值加上 double delta)
	 * @param @param key  (key对应的字段)
	 * @param @param value (加，减值)
	 * @return Long    返回类型
	 * @throws
	 */
	Double incrementDouble(final String key, Double value);

	/**
	 * @Title: decrementLong
	 * @Description: (将key的整数值按给定数字减少,为散了中某个值加上 long delta)
	 * @param @param key  (key对应的字段)
	 * @param @param value (加，减值)
	 * @return Long    返回类型
	 * @throws
	 */
	Long decrementLong(final String key, Long value);


	
	//////////////////////////////////////////////HASH///////////////////////////////////////////////////
	/**  
	* @Title: exists  
	* @Description: (判断缓存中是否有对应的value)
	* @param @param key
	* @param @param field
	* @param @return    参数  
	* @return boolean    返回类型  
	* @throws  
	*/ 
	boolean exists(final String key, final String field);
	/**  
	* @Title: existsHash  
	* @Description: (判断缓存中是否有对应的value)
	* @param @param key
	* @param @param field
	* @param @return    参数  
	* @return boolean    返回类型  
	* @throws  
	*/ 
	boolean remove(final String key, final String field);
	/**  
	* @Title: getHash  
	* @Description: (读取缓存)
	* @param @param key
	* @param @param field
	* @param @return    参数  
	* @return String    返回类型  
	* @throws  
	*/ 
	<T> T getHash(final String key, final String field);
	
	/**  
	* @Title: getAllHash  
	* @Description: (读取缓存)
	* @param @param key
	* @param @param field
	* @param @return    参数  
	* @return String    返回类型  
	* @throws  
	*/ 
	Map<String, Object> getAllHash(final String key);
	/**  
	* @Title: setHash  
	* @Description: (写入缓存，永久)
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
	* @Description: (写入缓存（有时间限制）)
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
	* @Description: (写入缓存，永久)
	* @param @param key
	* @param @param value
	* @param @return    参数  
	* @return boolean    返回类型  
	* @throws  
	*/ 
	boolean setAllHash(final String key, Map<String, Object> value);
	/**  
	* @Title: setAllHash  
	* @Description: (写入缓存（有时间限制）)
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
	* @Title: hIncrementLong
	* @Description: (将哈希字段的整数值按给定数字增加,为散了中某个值加上 long delta)
	* @param @param hKey  (key对应的字段)
	* @param @param field (value对应的字段)
	* @param @param value (加，减值)
	* @param @return    参数  
	* @return Long    返回类型  
	* @throws  
	*/ 
	Long hIncrementLong(String hKey, final String field, Long value);

	/**  
	* @Title: hIncrementDouble
	* @Description: (将哈希字段的整数值按给定数字增加,为散了中某个值加上 double delta)
	* @param @param hKey  (key对应的字段)
	* @param @param field (value对应的字段)
	* @param @param value (加，减值)
	* @param @return    参数  
	* @return Long    返回类型  
	* @throws  
	*/ 
	Double hIncrementDouble(String hKey, final String field, Double value);

	/****************************** LIST START ***********************************/
	/**
	 * 获取对象列表数量
	 * @see <a href="http://redis.io/commands/llen">Redis Documentation: LLEN</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @return 返回列表数量
	 */
	Long size(String key);

	/**
	 * 获取所有对象
	 * @see <a href="http://redis.io/commands/lrange">Redis Documentation: LRANGE</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @return 返回对象列表
	 */
	List getAll(String key);


	/**
	 * 从左获取范围内的对象
	 * @see <a href="http://redis.io/commands/lrange">Redis Documentation: LRANGE</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param startIndex 起始索引
	 * @param endIndex 结束索引
	 * @return 返回对象列表
	 */
	List lrange(String key, Long startIndex, Long endIndex);

	/**
	 * 从右获取范围内的对象
	 * @see <a href="http://redis.io/commands/lrange">Redis Documentation: LRANGE</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param startIndex 起始索引
	 * @param endIndex 结束索引
	 * @return 返回对象列表
	 */
	List rrange(String key, Long startIndex, Long endIndex);

	/**
	 * 从左移除对象
	 * @see <a href="http://redis.io/commands/lrem">Redis Documentation: LREM</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param count 个数
	 * @param value 对象
	 * @return 返回移除数量
	 */
	Long lremove(String key, Long count, Object value);

	/**
	 * 从右移除对象
	 * @see <a href="http://redis.io/commands/lrem">Redis Documentation: LREM</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param count 个数
	 * @param value 对象
	 * @return 返回移除数量
	 */
	Long rremove(String key, Long count, Object value);

	/**
	 * 从左截取对象(会修改redis中列表)
	 * @see <a href="http://redis.io/commands/ltrim">Redis Documentation: LTRIM</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param startIndex 起始索引
	 * @param endIndex 结束索引
	 * @return 返回截取的对象列表
	 */
	List lsubList(String key, Long startIndex, Long endIndex);

	/**
	 * 从右截取对象(会修改redis中列表)
	 * @see <a href="http://redis.io/commands/ltrim">Redis Documentation: LTRIM</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param startIndex 起始索引
	 * @param endIndex 结束索引
	 * @return 返回截取的对象列表
	 */
	List rsubList(String key, Long startIndex, Long endIndex);

	/**
	 * 从左修改指定索引的对象
	 * @see <a href="http://redis.io/commands/ltrim">Redis Documentation: LTRIM</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param index 索引
	 * @param value 对象
	 */
	void lset(String key, Long index, Object value);

	/**
	 * 从右修改指定索引的对象
	 * @see <a href="http://redis.io/commands/ltrim">Redis Documentation: LTRIM</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param index 索引
	 * @param value 对象
	 */
	void rset(String key, Long index, Object value);

	/**
	 * 从左获取对象
	 * @see <a href="http://redis.io/commands/lindex">Redis Documentation: LINDEX</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param index 索引
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	<T> T lget(String key, Long index);


	/**
	 * 从右获取对象
	 * @see <a href="http://redis.io/commands/lindex">Redis Documentation: LINDEX</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param index 索引
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	<T> T rget(String key, Long index);



	/**
	 * 从左插入对象
	 * @see <a href="http://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回列表数量
	 */
	Long lpush(String key, Object value);

	/**
	 * 按照中心点从左插入对象
	 * @see <a href="http://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param pivot 中心点对象
	 * @param value 对象
	 * @return 返回列表数量
	 */
	Long lpush(String key, Object pivot, Object value);

	/**
	 * 从左插入多个对象
	 * @see <a href="http://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param values 对象
	 * @return 返回列表数量
	 */
	Long lpushAll(String key, Object ...values);

	/**
	 * 从左插入对象如果列表存在
	 * @see <a href="http://redis.io/commands/lpushx">Redis Documentation: LPUSHX</a>
	 * @since redis 2.2.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回列表数量
	 */
	Long lpushIfPresent(String key, Object value);

	/**
	 * 从左弹出对象
	 * @see <a href="http://redis.io/commands/lpop">Redis Documentation: LPOP</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	<T> T lpop(String key);


	/**
	 * 从左弹出对象(阻塞)
	 * @see <a href="http://redis.io/commands/blpop">Redis Documentation: BLPOP</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param timeout 超时时间
	 * @param unit 单位
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	<T> T blpop(String key, Long timeout, TimeUnit unit);

	/**
	 * 从右插入对象
	 * @see <a href="http://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回列表数量
	 */
	Long rpush(String key, Object value);

	/**
	 * 从右插入对象
	 * @see <a href="http://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param pivot 中心点对象
	 * @param value 对象
	 * @return 返回列表数量
	 */
	Long rpush(String key, Object pivot, Object value);

	/**
	 * 从右插入对象如果列表存在
	 * @see <a href="http://redis.io/commands/rpushx">Redis Documentation: RPUSHX</a>
	 * @since redis 2.2.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回列表数量
	 */
	Long rpushIfPresent(String key, Object value);

	/**
	 * 从右插入对象
	 * @see <a href="http://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回列表数量
	 */
	Long rpushAll(String key, Object ...value);


	/**
	 * 从右弹出对象
	 * @see <a href="http://redis.io/commands/rpop">Redis Documentation: RPOP</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param <T> 对象类型
	 * @return 返回对象
	 */

	<T> T rpop(String key);

	/**
	 * 从右弹出对象(阻塞)
	 * @see <a href="http://redis.io/commands/brpop">Redis Documentation: BRPOP</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param timeout 超时时间
	 * @param unit 单位
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	<T> T brpop(String key, Long timeout, TimeUnit unit);

	/**
	 * 从左弹出对象并从左插入到另一个列表
	 * @see <a href="http://redis.io/commands/lpop">Redis Documentation: LPOP</a>
	 * @see <a href="http://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param otherKey 键
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	<T> T lpopAndlpush(String key, String otherKey);

	/**
	 * 从右弹出对象并从左插入到另一个列表
	 * @see <a href="http://redis.io/commands/rpoplpush">Redis Documentation: RPOPLPUSH</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param otherKey 键
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	<T> T rpopAndlpush(String key, String otherKey);

	/**
	 * 从右弹出对象并从右插入到另一个列表
	 * @see <a href="http://redis.io/commands/rpop">Redis Documentation: RPOP</a>
	 * @see <a href="http://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param otherKey 键
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	<T> T rpopAndrpush(String key, String otherKey);

	/**
	 * 从左弹出对象并从右插入到另一个列表
	 * @see <a href="http://redis.io/commands/lpop">Redis Documentation: LPOP</a>
	 * @see <a href="http://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param otherKey 键
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	<T> T lpopAndrpush(String key, String otherKey);

	/****************************** LIST END ***********************************/

	/****************************** SET START ***********************************/
	/**
	 * 新增对象
	 * @see <a href="http://redis.io/commands/sadd">Redis Documentation: SADD</a>
	 * @param key 键
	 * @since redis 1.0.0
	 * @param values 对象
	 * @return 返回成功个数
	 */
	Long addSet(String key, Object ...values);

	/**
	 * 弹出对象
	 * @see <a href="http://redis.io/commands/spop">Redis Documentation: SPOP</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	<T> T popSet(String key);

	/**
	 * 弹出对象
	 * @see <a href="http://redis.io/commands/spop">Redis Documentation: SPOP</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param count 对象个数
	 * @return 返回对象列表
	 */
	List popSet(String key, Long count);

	/**
	 * 移除对象
	 * @see <a href="http://redis.io/commands/srem">Redis Documentation: SREM</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param values 对象
	 * @return 返回移除对象数量
	 */
	Long removeSet(String key, Object ...values);

	/**
	 * 移动对象
	 * @see <a href="http://redis.io/commands/smove">Redis Documentation: SMOVE</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param destKey 目标键
	 * @param value 对象
	 * @return 返回布尔值,成功true,失败false
	 */
	Boolean moveSet(String key, String destKey, Object value);

	/**
	 * 获取对象数量
	 * @see <a href="http://redis.io/commands/scard">Redis Documentation: SCARD</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @return 返回对象数量
	 */
	Long sizeSet(String key);

	/**
	 * 是否包含对象
	 * @see <a href="http://redis.io/commands/sismember">Redis Documentation: SISMEMBER</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回布尔值,存在true,不存在false
	 */
	Boolean containsSet(String key, Object value);

	/**
	 * 获取不重复的随机对象
	 * @see <a href="http://redis.io/commands/srandmember">Redis Documentation: SRANDMEMBER</a>
	 * @since redis 2.6.0
	 * @param key 键
	 * @param count 数量
	 * @return 返回不重复的随机对象集合
	 */
	Set distinctRandomMembersSet(String key, Long count);

	/**
	 * 获取可重复的随机对象
	 * @see <a href="http://redis.io/commands/srandmember">Redis Documentation: SRANDMEMBER</a>
	 * @since redis 2.6.0
	 * @param key 键
	 * @param count 数量
	 * @return 返回不重复的随机对象集合
	 */
	List randomMembersSet(String key, Long count);

	/**
	 * 获取可重复的随机对象
	 * @see <a href="http://redis.io/commands/srandmember">Redis Documentation: SRANDMEMBER</a>
	 * @since redis 2.6.0
	 * @param key 键
	 * @param <T> 对象类型
	 * @return 返回可重复的随机对象
	 */
	<T> T randomMemberSet(String key);

	/**
	 * 获取对象集合
	 * @see <a href="http://redis.io/commands/smembers">Redis Documentation: SMEMBERS</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @return 返回对象集合
	 */
	Set membersSet(String key);

	/**
	 * 取对象差集
	 * @see <a href="http://redis.io/commands/sdiff">Redis Documentation: SDIFF</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param otherKys 其他键
	 * @return 返回与其他集合的对象差集
	 */
	Set differenceSet(String key, String ...otherKys);

	/**
	 * 取对象差集并存储到新的集合
	 * @see <a href="http://redis.io/commands/sdiffstore">Redis Documentation: SDIFFSTORE</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param storeKey 存储键
	 * @param otherKys 其他键
	 * @return 返回差集对象个数
	 */
	Long differenceAndStoreSet(String key, String storeKey, String ...otherKys);

	/**
	 * 取对象交集
	 * @see <a href="http://redis.io/commands/sinter">Redis Documentation: SINTER</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param otherKys 其他键
	 * @return 返回与其他集合的对象交集
	 */
	Set intersectSet(String key, String ...otherKys);

	/**
	 * 取对象交集并存储到新的集合
	 * @see <a href="http://redis.io/commands/sinterstore">Redis Documentation: SINTERSTORE</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param storeKey 存储键
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	Long intersectAndStoreSet(String key, String storeKey, String ...otherKys);

	/**
	 * 取对象并集
	 * @see <a href="http://redis.io/commands/sunion">Redis Documentation: SUNION</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param otherKys 其他键
	 * @return 返回与其他集合的对象交集
	 */
	Set unionSet(String key, String ...otherKys);

	/**
	 * 取对象并集并存储到新的集合
	 * @see <a href="http://redis.io/commands/sunionstore">Redis Documentation: SUNIONSTORE</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param storeKey 存储键
	 * @param otherKys 其他键
	 * @return 返回并集对象个数
	 */
	Long unionAndStoreSet(String key, String storeKey, String ...otherKys);

	/**
	 * 匹配对象
	 * @see <a href="http://redis.io/commands/sscan">Redis Documentation: SSCAN</a>
	 * @since redis 2.8.0
	 * @param key 键
	 * @param count 数量
	 * @param pattern 规则
	 * @return 返回匹配对象
	 */
	 Cursor scanSet(String key, Long count, String pattern);

	/****************************** SET END ***********************************/

	/****************************** ZSET START ***********************************/

	/**
	 * 新增对象
	 * @see <a href="http://redis.io/commands/zadd">Redis Documentation: ZADD</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param value 对象
	 * @param score 排序
	 * @return 返回布尔值,成功true,失败false
	 */
	Boolean addZSet(String key, Object value, double score);


	/**
	 * 新增对象存在则更新
	 * @see <a href="http://redis.io/commands/zadd">Redis Documentation: ZADD</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param map 对象字典
	 * @return 返回成功个数
	 */
	Long addZSet(String key, Map<Double, Object> map);

	/**
	 * 新增对象存在则更新
	 * @see <a href="http://redis.io/commands/zadd">Redis Documentation: ZADD</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param values 对象
	 * @return 返回成功个数
	 */
	Long addZSet(String key, Object ...values);

	/**
	 * 获取对象数量
	 * @see <a href="http://redis.io/commands/zcard">Redis Documentation: ZCARD</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @return 返回对象数量
	 */
	Long sizeZSet(String key);

	/**
	 * 获取最小-最大之间分数的对象数量
	 * @see <a href="http://redis.io/commands/zcount">Redis Documentation: ZCOUNT</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param min 最小分数
	 * @param max 最大分数
	 * @return 返回对象数量
	 */
	Long countZSet(String key, Double min, Double max);

	/**
	 * 正序获取范围内的对象
	 * @see <a href="http://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param startIndex 开始索引
	 * @param endIndex 结束索引
	 * @return 返回对象集合
	 */
	Set ascRangeZSet(String key, Long startIndex, Long endIndex);

	/**
	 * 倒序获取范围内的对象
	 * @see <a href="http://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param startSortIndex 起始排序索引
	 * @param endSortIndex 结束排序索引
	 * @return 返回对象集合
	 */
	Set descRangeZSet(String key, Long startSortIndex, Long endSortIndex);

	/**
	 * 正序获取范围内的对象
	 * @see <a href="http://redis.io/commands/zrangebylex">Redis Documentation: ZRANGEBYLEX</a>
	 * @since redis 2.8.9
	 * @param key 键
	 * @param startSortIndex 起始排序索引
	 * @param isContainsStart 是否包含起始
	 * @param endSortIndex 结束排序索引
	 * @param isContainsEnd 是否包含结束
	 * @return 返回对象集合
	 */
	Set ascRangeByLexZSet(String key, Long startSortIndex, boolean isContainsStart, Long endSortIndex, boolean isContainsEnd);

	/**
	 * 正序获取范围内的对象
	 * @see <a href="http://redis.io/commands/zrangebylex">Redis Documentation: ZRANGEBYLEX</a>
	 * @since redis 2.8.9
	 * @param key 键
	 * @param startSortIndex 起始排序索引
	 * @param isContainsStart 是否包含起始
	 * @param endSortIndex 结束排序索引
	 * @param isContainsEnd 是否包含结束
	 * @param count 返回数量
	 * @param offset 偏移量
	 * @return 返回对象集合
	 */
	Set ascRangeByLexZSet(String key, Long startSortIndex, boolean isContainsStart, Long endSortIndex, boolean isContainsEnd, Integer count, Integer offset);

	/**
	 * 获取范围内的对象
	 * @see <a href="http://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
	 * @since redis 1.0.5
	 * @param key 键
	 * @param min 最小分数
	 * @param max 最大分数
	 * @param count 返回数量
	 * @param offset 偏移量
	 * @return 返回对象集合
	 */
	Set rangeByScoreZSet(String key, Double min, Double max, Long count, Long offset);

	/**
	 * 获取范围内的对象(带分数)
	 * @see <a href="http://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param startSortIndex 起始排序索引
	 * @param endSortIndex 结束排序索引
	 * @return 返回对象字典
	 */
	Map<Double, Object> rangeByScoreZSet(String key, Long startSortIndex, Long endSortIndex);

	/**
	 * 获取范围内的对象(带分数)
	 * @see <a href="http://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
	 * @since redis 1.0.5
	 * @param key 键
	 * @param min 最小分数
	 * @param max 最大分数
	 * @return 返回对象字典
	 */
	Map<Double, Object> rangeByScoreWithScoresZSet(String key, Double min, Double max);

	/**
	 * 获取范围内的对象(带分数)
	 * @see <a href="http://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
	 * @since redis 1.0.5
	 * @param key 键
	 * @param min 最小分数
	 * @param max 最大分数
	 * @param count 返回数量
	 * @param offset 偏移量
	 * @return 返回对象字典
	 */
	Map<Double, Object> rangeByScoreWithScoresZSet(String key, Double min, Double max, Long count, Long offset);

	/**
	 * 获取集合对象
	 * @see <a href="http://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @return 返回对象字典
	 */
	Set getAllZSet(String key);


	/**
	 * 获取集合对象(带分数)
	 * @see <a href="http://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @return 返回对象字典
	 */
	Map<Double, Object> getAllByScoreZSet(String key);

	/**
	 * 获取当前对象排序索引
	 * @see <a href="http://redis.io/commands/zrank">Redis Documentation: ZRANK</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回对象排序索引
	 */
	Long sortIndexZSet(String key, Object value);

	/**
	 * 当前对象分数
	 * @see <a href="http://redis.io/commands/zscore">Redis Documentation: ZSCORE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回对象分数
	 */
	Double scoreZSet(String key, Object value);

	/**
	 * 对象分数自增
	 * @see <a href="http://redis.io/commands/zincrby">Redis Documentation: ZINCRBY</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param value 对象
	 * @param score 自增分数
	 * @return 返回对象分数
	 */
	Double incrementScoreZSet(String key, Object value, Double score);

	/**
	 * 移除对象
	 * @see <a href="http://redis.io/commands/zrem">Redis Documentation: ZREM</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param values 对象
	 * @return 返回对象移除数量
	 */
	Long removeZSet(String key, Object ...values);


	/**
	 * 正序移除范围内的对象
	 * @see <a href="http://redis.io/commands/zremrangebyrank">Redis Documentation: ZREMRANGEBYRANK</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param startSortIndex 起始排序索引
	 * @param endSortIndex 结束排序索引
	 * @return 返回对象移除数量
	 */
	Long ascRemoveRangeZSet(String key, Long startSortIndex, Long endSortIndex);

	/**
	 * 倒序移除范围内的对象
	 * @see <a href="http://redis.io/commands/zremrangebyrank">Redis Documentation: ZREMRANGEBYRANK</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param startSortIndex 起始排序索引
	 * @param endSortIndex 结束排序索引
	 * @return 返回对象移除数量
	 */
	Long descRemoveRangeZSet(String key, Long startSortIndex, Long endSortIndex);

	/**
	 * 移除范围内的对象
	 * @see <a href="http://redis.io/commands/zremrangebyscore">Redis Documentation: ZREMRANGEBYSCORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param min 最小分数
	 * @param max 最大分数
	 * @return 返回对象移除数量
	 */
	Long removeRangeByScoreZSet(String key, Double min, Double max);

	/**
	 * 取对象交集并存储到新的集合
	 * @see <a href="http://redis.io/commands/zinterstore">Redis Documentation: ZINTERSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param storeKey 存储键
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	Long intersectAndStoreZSet(String key, String storeKey, String ...otherKys);

	/**
	 * 取对象交集并存储到新的集合
	 * @see <a href="http://redis.io/commands/zinterstore">Redis Documentation: ZINTERSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param storeKey 存储键
	 * @param aggregate 聚合选项
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	Long intersectAndStoreZSet(String key, String storeKey, RedisZSetCommands.Aggregate aggregate, String ...otherKys);

	/**
	 * 取对象交集并存储到新的集合
	 * @see <a href="http://redis.io/commands/zinterstore">Redis Documentation: ZINTERSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param storeKey 存储键
	 * @param aggregate 聚合选项
	 * @param weights 权重选项
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	Long intersectAndStoreZSet(String key, String storeKey, RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights, String ...otherKys);

	/**
	 * 取对象交集(带分数)
	 * @see <a href="http://redis.io/commands/zinterstore">Redis Documentation: ZINTERSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	Map<Double, Object> intersectByScoreZSet(String key, String ...otherKys);
	/**
	 * 取对象交集
	 * @see <a href="http://redis.io/commands/zinterstore">Redis Documentation: ZINTERSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	Set intersectZSet(String key, String ...otherKys);

	/**
	 * 取对象并集并存储到新的集合
	 * @see <a href="http://redis.io/commands/zunionstore">Redis Documentation: ZUNIONSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param storeKey 存储键
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	Long unionAndStoreZSet(String key, String storeKey, String ...otherKys);

	/**
	 * 取对象并集并存储到新的集合
	 * @see <a href="http://redis.io/commands/zunionstore">Redis Documentation: ZUNIONSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param storeKey 存储键
	 * @param aggregate 聚合选项
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	Long unionAndStoreZSet(String key, String storeKey, RedisZSetCommands.Aggregate aggregate, String ...otherKys);

	/**
	 * 取对象并集并存储到新的集合
	 * @see <a href="http://redis.io/commands/zunionstore">Redis Documentation: ZUNIONSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param storeKey 存储键
	 * @param aggregate 聚合选项
	 * @param weights 权重选项
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	Long unionAndStoreZSet(String key, String storeKey, RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights, String ...otherKys);

	/**
	 * 取对象并集(带分数)
	 * @see <a href="http://redis.io/commands/zunionstore">Redis Documentation: ZUNIONSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	Map<Double, Object> unionByScoreZSet(String key, String ...otherKys);


	/**
	 * 取对象并集
	 * @see <a href="http://redis.io/commands/zunionstore">Redis Documentation: ZUNIONSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	Set unionZSet(String key, String ...otherKys);


	/**
	 * 反转当前对象排序索引
	 * @see <a href="http://redis.io/commands/zrevrank">Redis Documentation: ZREVRANK</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回对象排序索引
	 */
	Long reverseSortIndexZSet(String key, Object value);

	/**
	 * 反转范围内的对象
	 * @see <a href="http://redis.io/commands/zrevrange">Redis Documentation: ZREVRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param startIndex 起始排序索引
	 * @param endIndex 结束排序索引
	 * @return 返回对象集合
	 */
	Set reverseRangeZSet(String key, Long startIndex, Long endIndex);

	/**
	 * 反转范围内的对象
	 * @see <a href="http://redis.io/commands/zrevrange">Redis Documentation: ZREVRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param min 最小分数
	 * @param max 最大分数
	 * @return 返回对象集合
	 */
	Set reverseRangeByScoreZSet(String key, Double min, Double max);

	/**
	 * 反转范围内的对象(带分数)
	 * @see <a href="http://redis.io/commands/zrevrange">Redis Documentation: ZREVRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param startIndex 起始排序索引
	 * @param endIndex 结束排序索引
	 * @return 返回对象集合
	 */
	Map<Double, Object> reverseRangeByScoreZSet(String key, Long startIndex, Long endIndex);

	/**
	 * 反转范围内的对象(带分数)
	 * @see <a href="http://redis.io/commands/zrevrangebyscore">Redis Documentation: ZREVRANGEBYSCORE</a>
	 * @since redis 2.2.0
	 * @param key 键
	 * @param min 最小分数
	 * @param max 最大分数
	 * @return 返回对象集合
	 */
	Map<Double, Object> reverseRangeByScoreWithScoresZSet(String key, Double min, Double max);

	/**
	 * 反转范围内的对象(带分数)
	 * @see <a href="http://redis.io/commands/zrevrangebyscore">Redis Documentation: ZREVRANGEBYSCORE</a>
	 * @since redis 2.2.0
	 * @param key 键
	 * @param min 最小分数
	 * @param max 最大分数
	 * @param count 返回数量
	 * @param offset 偏移量
	 * @return 返回对象集合
	 */
	Map<Double, Object> reverseRangeByScoreWithScoresZSet(String key, Double min, Double max, Long count, Long offset);

	/**
	 * 匹配对象
	 * @see <a href="http://redis.io/commands/zscan">Redis Documentation: ZSCAN</a>
	 * @since redis 2.8.0
	 * @param key 键
	 * @param count 数量
	 * @param pattern 规则
	 * @return 返回匹配对象
	 */
	Cursor<ZSetOperations.TypedTuple<Object>> scanZSet(String key, Long count, String pattern);

	/**
	 * 弹出最大分数值对象
	 * @see <a href="http://redis.io/commands/zpopmax">Redis Documentation: ZPOPMAX</a>
	 * @since redis 5.0.0
	 * @param key 键
	 * @return 返回对象
	 */
	Object popMaxZSet(String key);

	/**
	 * 弹出最大分数值对象
	 * @see <a href="http://redis.io/commands/zpopmax">Redis Documentation: ZPOPMAX</a>
	 * @since redis 5.0.0
	 * @param key 键
	 * @param count 弹出数量
	 * @return 返回对象列表
	 */
	Set<ZSetOperations.TypedTuple<Object>> popMaxZSet(String key, int count);

	/**
	 * 弹出最大分数值对象(带分数)
	 * @see <a href="http://redis.io/commands/zpopmax">Redis Documentation: ZPOPMAX</a>
	 * @since redis 5.0.0
	 * @param key 键
	 * @param count 弹出数量
	 * @return 返回对象字典
	 */
	Map<Double, Object> popMaxByScoreZSet(String key, int count);

	/**
	 * 弹出最小分数值对象
	 * @see <a href="http://redis.io/commands/zpopmin">Redis Documentation: ZPOPMIN</a>
	 * @since redis 5.0.0
	 * @param key 键
	 * @return 返回对象
	 */
	Object popMinZSet(String key);

	/**
	 * 弹出最小分数值对象
	 * @see <a href="http://redis.io/commands/zpopmin">Redis Documentation: ZPOPMIN</a>
	 * @since redis 5.0.0
	 * @param key 键
	 * @param count 弹出数量
	 * @return 返回对象列表
	 */
	Set<ZSetOperations.TypedTuple<Object>> popMinZSet(String key, int count);

	/**
	 * 弹出最小分数值对象(带分数)
	 * @see <a href="http://redis.io/commands/zpopmin">Redis Documentation: ZPOPMIN</a>
	 * @since redis 5.0.0
	 * @param key 键
	 * @param count 弹出数量
	 * @return 返回对象字典
	 */
	Map<Double, Object> popMinByScoreZSet(String key, int count);

	/****************************** ZSET END ***********************************/

	/****************************** Geo START ***********************************/
	/**
	 * 添加对象
	 * @see <a href="http://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param point 坐标
	 * @param value 对象
	 * @return 返回总数
	 */
	Long add(String key, Point point, Object value);


	/**
	 * 添加对象
	 * @see <a href="http://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param params 参数，键为待添加对象，值为待添加坐标
	 * @return 返回总数
	 */
	Long add(String key, Map<Object, Point> params);


	/**
	 * 定位对象
	 * @see <a href="http://redis.io/commands/geopos">Redis Documentation: GEOPOS</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param values 对象
	 * @return 返回坐标列表
	 */
	List<Point> position(String key, Object ...values);


	/**
	 * 对象地理位置哈希码
	 * @see <a href="http://redis.io/commands/geohash">Redis Documentation: GEOHASH</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param values 对象
	 * @return 返回对象地理位置哈希码
	 */
	List<String> hash(String key, Object ...values);

	/**
	 * 对象距离
	 * @see <a href="http://redis.io/commands/geodist">Redis Documentation: GEODIST</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param member1 对象1
	 * @param member2 对象2
	 * @return 返回对象间的距离
	 */
	Distance distance(String key, Object member1, Object member2);

	/**
	 * 对象距离
	 * @see <a href="http://redis.io/commands/geodist">Redis Documentation: GEODIST</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param member1 对象1
	 * @param member2 对象2
	 * @param metric 单位
	 * @return 返回对象间的距离
	 */
	Distance distance(String key, Object member1, Object member2, Metric metric);

	/**
	 * 中心范围内的对象
	 * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param value 对象
	 * @param distance 距离
	 * @return 返回包含的对象
	 */
	List<Object> radiusByMember(String key, Object value, Distance distance);

	/**
	 * 中心范围内的对象
	 * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param value 对象
	 * @param radius 半径
	 * @return 返回包含的对象
	 */
	List<Object> radiusByMember(String key, Object value, Double radius);

	/**
	 * 中心范围内的对象
	 * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param value 对象
	 * @param distance 距离
	 * @param args 命令参数
	 * @return 返回包含的对象
	 */
	List<Object> radiusByMember(String key, Object value, Distance distance, RedisGeoCommands.GeoRadiusCommandArgs args);

	/**
	 * 中心范围内的对象
	 * @see <a href="http://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param within 圆
	 * @return 返回包含的对象
	 */
	List<Object> radius(String key, Circle within);

	/**
	 * 中心范围内的对象
	 * @see <a href="http://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param within 圆
	 * @param args 命令参数
	 * @return 返回包含的对象
	 */
	List<Object> radius(String key, Circle within, RedisGeoCommands.GeoRadiusCommandArgs args);

	/**
	 * 中心范围内的对象(带详细信息)
	 * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param value 对象
	 * @param distance 距离
	 * @return 返回包含的对象
	 */
	GeoResults<RedisGeoCommands.GeoLocation<Object>> radiusByMemberWithResult(String key, Object value, Distance distance);

	/**
	 * 中心范围内的对象(带详细信息)
	 * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param value 对象
	 * @param radius 半径
	 * @return 返回包含的对象
	 */
	GeoResults<RedisGeoCommands.GeoLocation<Object>> radiusByMemberWithResult(String key, Object value, Double radius);

	/**
	 * 中心范围内的对象(带详细信息)
	 * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param value 对象
	 * @param distance 距离
	 * @param args 命令参数
	 * @return 返回包含的对象
	 */
	GeoResults<RedisGeoCommands.GeoLocation<Object>> radiusByMemberWithResult(String key, Object value, Distance distance, RedisGeoCommands.GeoRadiusCommandArgs args);

	/**
	 * 中心范围内的对象(带详细信息)
	 * @see <a href="http://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param within 圆
	 * @return 返回包含的对象
	 */
	GeoResults<RedisGeoCommands.GeoLocation<Object>> radiusWithResult(String key, Circle within);

	/**
	 * 中心范围内的对象(带详细信息)
	 * @see <a href="http://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param within 圆
	 * @param args 命令参数
	 * @return 返回包含的对象
	 */
	GeoResults<RedisGeoCommands.GeoLocation<Object>> radiusWithResult(String key, Circle within, RedisGeoCommands.GeoRadiusCommandArgs args);

	/**
	 * 移除对象
	 * @see <a href="http://redis.io/commands/zrem">Redis Documentation: ZREM</a>
	 * @since redis 2.4.0
	 * @param key 键
	 * @param members 对象
	 * @return 返回移除数量
	 */
	Long remove(String key, Object ...members);

	/****************************** Geo END ***********************************/
	/**
	 * 分布式锁
	 *
	 * @param key        分布式锁key
	 * @param expireTime 持有锁的最长时间 (redis过期时间) 秒为单位
	 * @return 返回获取锁状态 成功失败
	 */
	boolean tryLock(String key, int expireTime);

	/**
	 * 分布式锁释放
	 * @param key 分布式锁key
	 */
	void unLock(String key);
	
}
