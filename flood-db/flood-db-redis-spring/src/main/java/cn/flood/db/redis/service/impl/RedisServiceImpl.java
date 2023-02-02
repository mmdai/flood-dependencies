/**  
* <p>Title: RedisServiceImpl.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月8日  
* @version 1.0  
*/  
package cn.flood.db.redis.service.impl;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import cn.flood.db.redis.util.ConvertUtil;
import cn.flood.db.redis.util.RedisLockUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;

import cn.flood.db.redis.service.RedisService;


/**  
* <p>Title: RedisServiceImpl</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2018年12月8日  
*/
@SuppressWarnings("unchecked")
public class RedisServiceImpl implements RedisService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private RedisTemplate<String, Object> redisTemplate;

	private RedisLockUtil redisLockUtil;
	
	@Override
	public RedisTemplate<String, Object> getRedisTemplate() {
		return this.redisTemplate;
	}

	public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate, RedisLockUtil redisLockUtil) {
		this.redisTemplate = redisTemplate;
		this.redisLockUtil = redisLockUtil;
	}

	/* (non-Javadoc)  
	 * <p>Title: remove</p>  
	 * <p>Description: </p>  
	 * @param keys  
	 * @see cn.flood.cache.service.RedisService#remove(java.lang.String[])  
	 */
	@Override
	public void remove(String... keys) {
		String[] var2 = keys;
		int var3 = keys.length;

		for (int var4 = 0; var4 < var3; ++var4) {
			String key = var2[var4];
			this.remove(key);
		}
	}

	/* (non-Javadoc)  
	 * <p>Title: removePattern</p>  
	 * <p>Description: </p>  
	 * @param pattern  
	 * @see cn.flood.cache.service.RedisService#removePattern(java.lang.String)  
	 */
	@Override
	public void removePattern(String pattern) {
		Set<String> keys = this.redisTemplate.keys(pattern);
		if (keys.size() > 0) {
			this.redisTemplate.delete(keys);
		}
	}

	/* (non-Javadoc)  
	 * <p>Title: exists</p>  
	 * <p>Description: </p>  
	 * @param key
	 * @return  
	 * @see cn.flood.cache.service.RedisService#exists(java.lang.String)  
	 */
	@Override
	public boolean exists(String key) {
		return this.redisTemplate.hasKey(key);
	}
	
	/* (non-Javadoc)  
	 * <p>Title: remove</p>  
	 * <p>Description: </p>  
	 * @param key  
	 * @see cn.flood.cache.service.RedisService#remove(java.lang.String)  
	 */
	@Override
	public boolean remove(String key) {
		if (this.exists(key)) {
			return this.redisTemplate.delete(key);
		}
		return false;
	}

	/* (non-Javadoc)  
	 * <p>Title: get</p>  
	 * <p>Description: </p>  
	 * @param key
	 * @return  
	 * @see cn.flood.cache.service.RedisService#get(java.lang.String)  
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key) {
		ValueOperations<String, Object> operations =  this.redisTemplate.opsForValue();
		return (T) operations.get(key);
	}

	/* (non-Javadoc)  
	 * <p>Title: set</p>  
	 * <p>Description: </p>  
	 * @param key
	 * @param value
	 * @return  
	 * @see cn.flood.cache.service.RedisService#set(java.lang.String, java.lang.Object)  
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> boolean set(String key, T value) {
		boolean result = false;
		try {
			ValueOperations<String, Object> operations = this.redisTemplate.opsForValue();
			operations.set(key, value);
			result = true;
		} catch (Exception var5) {
			var5.printStackTrace();
		}
		return result;
	}

	/* (non-Javadoc)  
	 * <p>Title: set</p>  
	 * <p>Description: </p>  
	 * @param key
	 * @param value
	 * @param expireTime
	 * @param timeUnit
	 * @return  
	 * @see cn.flood.cache.service.RedisService#set(java.lang.String, java.lang.Object, java.lang.Long, java.util.concurrent.TimeUnit)  
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> boolean set(String key, T value, Long expireTime, TimeUnit timeUnit) {
		boolean result = false;
		try {
			ValueOperations<String, Object> operations = this.redisTemplate.opsForValue();
			operations.set(key, value);
			return this.redisTemplate.expire(key, expireTime, timeUnit);
		} catch (Exception var7) {
			var7.printStackTrace();
		}
		return result;
	}

	/* (non-Javadoc)  
	 * <p>Title: existsHash</p>  
	 * <p>Description: </p>  
	 * @param key
	 * @param field
	 * @return  
	 * @see cn.flood.cache.service.RedisService#exists(java.lang.String)  
	 */
	@Override
	public boolean exists(String key, String field) {
		HashOperations<String, String, Object> hash = this.redisTemplate.opsForHash();
		return hash.hasKey(key, field);
	}
	
	/* (non-Javadoc)  
	 * <p>Title: removeHash</p>  
	 * <p>Description: </p>  
	 * @param key
	 * @param field
	 * @return  
	 * @see cn.flood.cache.service.RedisService#remove(java.lang.String)  
	 */
	@Override
	public boolean remove(String key, String field) {
		HashOperations<String, String, Object> hash = this.redisTemplate.opsForHash();
		Long result = hash.delete(key, field);
		if(result > 0){
			return true;
		}
		return false;
	}


	/* (non-Javadoc)  
	 * <p>Title: getHash</p>  
	 * <p>Description: </p>  
	 * @param key
	 * @param field
	 * @return  
	 * @see cn.flood.cache.service.RedisService#getHash(java.lang.String, java.lang.String)  
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getHash(String key, String field) {
		HashOperations<String, String, Object> hash = this.redisTemplate.opsForHash();
		return (T) hash.get(key, field);
	}

	/* (non-Javadoc)  
	 * <p>Title: getAllHash</p>  
	 * <p>Description: </p>  
	 * @param key
	 * @return  
	 * @see cn.flood.cache.service.RedisService#getAllHash(java.lang.String)  
	 */
	@Override
	public Map<String, Object> getAllHash(String key) {
		HashOperations<String, String, Object> hash = this.redisTemplate.opsForHash();
		return hash.entries(key);
	}

	/* (non-Javadoc)  
	 * <p>Title: setHash</p>  
	 * <p>Description: </p>  
	 * @param key
	 * @param field
	 * @param value
	 * @return  
	 * @see cn.flood.cache.service.RedisService#setHash(java.lang.String, java.lang.String, java.lang.Object)  
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> boolean setHash(String key, String field, T value) {
		try {
			HashOperations<String, String, Object> hash = this.redisTemplate.opsForHash();
			hash.put(key, field, value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/* (non-Javadoc)  
	 * <p>Title: setHash</p>  
	 * <p>Description: </p>  
	 * @param key
	 * @param field
	 * @param value
	 * @param expireTime
	 * @param timeUnit
	 * @return  
	 * @see cn.flood.cache.service.RedisService#setHash(java.lang.String, java.lang.String, java.lang.Object, java.lang.Long, java.util.concurrent.TimeUnit)  
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> boolean setHash(String key, String field, T value, Long expireTime, TimeUnit timeUnit) {
		try {
			HashOperations<String, String, Object> hash = this.redisTemplate.opsForHash();
			hash.put(key, field, value);
			return expire(key, expireTime, timeUnit);
		} catch (Exception e) {
			log.error("error :{}",e.getMessage());
			return false;
		}
	}

	/* (non-Javadoc)  
	 * <p>Title: setAllHash</p>  
	 * <p>Description: </p>  
	 * @param key
	 * @param value
	 * @return  
	 * @see cn.flood.cache.service.RedisService#setAllHash(java.lang.String, java.util.Map)  
	 */
	@Override
	public boolean setAllHash(String key, Map<String, Object> value) {
		try {
			if (value.isEmpty()) {
				return false;
			}
			Set<Entry<String, Object>> entrySet = value.entrySet();
			Map<byte[], byte[]> hashes = new HashMap<>(value.size());
			for (Entry<String, Object> entry : entrySet) {
				hashes.put(entry.getKey().getBytes(), entry.getValue().toString().getBytes());
			}
			return this.redisTemplate.execute(new RedisCallback<Boolean>() {
				@Override
				public Boolean doInRedis(RedisConnection connection) {
					connection.hMSet(key.getBytes(), hashes);
					return true;
				}
			});
		} catch (Exception e) {
			return false;
		}
	}

	/* (non-Javadoc)  
	 * <p>Title: setAllHash</p>  
	 * <p>Description: </p>  
	 * @param key
	 * @param value
	 * @param expireTime
	 * @param timeUnit
	 * @return  
	 * @see cn.flood.cache.service.RedisService#setAllHash(java.lang.String, java.util.Map, java.lang.Long, java.util.concurrent.TimeUnit)  
	 */
	@Override
	public boolean setAllHash(String key, Map<String, Object> value, Long expireTime, TimeUnit timeUnit) {
		try {
			if (value.isEmpty()) {
				return false;
			}
			Set<Entry<String, Object>> entrySet = value.entrySet();
			Map<byte[], byte[]> hashes = new HashMap<>(value.size());
			for (Entry<String, Object> entry : entrySet) {
				hashes.put(entry.getKey().getBytes(), entry.getValue().toString().getBytes());
			}
			this.redisTemplate.execute(new RedisCallback<Object>() {
				@Override
				public Object doInRedis(RedisConnection connection) {
					connection.hMSet(key.getBytes(), hashes);
					return null;
				}
			});
			return expire(key, expireTime, timeUnit);
		} catch (Exception e) {
			return false;
		}
	}
	
	/* (non-Javadoc)  
	 * <p>Title: hIncrementLong</p>
	 * <p>Description: </p>  
	 * @param hKey
	 * @param field
	 * @param value
	 * @return  
	 * @see cn.flood.cache.service.RedisService#hIncrBy(java.lang.String, java.lang.String, java.lang.Long)  
	 */
	@Override
	public Long hIncrementLong(String hKey, String field, Long value) {
		HashOperations<String, String, Object> hash = this.redisTemplate.opsForHash();
		return hash.increment(hKey, field, value);
	}

	/* (non-Javadoc)  
	 * <p>Title: hIncrementDouble</p>
	 * <p>Description: </p>  
	 * @param hKey
	 * @param field
	 * @param value
	 * @return  
	 * @see cn.flood.cache.service.RedisService#hIncrBy(java.lang.String, java.lang.String, java.lang.Double)  
	 */
	@Override
	public Double hIncrementDouble(String hKey, String field, Double value) {
		HashOperations<String, String, Object> hash = this.redisTemplate.opsForHash();
		return hash.increment(hKey, field, value);
	}

	/**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
	protected boolean expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
            	this.redisTemplate.expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
	 * 
	 * <p>Title: getRedisSerializer</p>  
	 * <p>Description: 获取redis模板 </p>  
	 * @return
	 */
	protected RedisSerializer<String> getRedisSerializer() {
        RedisSerializer<String> redisSerializer = this.redisTemplate.getStringSerializer();
        return redisSerializer;
    }


	/* (non-Javadoc)  
	 * <p>Title: existAndUpdtime</p>  
	 * <p>Description: </p>  
	 * @param key
	 * @param expireTime
	 * @param timeUnit
	 * @return  
	 * @see cn.flood.redis.service.RedisService#existAndUpdtime(java.lang.String, java.lang.Long, java.util.concurrent.TimeUnit)  
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> boolean setIfPresent(String key, T value, Long expireTime, TimeUnit timeUnit) {
		try {
			ValueOperations<String, Object> operations = this.redisTemplate.opsForValue();
			return operations.setIfPresent(key, value, expireTime, timeUnit);
		} catch (Exception e) {
			log.error("error :{}", e.getMessage());
			return false;
		}
	}

	/**
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public Long incrementLong(String key, Long value) {
		ValueOperations<String, Object> operations = this.redisTemplate.opsForValue();
		return operations.increment(key, value);
	}

	/**
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public Double incrementDouble(String key, Double value) {
		ValueOperations<String, Object> operations = this.redisTemplate.opsForValue();
		return operations.increment(key, value);
	}

	/**
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public Long decrementLong(String key, Long value) {
		ValueOperations<String, Object> operations = this.redisTemplate.opsForValue();
		return operations.decrement(key, value);
	}

	/****************************** LIST START ***********************************/
	/**
	 * 获取对象列表数量
	 * @see <a href="http://redis.io/commands/llen">Redis Documentation: LLEN</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @return 返回列表数量
	 */
	@Override
	public Long size(String key){
		return this.redisTemplate.opsForList().size(key);
	}

	/**
	 * 获取所有对象
	 * @see <a href="http://redis.io/commands/lrange">Redis Documentation: LRANGE</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @return 返回对象列表
	 */
	@Override
	public List getAll(String key){
		return this.lrange(key, 0L, -1L);
	}


	/**
	 * 从左获取范围内的对象
	 * @see <a href="http://redis.io/commands/lrange">Redis Documentation: LRANGE</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param startIndex 起始索引
	 * @param endIndex 结束索引
	 * @return 返回对象列表
	 */
	@Override
	public List lrange(String key, Long startIndex, Long endIndex){
		return this.redisTemplate.opsForList().range(key, startIndex, endIndex);
	}

	/**
	 * 从右获取范围内的对象
	 * @see <a href="http://redis.io/commands/lrange">Redis Documentation: LRANGE</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param startIndex 起始索引
	 * @param endIndex 结束索引
	 * @return 返回对象列表
	 */
	@Override
	public List rrange(String key, Long startIndex, Long endIndex){
		List list = this.lrange(key, -endIndex-1, -startIndex-1);
		Collections.reverse(list);
		return list;
	}

	/**
	 * 从左移除对象
	 * @see <a href="http://redis.io/commands/lrem">Redis Documentation: LREM</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param count 个数
	 * @param value 对象
	 * @return 返回移除数量
	 */
	@Override
	public Long lremove(String key, Long count, Object value){
		return this.redisTemplate.opsForList().remove(key, count, value);
	}

	/**
	 * 从右移除对象
	 * @see <a href="http://redis.io/commands/lrem">Redis Documentation: LREM</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param count 个数
	 * @param value 对象
	 * @return 返回移除数量
	 */
	@Override
	public Long rremove(String key, Long count, Object value){
		return this.lremove(key, -count, value);
	}

	/**
	 * 从左截取对象(会修改redis中列表)
	 * @see <a href="http://redis.io/commands/ltrim">Redis Documentation: LTRIM</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param startIndex 起始索引
	 * @param endIndex 结束索引
	 * @return 返回截取的对象列表
	 */
	@Override
	public List lsubList(String key, Long startIndex, Long endIndex){
		this.redisTemplate.opsForList().trim(key, startIndex, endIndex);
		return this.lrange(key, startIndex, this.size(key));
	}

	/**
	 * 从右截取对象(会修改redis中列表)
	 * @see <a href="http://redis.io/commands/ltrim">Redis Documentation: LTRIM</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param startIndex 起始索引
	 * @param endIndex 结束索引
	 * @return 返回截取的对象列表
	 */
	@Override
	public List rsubList(String key, Long startIndex, Long endIndex){
		this.redisTemplate.opsForList().trim(key, -endIndex-1, -startIndex-1);
		int length = this.size(key).intValue();
		List<Object> list = new ArrayList<>(length);
		String temp = String.format("%s_temp", key);
		for (int i = 0; i < length; i++) {
			list.add(this.rpopAndrpush(key, temp));
		}
		this.redisTemplate.rename(temp, key);
		return list;
	}

	/**
	 * 从左修改指定索引的对象
	 * @see <a href="http://redis.io/commands/ltrim">Redis Documentation: LTRIM</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param index 索引
	 * @param value 对象
	 */
	@Override
	public void lset(String key, Long index, Object value){
		this.redisTemplate.opsForList().set(key, index, value);
	}

	/**
	 * 从右修改指定索引的对象
	 * @see <a href="http://redis.io/commands/ltrim">Redis Documentation: LTRIM</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param index 索引
	 * @param value 对象
	 */
	@Override
	public void rset(String key, Long index, Object value){
		this.redisTemplate.opsForList().set(key, -index-1, value);
	}

	/**
	 * 从左获取对象
	 * @see <a href="http://redis.io/commands/lindex">Redis Documentation: LINDEX</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param index 索引
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T lget(String key, Long index){
		return (T) this.redisTemplate.opsForList().index(key, index);
	}


	/**
	 * 从右获取对象
	 * @see <a href="http://redis.io/commands/lindex">Redis Documentation: LINDEX</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param index 索引
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T rget(String key, Long index){
		return  (T) this.redisTemplate.opsForList().index(key, -index-1);
	}

	/**
	 * 从左插入对象
	 * @see <a href="http://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回列表数量
	 */
	@Override
	public Long lpush(String key, Object value){
		return this.redisTemplate.opsForList().leftPush(key, value);
	}

	/**
	 * 按照中心点从左插入对象
	 * @see <a href="http://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param pivot 中心点对象
	 * @param value 对象
	 * @return 返回列表数量
	 */
	@Override
	public Long lpush(String key, Object pivot, Object value){
		return this.redisTemplate.opsForList().leftPush(key, pivot, value);
	}

	/**
	 * 从左插入多个对象
	 * @see <a href="http://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param values 对象
	 * @return 返回列表数量
	 */
	@Override
	public Long lpushAll(String key, Object ...values){
		return this.redisTemplate.opsForList().leftPushAll(key, values);
	}

	/**
	 * 从左插入对象如果列表存在
	 * @see <a href="http://redis.io/commands/lpushx">Redis Documentation: LPUSHX</a>
	 * @since redis 2.2.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回列表数量
	 */
	@Override
	public Long lpushIfPresent(String key, Object value){
		return this.redisTemplate.opsForList().leftPushIfPresent(key, value);
	}

	/**
	 * 从左弹出对象
	 * @see <a href="http://redis.io/commands/lpop">Redis Documentation: LPOP</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T lpop(String key){
		return (T) this.redisTemplate.opsForList().leftPop(key);
	}


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
	@SuppressWarnings("unchecked")
	@Override
	public <T> T blpop(String key, Long timeout, TimeUnit unit){
		return (T) this.redisTemplate.opsForList().leftPop(key, timeout, unit);
	}

	/**
	 * 从右插入对象
	 * @see <a href="http://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回列表数量
	 */
	@Override
	public Long rpush(String key, Object value){
		return this.redisTemplate.opsForList().rightPush(key, value);
	}

	/**
	 * 从右插入对象
	 * @see <a href="http://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param pivot 中心点对象
	 * @param value 对象
	 * @return 返回列表数量
	 */
	@Override
	public Long rpush(String key, Object pivot, Object value){
		return this.redisTemplate.opsForList().rightPush(key, pivot, value);
	}

	/**
	 * 从右插入对象如果列表存在
	 * @see <a href="http://redis.io/commands/rpushx">Redis Documentation: RPUSHX</a>
	 * @since redis 2.2.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回列表数量
	 */
	@Override
	public Long rpushIfPresent(String key, Object value){
		return this.redisTemplate.opsForList().rightPushIfPresent(key, value);
	}

	/**
	 * 从右插入对象
	 * @see <a href="http://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回列表数量
	 */
	@Override
	public Long rpushAll(String key, Object ...value){
		return this.redisTemplate.opsForList().rightPushAll(key, value);
	}

	/**
	 * 从右弹出对象
	 * @see <a href="http://redis.io/commands/rpop">Redis Documentation: RPOP</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T rpop(String key){
		return (T) this.redisTemplate.opsForList().rightPop(key);
	}

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
	@SuppressWarnings("unchecked")
	@Override
	public <T> T brpop(String key, Long timeout, TimeUnit unit){
		return (T) this.redisTemplate.opsForList().rightPop(key, timeout, unit);
	}

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
	@SuppressWarnings("unchecked")
	@Override
	public <T> T lpopAndlpush(String key, String otherKey){
		T t = this.lpop(key);
		this.lpush(otherKey, t);
		return t;
	}

	/**
	 * 从右弹出对象并从左插入到另一个列表
	 * @see <a href="http://redis.io/commands/rpoplpush">Redis Documentation: RPOPLPUSH</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param otherKey 键
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T rpopAndlpush(String key, String otherKey){
		return (T) this.redisTemplate.opsForList().rightPopAndLeftPush(key, otherKey);
	}

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
	@SuppressWarnings("unchecked")
	@Override
	public <T> T rpopAndrpush(String key, String otherKey){
		T t = this.rpop(key);
		this.rpush(otherKey, t);
		return t;
	}

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
	@SuppressWarnings("unchecked")
	@Override
	public <T> T lpopAndrpush(String key, String otherKey){
		T t = this.lpop(key);
		this.rpush(otherKey, t);
		return t;
	}

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
	@Override
	public Long addSet(String key, Object ...values) {
		return this.redisTemplate.opsForSet().add(key, values);
	}

	/**
	 * 弹出对象
	 * @see <a href="http://redis.io/commands/spop">Redis Documentation: SPOP</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param <T> 对象类型
	 * @return 返回对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T popSet(String key) {
		return (T) this.redisTemplate.opsForSet().pop(key);
	}

	/**
	 * 弹出对象
	 * @see <a href="http://redis.io/commands/spop">Redis Documentation: SPOP</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param count 对象个数
	 * @return 返回对象列表
	 */
	@Override
	public List popSet(String key, Long count) {
		return this.redisTemplate.opsForSet().pop(key, count);
	}


	/**
	 * 移除对象
	 * @see <a href="http://redis.io/commands/srem">Redis Documentation: SREM</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param values 对象
	 * @return 返回移除对象数量
	 */
	@Override
	public Long removeSet(String key, Object ...values) {
		return this.redisTemplate.opsForSet().remove(key, values);
	}

	/**
	 * 移动对象
	 * @see <a href="http://redis.io/commands/smove">Redis Documentation: SMOVE</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param destKey 目标键
	 * @param value 对象
	 * @return 返回布尔值,成功true,失败false
	 */
	@Override
	public Boolean moveSet(String key, String destKey, Object value) {
		return this.redisTemplate.opsForSet().move(key, value, destKey);
	}

	/**
	 * 获取对象数量
	 * @see <a href="http://redis.io/commands/scard">Redis Documentation: SCARD</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @return 返回对象数量
	 */
	@Override
	public Long sizeSet(String key) {
		return this.redisTemplate.opsForSet().size(key);
	}

	/**
	 * 是否包含对象
	 * @see <a href="http://redis.io/commands/sismember">Redis Documentation: SISMEMBER</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回布尔值,存在true,不存在false
	 */
	@Override
	public Boolean containsSet(String key, Object value) {
		return this.redisTemplate.opsForSet().isMember(key, value);
	}

	/**
	 * 获取不重复的随机对象
	 * @see <a href="http://redis.io/commands/srandmember">Redis Documentation: SRANDMEMBER</a>
	 * @since redis 2.6.0
	 * @param key 键
	 * @param count 数量
	 * @return 返回不重复的随机对象集合
	 */
	@Override
	public Set distinctRandomMembersSet(String key, Long count) {
		return this.redisTemplate.opsForSet().distinctRandomMembers(key, count);
	}

	/**
	 * 获取可重复的随机对象
	 * @see <a href="http://redis.io/commands/srandmember">Redis Documentation: SRANDMEMBER</a>
	 * @since redis 2.6.0
	 * @param key 键
	 * @param count 数量
	 * @return 返回不重复的随机对象集合
	 */
	@Override
	public List randomMembersSet(String key, Long count) {
		return this.redisTemplate.opsForSet().randomMembers(key, count);
	}

	/**
	 * 获取可重复的随机对象
	 * @see <a href="http://redis.io/commands/srandmember">Redis Documentation: SRANDMEMBER</a>
	 * @since redis 2.6.0
	 * @param key 键
	 * @param <T> 对象类型
	 * @return 返回可重复的随机对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T randomMemberSet(String key) {
		return (T) this.redisTemplate.opsForSet().randomMember(key);
	}

	/**
	 * 获取对象集合
	 * @see <a href="http://redis.io/commands/smembers">Redis Documentation: SMEMBERS</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @return 返回对象集合
	 */
	@Override
	public Set membersSet(String key) {
		return this.redisTemplate.opsForSet().members(key);
	}

	/**
	 * 取对象差集
	 * @see <a href="http://redis.io/commands/sdiff">Redis Documentation: SDIFF</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param otherKys 其他键
	 * @return 返回与其他集合的对象差集
	 */
	@Override
	public Set differenceSet(String key, String ...otherKys) {
		return this.redisTemplate.opsForSet().difference(key, Arrays.asList(otherKys));
	}

	/**
	 * 取对象差集并存储到新的集合
	 * @see <a href="http://redis.io/commands/sdiffstore">Redis Documentation: SDIFFSTORE</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param storeKey 存储键
	 * @param otherKys 其他键
	 * @return 返回差集对象个数
	 */
	@Override
	public Long differenceAndStoreSet(String key, String storeKey, String ...otherKys) {
		return this.redisTemplate.opsForSet().differenceAndStore(key, Arrays.asList(otherKys), storeKey);
	}

	/**
	 * 取对象交集
	 * @see <a href="http://redis.io/commands/sinter">Redis Documentation: SINTER</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param otherKys 其他键
	 * @return 返回与其他集合的对象交集
	 */
	@Override
	public Set intersectSet(String key, String ...otherKys) {
		return this.redisTemplate.opsForSet().intersect(key, Arrays.asList(otherKys));
	}

	/**
	 * 取对象交集并存储到新的集合
	 * @see <a href="http://redis.io/commands/sinterstore">Redis Documentation: SINTERSTORE</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param storeKey 存储键
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	@Override
	public Long intersectAndStoreSet(String key, String storeKey, String ...otherKys) {
		return this.redisTemplate.opsForSet().intersectAndStore(key, Arrays.asList(otherKys), storeKey);
	}

	/**
	 * 取对象并集
	 * @see <a href="http://redis.io/commands/sunion">Redis Documentation: SUNION</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param otherKys 其他键
	 * @return 返回与其他集合的对象交集
	 */
	@Override
	public Set unionSet(String key, String ...otherKys) {
		return this.redisTemplate.opsForSet().union(key, Arrays.asList(otherKys));
	}

	/**
	 * 取对象并集并存储到新的集合
	 * @see <a href="http://redis.io/commands/sunionstore">Redis Documentation: SUNIONSTORE</a>
	 * @since redis 1.0.0
	 * @param key 键
	 * @param storeKey 存储键
	 * @param otherKys 其他键
	 * @return 返回并集对象个数
	 */
	@Override
	public Long unionAndStoreSet(String key, String storeKey, String ...otherKys) {
		return this.redisTemplate.opsForSet().unionAndStore(key, Arrays.asList(otherKys), storeKey);
	}

	/**
	 * 匹配对象
	 * @see <a href="http://redis.io/commands/sscan">Redis Documentation: SSCAN</a>
	 * @since redis 2.8.0
	 * @param key 键
	 * @param count 数量
	 * @param pattern 规则
	 * @return 返回匹配对象
	 */
	@Override
	public Cursor scanSet(String key, Long count, String pattern) {
		return this.redisTemplate.opsForSet().scan(key, ScanOptions.scanOptions().count(count).match(pattern).build());
	}
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
	@Override
	public Boolean addZSet(String key, Object value, double score){
		return this.redisTemplate.opsForZSet().add(key, value, score);
	}


	/**
	 * 新增对象存在则更新
	 * @see <a href="http://redis.io/commands/zadd">Redis Documentation: ZADD</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param map 对象字典
	 * @return 返回成功个数
	 */
	@Override
	public Long addZSet(String key, Map<Double, Object> map){
		return this.redisTemplate.opsForZSet().add(key, ConvertUtil.toTypedTupleSet(map));
	}

	/**
	 * 新增对象存在则更新
	 * @see <a href="http://redis.io/commands/zadd">Redis Documentation: ZADD</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param values 对象
	 * @return 返回成功个数
	 */
	@Override
	public Long addZSet(String key, Object ...values){
		return this.addZSet(key, ConvertUtil.toMap(values));
	}

	/**
	 * 获取对象数量
	 * @see <a href="http://redis.io/commands/zcard">Redis Documentation: ZCARD</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @return 返回对象数量
	 */
	@Override
	public Long sizeZSet(String key){
		return this.redisTemplate.opsForZSet().zCard(key);
	}

	/**
	 * 获取最小-最大之间分数的对象数量
	 * @see <a href="http://redis.io/commands/zcount">Redis Documentation: ZCOUNT</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param min 最小分数
	 * @param max 最大分数
	 * @return 返回对象数量
	 */
	@Override
	public Long countZSet(String key, Double min, Double max){
		return this.redisTemplate.opsForZSet().count(key, min, max);
	}

	/**
	 * 正序获取范围内的对象
	 * @see <a href="http://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param startIndex 开始索引
	 * @param endIndex 结束索引
	 * @return 返回对象集合
	 */
	@Override
	public Set ascRangeZSet(String key, Long startIndex, Long endIndex){
		return this.redisTemplate.opsForZSet().range(key, startIndex, endIndex);
	}

	/**
	 * 倒序获取范围内的对象
	 * @see <a href="http://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param startSortIndex 起始排序索引
	 * @param endSortIndex 结束排序索引
	 * @return 返回对象集合
	 */
	@Override
	public Set descRangeZSet(String key, Long startSortIndex, Long endSortIndex){
		return this.ascRangeZSet(key, -endSortIndex-1, startSortIndex-1);
	}

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
	@Override
	public Set ascRangeByLexZSet(String key, Long startSortIndex, boolean isContainsStart, Long endSortIndex, boolean isContainsEnd){
		return this.redisTemplate.opsForZSet().rangeByLex(
				key,
				this.getRange(startSortIndex, isContainsStart, endSortIndex, isContainsEnd)
		);
	}

	/**
	 * 获取范围对象
	 * @param startSortIndex 起始排序索引
	 * @param isContainsStart 是否包含起始
	 * @param endSortIndex 结束排序索引
	 * @param isContainsEnd 是否包含结束
	 * @return 返回范围对象
	 */
	private RedisZSetCommands.Range getRange(Long startSortIndex, boolean isContainsStart, Long endSortIndex, boolean isContainsEnd) {
		RedisZSetCommands.Range range = RedisZSetCommands.Range.range();
		if (isContainsStart){
			range.gte(startSortIndex);
		}else{
			range.gt(startSortIndex);
		}
		if (isContainsEnd){
			range.lte(endSortIndex);
		}else{
			range.lt(endSortIndex);
		}
		return range;
	}

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
	@Override
	public Set ascRangeByLexZSet(String key, Long startSortIndex, boolean isContainsStart, Long endSortIndex, boolean isContainsEnd, Integer count, Integer offset){
		return this.redisTemplate.opsForZSet().rangeByLex(
				key,
				this.getRange(startSortIndex, isContainsStart, endSortIndex, isContainsEnd)
		);
	}

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
	@Override
	public Set rangeByScoreZSet(String key, Double min, Double max, Long count, Long offset){
		return this.redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
	}

	/**
	 * 获取范围内的对象(带分数)
	 * @see <a href="http://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param startSortIndex 起始排序索引
	 * @param endSortIndex 结束排序索引
	 * @return 返回对象字典
	 */
	@Override
	public Map<Double, Object> rangeByScoreZSet(String key, Long startSortIndex, Long endSortIndex){
		return ConvertUtil.toMap(this.redisTemplate.opsForZSet().rangeWithScores(key, startSortIndex, endSortIndex));
	}

	/**
	 * 获取范围内的对象(带分数)
	 * @see <a href="http://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
	 * @since redis 1.0.5
	 * @param key 键
	 * @param min 最小分数
	 * @param max 最大分数
	 * @return 返回对象字典
	 */
	@Override
	public Map<Double, Object> rangeByScoreWithScoresZSet(String key, Double min, Double max){
		return ConvertUtil.toMap(this.redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max));
	}

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
	@Override
	public Map<Double, Object> rangeByScoreWithScoresZSet(String key, Double min, Double max, Long count, Long offset){
		return ConvertUtil.toMap(this.redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, offset, count));
	}

	/**
	 * 获取集合对象
	 * @see <a href="http://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @return 返回对象字典
	 */
	@Override
	public Set getAllZSet(String key){
		return this.ascRangeZSet(key, 0L, -1L);
	}


	/**
	 * 获取集合对象(带分数)
	 * @see <a href="http://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @return 返回对象字典
	 */
	@Override
	public Map<Double, Object> getAllByScoreZSet(String key){
		return this.rangeByScoreZSet(key, 0L, -1L);
	}

	/**
	 * 获取当前对象排序索引
	 * @see <a href="http://redis.io/commands/zrank">Redis Documentation: ZRANK</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回对象排序索引
	 */
	@Override
	public Long sortIndexZSet(String key, Object value){
		return this.redisTemplate.opsForZSet().rank(key, value);
	}

	/**
	 * 当前对象分数
	 * @see <a href="http://redis.io/commands/zscore">Redis Documentation: ZSCORE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回对象分数
	 */
	@Override
	public Double scoreZSet(String key, Object value){
		return this.redisTemplate.opsForZSet().score(key, value);
	}

	/**
	 * 对象分数自增
	 * @see <a href="http://redis.io/commands/zincrby">Redis Documentation: ZINCRBY</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param value 对象
	 * @param score 自增分数
	 * @return 返回对象分数
	 */
	@Override
	public Double incrementScoreZSet(String key, Object value, Double score){
		return this.redisTemplate.opsForZSet().incrementScore(key, value, score);
	}

	/**
	 * 移除对象
	 * @see <a href="http://redis.io/commands/zrem">Redis Documentation: ZREM</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param values 对象
	 * @return 返回对象移除数量
	 */
	@Override
	public Long removeZSet(String key, Object ...values){
		return this.redisTemplate.opsForZSet().remove(key, values);
	}


	/**
	 * 正序移除范围内的对象
	 * @see <a href="http://redis.io/commands/zremrangebyrank">Redis Documentation: ZREMRANGEBYRANK</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param startSortIndex 起始排序索引
	 * @param endSortIndex 结束排序索引
	 * @return 返回对象移除数量
	 */
	@Override
	public Long ascRemoveRangeZSet(String key, Long startSortIndex, Long endSortIndex){
		return this.redisTemplate.opsForZSet().removeRange(key, startSortIndex, endSortIndex);
	}

	/**
	 * 倒序移除范围内的对象
	 * @see <a href="http://redis.io/commands/zremrangebyrank">Redis Documentation: ZREMRANGEBYRANK</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param startSortIndex 起始排序索引
	 * @param endSortIndex 结束排序索引
	 * @return 返回对象移除数量
	 */
	@Override
	public Long descRemoveRangeZSet(String key, Long startSortIndex, Long endSortIndex){
		return this.ascRemoveRangeZSet(key, -endSortIndex-1, -startSortIndex-1);
	}

	/**
	 * 移除范围内的对象
	 * @see <a href="http://redis.io/commands/zremrangebyscore">Redis Documentation: ZREMRANGEBYSCORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param min 最小分数
	 * @param max 最大分数
	 * @return 返回对象移除数量
	 */
	@Override
	public Long removeRangeByScoreZSet(String key, Double min, Double max){
		return this.redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
	}

	/**
	 * 取对象交集并存储到新的集合
	 * @see <a href="http://redis.io/commands/zinterstore">Redis Documentation: ZINTERSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param storeKey 存储键
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	@Override
	public Long intersectAndStoreZSet(String key, String storeKey, String ...otherKys){
		return this.redisTemplate.opsForZSet().intersectAndStore(key, Arrays.asList(otherKys), storeKey);
	}

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
	@Override
	public Long intersectAndStoreZSet(String key, String storeKey, RedisZSetCommands.Aggregate aggregate, String ...otherKys){
		return this.redisTemplate.opsForZSet().intersectAndStore(key, Arrays.asList(otherKys), storeKey, aggregate);
	}

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
	@Override
	public Long intersectAndStoreZSet(String key, String storeKey, RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights, String ...otherKys){
		return this.redisTemplate.opsForZSet().intersectAndStore(key, Arrays.asList(otherKys), storeKey, aggregate, weights);
	}

	/**
	 * 取对象交集(带分数)
	 * @see <a href="http://redis.io/commands/zinterstore">Redis Documentation: ZINTERSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	@Override
	public Map<Double, Object> intersectByScoreZSet(String key, String ...otherKys){
		String tempKey = UUID.randomUUID().toString();
		this.intersectAndStoreZSet(key, tempKey, otherKys);
		Map<Double, Object> map = this.getAllByScoreZSet(tempKey);
		this.redisTemplate.delete(tempKey);
		return map;
	}
	/**
	 * 取对象交集
	 * @see <a href="http://redis.io/commands/zinterstore">Redis Documentation: ZINTERSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	@Override
	public Set intersectZSet(String key, String ...otherKys){
		String tempKey = UUID.randomUUID().toString();
		this.intersectAndStoreZSet(key, tempKey, otherKys);
		Set set = this.getAllZSet(tempKey);
		this.redisTemplate.delete(tempKey);
		return set;
	}

	/**
	 * 取对象并集并存储到新的集合
	 * @see <a href="http://redis.io/commands/zunionstore">Redis Documentation: ZUNIONSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param storeKey 存储键
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	@Override
	public Long unionAndStoreZSet(String key, String storeKey, String ...otherKys){
		return this.redisTemplate.opsForZSet().unionAndStore(key, Arrays.asList(otherKys), storeKey);
	}

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
	@Override
	public Long unionAndStoreZSet(String key, String storeKey, RedisZSetCommands.Aggregate aggregate, String ...otherKys){
		return this.redisTemplate.opsForZSet().unionAndStore(key, Arrays.asList(otherKys), storeKey);
	}

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
	@Override
	public Long unionAndStoreZSet(String key, String storeKey, RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights, String ...otherKys){
		return this.redisTemplate.opsForZSet().unionAndStore(key, Arrays.asList(otherKys), storeKey, aggregate);
	}

	/**
	 * 取对象并集(带分数)
	 * @see <a href="http://redis.io/commands/zunionstore">Redis Documentation: ZUNIONSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	@Override
	public Map<Double, Object> unionByScoreZSet(String key, String ...otherKys){
		String tempKey = UUID.randomUUID().toString();
		this.unionAndStoreZSet(key, tempKey, otherKys);
		Map<Double, Object> map = this.getAllByScoreZSet(tempKey);
		this.redisTemplate.delete(tempKey);
		return map;
	}


	/**
	 * 取对象并集
	 * @see <a href="http://redis.io/commands/zunionstore">Redis Documentation: ZUNIONSTORE</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param otherKys 其他键
	 * @return 返回交集对象个数
	 */
	@Override
	public Set unionZSet(String key, String ...otherKys){
		String tempKey = UUID.randomUUID().toString();
		this.unionAndStoreZSet(key, tempKey, otherKys);
		Set set = this.getAllZSet(tempKey);
		this.redisTemplate.delete(tempKey);
		return set;
	}


	/**
	 * 反转当前对象排序索引
	 * @see <a href="http://redis.io/commands/zrevrank">Redis Documentation: ZREVRANK</a>
	 * @since redis 2.0.0
	 * @param key 键
	 * @param value 对象
	 * @return 返回对象排序索引
	 */
	@Override
	public Long reverseSortIndexZSet(String key, Object value){
		return this.redisTemplate.opsForZSet().reverseRank(key, value);
	}

	/**
	 * 反转范围内的对象
	 * @see <a href="http://redis.io/commands/zrevrange">Redis Documentation: ZREVRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param startIndex 起始排序索引
	 * @param endIndex 结束排序索引
	 * @return 返回对象集合
	 */
	@Override
	public Set reverseRangeZSet(String key, Long startIndex, Long endIndex){
		return this.redisTemplate.opsForZSet().reverseRange(key, startIndex, endIndex);
	}

	/**
	 * 反转范围内的对象
	 * @see <a href="http://redis.io/commands/zrevrange">Redis Documentation: ZREVRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param min 最小分数
	 * @param max 最大分数
	 * @return 返回对象集合
	 */
	@Override
	public Set reverseRangeByScoreZSet(String key, Double min, Double max){
		return this.redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
	}

	/**
	 * 反转范围内的对象(带分数)
	 * @see <a href="http://redis.io/commands/zrevrange">Redis Documentation: ZREVRANGE</a>
	 * @since redis 1.2.0
	 * @param key 键
	 * @param startIndex 起始排序索引
	 * @param endIndex 结束排序索引
	 * @return 返回对象集合
	 */
	@Override
	public Map<Double, Object> reverseRangeByScoreZSet(String key, Long startIndex, Long endIndex){
		return ConvertUtil.toMap(this.redisTemplate.opsForZSet().reverseRangeWithScores(key, startIndex, endIndex));
	}

	/**
	 * 反转范围内的对象(带分数)
	 * @see <a href="http://redis.io/commands/zrevrangebyscore">Redis Documentation: ZREVRANGEBYSCORE</a>
	 * @since redis 2.2.0
	 * @param key 键
	 * @param min 最小分数
	 * @param max 最大分数
	 * @return 返回对象集合
	 */
	@Override
	public Map<Double, Object> reverseRangeByScoreWithScoresZSet(String key, Double min, Double max){
		return ConvertUtil.toMap(this.redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max));
	}

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
	@Override
	public Map<Double, Object> reverseRangeByScoreWithScoresZSet(String key, Double min, Double max, Long count, Long offset){
		return ConvertUtil.toMap(this.redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max, offset, count));
	}

	/**
	 * 匹配对象
	 * @see <a href="http://redis.io/commands/zscan">Redis Documentation: ZSCAN</a>
	 * @since redis 2.8.0
	 * @param key 键
	 * @param count 数量
	 * @param pattern 规则
	 * @return 返回匹配对象
	 */
	@Override
	public Cursor<ZSetOperations.TypedTuple<Object>> scanZSet(String key, Long count, String pattern){
		return this.redisTemplate.opsForZSet().scan(key, ScanOptions.scanOptions().count(count).match(pattern).build());
	}

	/**
	 * 弹出最大分数值对象
	 * @see <a href="http://redis.io/commands/zpopmax">Redis Documentation: ZPOPMAX</a>
	 * @since redis 5.0.0
	 * @param key 键
	 * @return 返回对象
	 */
	@Override
	public Object popMaxZSet(String key){
		return this.redisTemplate.opsForZSet().popMax(key).getValue();
	}

	/**
	 * 弹出最大分数值对象
	 * @see <a href="http://redis.io/commands/zpopmax">Redis Documentation: ZPOPMAX</a>
	 * @since redis 5.0.0
	 * @param key 键
	 * @param count 弹出数量
	 * @return 返回对象列表
	 */
	@Override
	public Set<ZSetOperations.TypedTuple<Object>> popMaxZSet(String key, int count){
		return this.redisTemplate.opsForZSet().popMax(key, count);
	}

	/**
	 * 弹出最大分数值对象(带分数)
	 * @see <a href="http://redis.io/commands/zpopmax">Redis Documentation: ZPOPMAX</a>
	 * @since redis 5.0.0
	 * @param key 键
	 * @param count 弹出数量
	 * @return 返回对象字典
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<Double, Object> popMaxByScoreZSet(String key, int count){
		return ConvertUtil.toObjectMap(this.redisTemplate.opsForZSet().popMax(key, count));
	}

	/**
	 * 弹出最小分数值对象
	 * @see <a href="http://redis.io/commands/zpopmin">Redis Documentation: ZPOPMIN</a>
	 * @since redis 5.0.0
	 * @param key 键
	 * @return 返回对象
	 */
	@Override
	public Object popMinZSet(String key){
		return this.redisTemplate.opsForZSet().popMin(key).getValue();
	}

	/**
	 * 弹出最小分数值对象
	 * @see <a href="http://redis.io/commands/zpopmin">Redis Documentation: ZPOPMIN</a>
	 * @since redis 5.0.0
	 * @param key 键
	 * @param count 弹出数量
	 * @return 返回对象列表
	 */
	@Override
	public Set<ZSetOperations.TypedTuple<Object>> popMinZSet(String key, int count){
		return this.redisTemplate.opsForZSet().popMin(key, count);
	}

	/**
	 * 弹出最小分数值对象(带分数)
	 * @see <a href="http://redis.io/commands/zpopmin">Redis Documentation: ZPOPMIN</a>
	 * @since redis 5.0.0
	 * @param key 键
	 * @param count 弹出数量
	 * @return 返回对象字典
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<Double, Object> popMinByScoreZSet(String key, int count){
		return ConvertUtil.toObjectMap(this.redisTemplate.opsForZSet().popMin(key, count));
	}

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
	@Override
	public Long add(String key, Point point, Object value) {
		return this.redisTemplate.opsForGeo().add(key, point, value);
	}


	/**
	 * 添加对象
	 * @see <a href="http://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param params 参数，键为待添加对象，值为待添加坐标
	 * @return 返回总数
	 */
	@Override
	public Long add(String key, Map<Object, Point> params) {
		return this.redisTemplate.opsForGeo().add(key, params);
	}


	/**
	 * 定位对象
	 * @see <a href="http://redis.io/commands/geopos">Redis Documentation: GEOPOS</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param values 对象
	 * @return 返回坐标列表
	 */
	@Override
	public List<Point> position(String key, Object ...values) {
		return this.redisTemplate.opsForGeo().position(key, values);
	}


	/**
	 * 对象地理位置哈希码
	 * @see <a href="http://redis.io/commands/geohash">Redis Documentation: GEOHASH</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param values 对象
	 * @return 返回对象地理位置哈希码
	 */
	@Override
	public List<String> hash(String key, Object ...values) {
		if (values==null||values.length==0) {
			return new ArrayList<>();
		}
		return this.redisTemplate.opsForGeo().hash(key, values);
	}

	/**
	 * 对象距离
	 * @see <a href="http://redis.io/commands/geodist">Redis Documentation: GEODIST</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param member1 对象1
	 * @param member2 对象2
	 * @return 返回对象间的距离
	 */
	@Override
	public Distance distance(String key, Object member1, Object member2) {
		return this.redisTemplate.opsForGeo().distance(key, member1, member2);
	}


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
	@Override
	public Distance distance(String key, Object member1, Object member2, Metric metric) {
		return this.redisTemplate.opsForGeo().distance(key, member1, member2, metric);
	}

	/**
	 * 中心范围内的对象
	 * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param value 对象
	 * @param distance 距离
	 * @return 返回包含的对象
	 */
	@Override
	public List<Object> radiusByMember(String key, Object value, Distance distance) {
		GeoResults<RedisGeoCommands.GeoLocation<Object>> results = this.redisTemplate.opsForGeo().radius(key, value, distance);
		return ConvertUtil.toList(results);
	}

	/**
	 * 中心范围内的对象
	 * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param value 对象
	 * @param radius 半径
	 * @return 返回包含的对象
	 */
	@Override
	public List<Object> radiusByMember(String key, Object value, Double radius) {
		GeoResults<RedisGeoCommands.GeoLocation<Object>> results = this.redisTemplate.opsForGeo().radius(key, value, radius);
		return ConvertUtil.toList(results);
	}

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
	@Override
	public List<Object> radiusByMember(String key, Object value, Distance distance, RedisGeoCommands.GeoRadiusCommandArgs args) {
		GeoResults<RedisGeoCommands.GeoLocation<Object>> results = this.redisTemplate.opsForGeo().radius(key, value, distance, args);
		return ConvertUtil.toList(results);
	}

	/**
	 * 中心范围内的对象
	 * @see <a href="http://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param within 圆
	 * @return 返回包含的对象
	 */
	@Override
	public List<Object> radius(String key, Circle within) {
		GeoResults<RedisGeoCommands.GeoLocation<Object>> results = this.redisTemplate.opsForGeo().radius(key, within);
		return ConvertUtil.toList(results);
	}

	/**
	 * 中心范围内的对象
	 * @see <a href="http://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param within 圆
	 * @param args 命令参数
	 * @return 返回包含的对象
	 */
	@Override
	public List<Object> radius(String key, Circle within, RedisGeoCommands.GeoRadiusCommandArgs args) {
		GeoResults<RedisGeoCommands.GeoLocation<Object>> results = this.redisTemplate.opsForGeo().radius(key, within, args);
		return ConvertUtil.toList(results);
	}

	/**
	 * 中心范围内的对象(带详细信息)
	 * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param value 对象
	 * @param distance 距离
	 * @return 返回包含的对象
	 */
	@Override
	public GeoResults<RedisGeoCommands.GeoLocation<Object>> radiusByMemberWithResult(String key, Object value, Distance distance) {
		return this.redisTemplate.opsForGeo().radius(key, value, distance);
	}

	/**
	 * 中心范围内的对象(带详细信息)
	 * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param value 对象
	 * @param radius 半径
	 * @return 返回包含的对象
	 */
	@Override
	public GeoResults<RedisGeoCommands.GeoLocation<Object>> radiusByMemberWithResult(String key, Object value, Double radius) {
		return this.redisTemplate.opsForGeo().radius(key, value, radius);
	}

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
	@Override
	public GeoResults<RedisGeoCommands.GeoLocation<Object>> radiusByMemberWithResult(String key, Object value, Distance distance, RedisGeoCommands.GeoRadiusCommandArgs args) {
		return this.redisTemplate.opsForGeo().radius(key, value, distance, args);
	}

	/**
	 * 中心范围内的对象(带详细信息)
	 * @see <a href="http://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param within 圆
	 * @return 返回包含的对象
	 */
	@Override
	public GeoResults<RedisGeoCommands.GeoLocation<Object>> radiusWithResult(String key, Circle within) {
		return this.redisTemplate.opsForGeo().radius(key, within);
	}

	/**
	 * 中心范围内的对象(带详细信息)
	 * @see <a href="http://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
	 * @since redis 3.2.0
	 * @param key 键
	 * @param within 圆
	 * @param args 命令参数
	 * @return 返回包含的对象
	 */
	@Override
	public GeoResults<RedisGeoCommands.GeoLocation<Object>> radiusWithResult(String key, Circle within, RedisGeoCommands.GeoRadiusCommandArgs args) {
		return this.redisTemplate.opsForGeo().radius(key, within, args);
	}

	/**
	 * 移除对象
	 * @see <a href="http://redis.io/commands/zrem">Redis Documentation: ZREM</a>
	 * @since redis 2.4.0
	 * @param key 键
	 * @param members 对象
	 * @return 返回移除数量
	 */
	@Override
	public Long remove(String key, Object ...members) {
		return this.redisTemplate.opsForGeo().remove(key, (Object[]) members);
	}

	/****************************** Geo END ***********************************/

	/**
	 * 分布式锁
	 *
	 * @param key        分布式锁key
	 * @param expireTime 持有锁的最长时间 (redis过期时间) 秒为单位
	 * @return 返回获取锁状态 成功失败
	 */
	@Override
	public boolean tryLock(String key, int expireTime) {
		return redisLockUtil.tryLock(key, "", expireTime);
	}

	@Override
	public void unLock(String key) {
		redisLockUtil.releaseLock(key, "");
	}


}
