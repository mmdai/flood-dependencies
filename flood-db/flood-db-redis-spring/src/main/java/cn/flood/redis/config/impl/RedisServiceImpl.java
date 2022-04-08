/**  
* <p>Title: RedisServiceImpl.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月8日  
* @version 1.0  
*/  
package cn.flood.redis.config.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;

import cn.flood.redis.config.RedisService;


/**  
* <p>Title: RedisServiceImpl</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2018年12月8日  
*/
public class RedisServiceImpl implements RedisService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private RedisTemplate<String, Object> redisTemplate;
	
	@Override
	public RedisTemplate<String, Object> getRedisTemplate() {
		return this.redisTemplate;
	}

	public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate ;
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
//			return this.redisTemplate.execute(connection -> {
//				connection.hMSet(key.getBytes(), hashes);
//				return true;
//			});
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
//			this.redisTemplate.execute(connection -> {
//				connection.hMSet(key.getBytes(), hashes);
//				return null;
//			});
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
	 * <p>Title: hIncrBy</p>  
	 * <p>Description: </p>  
	 * @param hKey
	 * @param field
	 * @param value
	 * @return  
	 * @see cn.flood.cache.service.RedisService#hIncrBy(java.lang.String, java.lang.String, java.lang.Long)  
	 */
	@Override
	public Long hIncrBy(String hKey, String field, Long value) {
		HashOperations<String, String, Object> hash = this.redisTemplate.opsForHash();
		return hash.increment(hKey, field, value);
	}

	/* (non-Javadoc)  
	 * <p>Title: hIncrBy</p>  
	 * <p>Description: </p>  
	 * @param hKey
	 * @param field
	 * @param value
	 * @return  
	 * @see cn.flood.cache.service.RedisService#hIncrBy(java.lang.String, java.lang.String, java.lang.Double)  
	 */
	@Override
	public Double hIncrBy(String hKey, String field, Double value) {
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

	@Override
	public long setLPush(String lKey, String lValue) {
		return this.redisTemplate.opsForList().leftPush(lKey, lValue);
	}

	@Override
	public long setLPush(String lKey, String[] lValue) {
		return this.redisTemplate.opsForList().leftPush(lKey, lValue);
	}

	@Override
	public long setRPush(String lKey, String lValue) {
		return this.redisTemplate.opsForList().rightPush(lKey, lValue);
	}

	@Override
	public long setRPush(String lKey, String[] lValue) {
		return this.redisTemplate.opsForList().rightPush(lKey, lValue);
	}

	@Override
	public long getLLen(String lKey) {
		return this.redisTemplate.opsForList().size(lKey);
	}

	@Override
	public String getLpop(String lKey) {
		return (String) this.redisTemplate.opsForList().leftPop(lKey);
	}

	@Override
	public String getRpop(String lKey) {
		return (String) this.redisTemplate.opsForList().rightPop(lKey);
	}

	@Override
	public long incr(String key, long delta) {
		return this.redisTemplate.opsForValue().increment(key, delta);
	}

	@Override
	public long decr(String key, long delta) {
		return this.redisTemplate.opsForValue().increment(key, -delta);
	}
	

}
