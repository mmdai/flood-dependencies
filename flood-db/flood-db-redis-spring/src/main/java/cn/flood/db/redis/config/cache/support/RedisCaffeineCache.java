package cn.flood.db.redis.config.cache.support;

import cn.flood.db.redis.config.cache.CacheRedisCaffeineProperties;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;

import com.github.benmanes.caffeine.cache.Cache;


/**
 * 
* <p>Title: RedisCaffeineCache</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2018年12月5日
 */
public class RedisCaffeineCache extends AbstractValueAdaptingCache {
	
	private final Logger logger = LoggerFactory.getLogger(RedisCaffeineCache.class);

	private String name;
	
	@SuppressWarnings("rawtypes")
	private RedisTemplate redisTemplate;

	private Cache<Object, Object> caffeineCache;

	private String cachePrefix;

	private long defaultExpiration = 0;

	private Map<String, Long> expires;
	
	private String topic = "cache:redis:caffeine:topic";
	
	private Map<String, ReentrantLock> keyLockMap = new ConcurrentHashMap<String, ReentrantLock>();
	
	protected RedisCaffeineCache(boolean allowNullValues) {
		super(allowNullValues);
	}

	public RedisCaffeineCache(String name, RedisTemplate<? extends Object, ? extends Object> redisTemplate,
			Cache<Object, Object> caffeineCache, CacheRedisCaffeineProperties cacheRedisCaffeineProperties) {
		super(cacheRedisCaffeineProperties.isCacheNullValues());
		this.name = name;
		this.redisTemplate = redisTemplate;
		this.caffeineCache = caffeineCache;
		this.cachePrefix = cacheRedisCaffeineProperties.getCachePrefix();
		this.defaultExpiration = cacheRedisCaffeineProperties.getRedis().getDefaultExpiration();
		this.expires = cacheRedisCaffeineProperties.getRedis().getExpires();
		this.topic = cacheRedisCaffeineProperties.getRedis().getTopic();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Object getNativeCache() {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		Object value = lookup(key);
		if(value != null) {
			return (T) value;
		}
		ReentrantLock lock = keyLockMap.get(key.toString());
		if(lock == null) {
			logger.debug("create lock for key : {}", key);
			lock = new ReentrantLock();
			keyLockMap.putIfAbsent(key.toString(), lock);
		}
		try {
			lock.lock();
			value = lookup(key);
			if(value != null) {
				return (T) value;
			}
			value = valueLoader.call();
			Object storeValue = toStoreValue(value);
			put(key, storeValue);
			return (T) value;
		} catch (Exception e) {
			throw new ValueRetrievalException(key, valueLoader, e.getCause());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void put(Object key, Object value) {
		if (!super.isAllowNullValues() && value == null) {
			this.evict(key);
            return;
        }
		long expire = getExpire();
		if(expire > 0) {
			redisTemplate.opsForValue().set(getKey(key), toStoreValue(value), expire, TimeUnit.MILLISECONDS);
		} else {
			redisTemplate.opsForValue().set(getKey(key), toStoreValue(value));
		}
		
		push(new CacheMessage(this.name, key));
		
		caffeineCache.put(key, value);
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		Object cacheKey = getKey(key);
		Object prevValue = null;
		// 考虑使用分布式锁，或者将redis的setIfAbsent改为原子性操作
		synchronized (key) {
			prevValue = redisTemplate.opsForValue().get(cacheKey);
			if(prevValue == null) {
				long expire = getExpire();
				if(expire > 0) {
					redisTemplate.opsForValue().setIfAbsent(getKey(key), toStoreValue(value), expire, TimeUnit.MINUTES);
				} else {
					redisTemplate.opsForValue().setIfAbsent(getKey(key), toStoreValue(value));
				}
				
				push(new CacheMessage(this.name, key));
				
				caffeineCache.put(key, toStoreValue(value));
			}
		}
		return toValueWrapper(prevValue);
	}

	@Override
	public void evict(Object key) {
		// 先清除redis中缓存数据，然后清除caffeine中的缓存，避免短时间内如果先清除caffeine缓存后其他请求会再从redis里加载到caffeine中
		redisTemplate.delete(getKey(key));
		
		push(new CacheMessage(this.name, key));
		
		caffeineCache.invalidate(key);
	}

	@Override
	public void clear() {
		// 先清除redis中缓存数据，然后清除caffeine中的缓存，避免短时间内如果先清除caffeine缓存后其他请求会再从redis里加载到caffeine中
		Set<Object> keys = redisTemplate.keys(this.name.concat(":*"));
		for(Object key : keys) {
			redisTemplate.delete(key);
		}
		
		push(new CacheMessage(this.name, null));
		
		caffeineCache.invalidateAll();
	}

	@Override
	protected Object lookup(Object key) {
		Object cacheKey = getKey(key);
		Object value = caffeineCache.getIfPresent(key);
		if(value != null) {
			logger.debug("get cache from caffeine, the key is : {}", cacheKey);
			return value;
		}
		
		value = redisTemplate.opsForValue().get(cacheKey);
		
		if(value != null) {
			logger.debug("get cache from redis and put in caffeine, the key is : {}", cacheKey);
			caffeineCache.put(key, value);
		}
		return value;
	}

	private Object getKey(Object key) {
		return this.name.concat(":").concat(
				ObjectUtils.isEmpty(cachePrefix) ? key.toString() : cachePrefix.concat(":").concat(key.toString()));
	}
	
	private long getExpire() {
		long expire = defaultExpiration;
		Long cacheNameExpire = expires.get(this.name);
		return cacheNameExpire == null ? expire : cacheNameExpire.longValue();
	}
	
	/**
	 * 
	 * <p>Title: push</p>  
	 * <p>Description: 缓存变更时通知其他节点清理本地缓存</p>  
	 * @param message
	 */
	private void push(CacheMessage message) {
		redisTemplate.convertAndSend(topic, message);
	}
	
	/**
	 * 
	 * <p>Title: clearLocal</p>  
	 * <p>Description: 清理本地缓存</p>  
	 * @param key
	 */
	public void clearLocal(Object key) {
		logger.debug("clear local cache, the key is : {}", key);
		if(key == null) {
			caffeineCache.invalidateAll();
		} else {
			caffeineCache.invalidate(key);
		}
	}
}
