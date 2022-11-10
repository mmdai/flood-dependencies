package cn.flood.db.redis.wrap;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *  redisTemplate基础操作封装
 */
public class RedisBaseOperation {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void remove(RedisTemplate<String, Object> redisTemplate, String... keys) {
        String[] var2 = keys;
        int var3 = keys.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String key = var2[var4];
            this.remove(redisTemplate, key);
        }
    }

    public void removePattern(RedisTemplate<String, Object> redisTemplate, String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    public boolean exists(RedisTemplate<String, Object> redisTemplate, String key) {
        return redisTemplate.hasKey(key);
    }

    public boolean remove(RedisTemplate<String, Object> redisTemplate, String key) {
        if (this.exists(redisTemplate, key)) {
            return redisTemplate.delete(key);
        }
        return false;
    }

    public <T> T get(RedisTemplate<String, Object> redisTemplate, String key) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        return (T) operations.get(key);
    }

    public <T> boolean set(RedisTemplate<String, Object> redisTemplate, String key, T value) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception var5) {
            var5.printStackTrace();
        }
        return result;
    }

    public <T> boolean set(RedisTemplate<String, Object> redisTemplate, String key, T value, Long expireTime, TimeUnit timeUnit) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            return redisTemplate.expire(key, expireTime, timeUnit);
        } catch (Exception var7) {
            var7.printStackTrace();
        }
        return result;
    }

    public boolean exists(RedisTemplate<String, Object> redisTemplate, String key, String field) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        return hash.hasKey(key, field);
    }

    public boolean remove(RedisTemplate<String, Object> redisTemplate, String key, String field) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        Long result = hash.delete(key, field);
        if(result > 0){
            return true;
        }
        return false;
    }

    public <T> T getHash(RedisTemplate<String, Object> redisTemplate, String key, String field) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        return (T) hash.get(key, field);
    }

    public Map<String, Object> getAllHash(RedisTemplate<String, Object> redisTemplate, String key) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        return hash.entries(key);
    }

    public <T> boolean setHash(RedisTemplate<String, Object> redisTemplate, String key, String field, T value) {
        try {
            HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
            hash.put(key, field, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public <T> boolean setHash(RedisTemplate<String, Object> redisTemplate, String key, String field, T value, Long expireTime, TimeUnit timeUnit) {
        try {
            HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
            hash.put(key, field, value);
            return expire(redisTemplate, key, expireTime, timeUnit);
        } catch (Exception e) {
            log.error("error :{}",e.getMessage());
            return false;
        }
    }


    public boolean setAllHash(RedisTemplate<String, Object> redisTemplate, String key, Map<String, Object> value) {
        try {
            if (value.isEmpty()) {
                return false;
            }
            Set<Map.Entry<String, Object>> entrySet = value.entrySet();
            Map<byte[], byte[]> hashes = new HashMap<>(value.size());
            for (Map.Entry<String, Object> entry : entrySet) {
                hashes.put(entry.getKey().getBytes(), entry.getValue().toString().getBytes());
            }
            return redisTemplate.execute(new RedisCallback<Boolean>() {
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


    public boolean setAllHash(RedisTemplate<String, Object> redisTemplate, String key, Map<String, Object> value, Long expireTime, TimeUnit timeUnit) {
        try {
            if (value.isEmpty()) {
                return false;
            }
            Set<Map.Entry<String, Object>> entrySet = value.entrySet();
            Map<byte[], byte[]> hashes = new HashMap<>(value.size());
            for (Map.Entry<String, Object> entry : entrySet) {
                hashes.put(entry.getKey().getBytes(), entry.getValue().toString().getBytes());
            }
            redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) {
                    connection.hMSet(key.getBytes(), hashes);
                    return null;
                }
            });
            return expire(redisTemplate, key, expireTime, timeUnit);
        } catch (Exception e) {
            return false;
        }
    }


    public Long hIncrBy(RedisTemplate<String, Object> redisTemplate, String hKey, String field, Long value) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        return hash.increment(hKey, field, value);
    }


    public Double hIncrBy(RedisTemplate<String, Object> redisTemplate, String hKey, String field, Double value) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        return hash.increment(hKey, field, value);
    }


    protected boolean expire(RedisTemplate<String, Object> redisTemplate, String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    public <T> boolean setIfPresent(RedisTemplate<String, Object> redisTemplate, String key, T value, Long expireTime, TimeUnit timeUnit) {
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            return operations.setIfPresent(key, value, expireTime, timeUnit);
        } catch (Exception e) {
            log.error("error :{}", e.getMessage());
            return false;
        }
    }

    public long setLPush(RedisTemplate<String, Object> redisTemplate, String lKey, String lValue) {
        return redisTemplate.opsForList().leftPush(lKey, lValue);
    }

    public long setLPush(RedisTemplate<String, Object> redisTemplate, String lKey, String[] lValue) {
        return redisTemplate.opsForList().leftPush(lKey, lValue);
    }

    public long setRPush(RedisTemplate<String, Object> redisTemplate, String lKey, String lValue) {
        return redisTemplate.opsForList().rightPush(lKey, lValue);
    }

    public long setRPush(RedisTemplate<String, Object> redisTemplate, String lKey, String[] lValue) {
        return redisTemplate.opsForList().rightPush(lKey, lValue);
    }

    public long getLLen(RedisTemplate<String, Object> redisTemplate, String lKey) {
        return redisTemplate.opsForList().size(lKey);
    }

    public String getLpop(RedisTemplate<String, Object> redisTemplate, String lKey) {
        return (String) redisTemplate.opsForList().leftPop(lKey);
    }

    public String getRpop(RedisTemplate<String, Object> redisTemplate, String lKey) {
        return (String) redisTemplate.opsForList().rightPop(lKey);
    }

    public long incr(RedisTemplate<String, Object> redisTemplate, String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public long decr(RedisTemplate<String, Object> redisTemplate, String key, long delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }


}
