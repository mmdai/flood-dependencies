package cn.flood.db.redis.handler;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicDouble;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 数字助手
* @author daimm
 * @date 2019/6/3
 * @since 1.8
 */
@SuppressWarnings("unchecked")
public final class NumberHandler implements RedisHandler {
    /**
     * 对象模板
     */
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 字符串模板
     */
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 对象模板
     */
    private ValueOperations<String, Object> operations;
    /**
     * 对象哈希模板
     */
    private HashOperations<String, String, Object> hashOperations;
    /**
     * 数据库索引
     */
    private int dbIndex;

    /**
     * 数字助手构造
     * @param dbIndex 数据库索引
     */
    NumberHandler(Integer dbIndex) {
        this.dbIndex = dbIndex;
        List<RedisTemplate> templateList = HandlerManager.createTemplate(dbIndex);
        this.redisTemplate = templateList.get(0);
        this.stringRedisTemplate = (StringRedisTemplate) templateList.get(1);
        this.operations = redisTemplate.opsForValue();
        this.hashOperations = redisTemplate.opsForHash();
    }

    /**
     * 数字助手构造
     * @param transactionHandler 事务助手
     */
    NumberHandler(TransactionHandler transactionHandler) {
        this.dbIndex = transactionHandler.getDbIndex();
        List<RedisTemplate> templateList = HandlerManager.createTemplate(dbIndex);
        this.redisTemplate = templateList.get(0);
        this.stringRedisTemplate = (StringRedisTemplate) templateList.get(1);
        this.operations = redisTemplate.opsForValue();
        this.hashOperations = redisTemplate.opsForHash();
    }

    /**
     * 获取原子浮点数实例
     * @param key 键
     * @return 返回原子浮点数实例
     */
    public RedisAtomicDouble getAtomicDouble(String key) {
        return new RedisAtomicDouble(key, HandlerManager.getConnectionFactory(this.dbIndex));
    }

    /**
     * 获取原子长整数实例
     * @param key 键
     * @return 返回原子长整数实例
     */
    public RedisAtomicLong getAtomicLong(String key) {
        return new RedisAtomicLong(key, HandlerManager.getConnectionFactory(this.dbIndex));
    }

    /**
     * 获取原子整数实例
     * @param key 键
     * @return 返回原子整数实例
     */
    public RedisAtomicInteger getAtomicInteger(String key) {
        return new RedisAtomicInteger(key, HandlerManager.getConnectionFactory(this.dbIndex));
    }

    /**
     * 设置浮点数
     * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param value 值
     */
    public void setDouble(String key, double value) {
        this.operations.set(key, value);
    }

    /**
     * 设置浮点数
     * @see <a href="http://redis.io/commands/hset">Redis Documentation: HSET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @param value 值
     */
    public void setDouble(String key, String hashKey, double value) {
        this.hashOperations.put(key, hashKey, value);
    }

    /**
     * 设置浮点数(若存在则更新过期时间)
     * @see <a href="http://redis.io/commands/setex">Redis Documentation: SETEX</a>
     * @since redis 2.0.0
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void setDouble(String key, double value, long timeout, TimeUnit unit) {
        this.operations.set(key, value, timeout, unit);
    }

    /**
     * 设置浮点数如果不存在
     * @see <a href="http://redis.io/commands/setnx">Redis Documentation: SETNX</a>
     * @since redis 1.0.0
     * @param key 键
     * @param value 浮点数
     * @return 返回布尔值,成功true,失败false
     */
    public Boolean setDoubleIfAbsent(String key, double value) {
        return this.operations.setIfAbsent(key, value);
    }

    /**
     * 设置浮点数如果不存在
     * @see <a href="http://redis.io/commands/hsetnx">Redis Documentation: HSETNX</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @param value 浮点数
     * @return 返回布尔值,成功true,失败false
     */
    public Boolean setDoubleIfAbsent(String key, String hashKey, double value) {
        return this.hashOperations.putIfAbsent(key, hashKey, value);
    }

    /**
     * 设置浮点数并设置过期时间如果不存在
     * @see <a href="http://redis.io/commands/setnx">Redis Documentation: SETNX</a>
     * @since redis 2.6.12
     * @param key 键
     * @param value 浮点数
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 返回布尔值,成功true,失败false
     */
    public Boolean setDoubleIfAbsent(String key, double value, long timeout, TimeUnit unit) {
        return this.operations.setIfAbsent(key, value, timeout, unit);
    }

    /**
     * 获取浮点数
     * @see <a href="http://redis.io/commands/get">Redis Documentation: GET</a>
     * @since redis 1.0.0
     * @param key 键
     * @return 返回浮点数
     */
    public Double getDouble(String key) {
        Object value = this.operations.get(key);
        if (value!=null) {
            return Double.valueOf(value.toString());
        }
        return null;
    }

    /**
     * 获取浮点数
     * @see <a href="http://redis.io/commands/hget">Redis Documentation: HGET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @return 返回浮点数
     */
    public Double getDouble(String key, String hashKey) {
        Object value = this.hashOperations.get(key, hashKey);
        if (value!=null) {
            return Double.valueOf(value.toString());
        }
        return null;
    }

    /**
     * 获取并设置浮点数
     * @see <a href="http://redis.io/commands/getset">Redis Documentation: GETSET</a>
     * @since redis 1.0.0
     * @param key 键
     * @param newValue 新值
     * @return 返回原值
     */
    public Double getAndSetDouble(String key, double newValue) {
        Object value = this.operations.getAndSet(key, newValue);
        if (value!=null) {
            return Double.valueOf(value.toString());
        }
        return null;
    }

    /**
     * 获取并设置浮点数
     * @see <a href="http://redis.io/commands/hget">Redis Documentation: HGET</a>
     * @see <a href="http://redis.io/commands/hset">Redis Documentation: HSET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @param newValue 新值
     * @return 返回原值
     */
    public Double getAndSetDouble(String key, String hashKey, double newValue) {
        Double value = this.getDouble(key, hashKey);
        this.setDouble(key, hashKey, newValue);
        return value;
    }

    /**
     * 增加浮点数
     * @see <a href="http://redis.io/commands/incrbyfloat">Redis Documentation: INCRBYFLOAT</a>
     * @since redis 2.6.0
     * @param key 键
     * @param data 步长
     * @return 返回增加后的值
     */
    public Double addDouble(String key, double data) {
        return this.operations.increment(key, data);
    }

    /**
     * 增加浮点数
     * @see <a href="http://redis.io/commands/get">Redis Documentation: GET</a>
     * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param data 步长
     * @return 返回增加后的值
     */
    public synchronized Double addDoubleBySync(String key, double data) {
        Double old = this.getDouble(key);
        double value = new BigDecimal(old==null?"0":Double.toString(old)).add(new BigDecimal(Double.toString(data))).doubleValue();
        this.setDouble(key, value);
        return value;
    }

    /**
     * 增加浮点数
     * @see <a href="http://redis.io/commands/hincrbyfloat">Redis Documentation: HINCRBYFLOAT</a>
     * @since redis 2.6.0
     * @param key 键
     * @param hashKey hash键
     * @param data 步长
     * @return 返回增加后的值
     */
    public Double addDouble(String key, String hashKey, double data) {
        return this.hashOperations.increment(key, hashKey, data);
    }

    /**
     * 增加浮点数
     * @see <a href="http://redis.io/commands/hget">Redis Documentation: HGET</a>
     * @see <a href="http://redis.io/commands/hset">Redis Documentation: HSET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @param data 步长
     * @return 返回增加后的值
     */
    public synchronized Double addDoubleBySync(String key, String hashKey, double data) {
        Double old = this.getDouble(key, hashKey);
        double value = new BigDecimal(old==null?"0":Double.toString(old)).add(new BigDecimal(Double.toString(data))).doubleValue();
        this.setDouble(key, hashKey, value);
        return value;
    }

    /**
     * 获取并增加浮点数
     * @see <a href="http://redis.io/commands/incrbyfloat">Redis Documentation: INCRBYFLOAT</a>
     * @since redis 2.6.0
     * @param key 键
     * @param data 步长
     * @return 返回原值
     */
    public Double getAndAddDouble(String key, double data) {
        return new BigDecimal(Double.toString(this.addDouble(key, data))).subtract(new BigDecimal(Double.toString(data))).doubleValue();
    }

    /**
     * 获取并增加浮点数
     * @see <a href="http://redis.io/commands/get">Redis Documentation: GET</a>
     * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param data 步长
     * @return 返回原值
     */
    public synchronized Double getAndAddDoubleBySync(String key, double data) {
        return new BigDecimal(Double.toString(this.addDoubleBySync(key, data))).subtract(new BigDecimal(Double.toString(data))).doubleValue();
    }

    /**
     * 获取并增加浮点数
     * @see <a href="http://redis.io/commands/hincrbyfloat">Redis Documentation: HINCRBYFLOAT</a>
     * @since redis 2.6.0
     * @param key 键
     * @param hashKey hash键
     * @param data 步长
     * @return 返回原值
     */
    public Double getAndAddDouble(String key, String hashKey, double data) {
        return new BigDecimal(Double.toString(this.addDouble(key, hashKey, data))).subtract(new BigDecimal(Double.toString(data))).doubleValue();
    }

    /**
     * 获取并增加浮点数
     * @see <a href="http://redis.io/commands/hget">Redis Documentation: HGET</a>
     * @see <a href="http://redis.io/commands/hset">Redis Documentation: HSET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @param data 步长
     * @return 返回原值
     */
    public synchronized Double getAndAddDoubleBySync(String key, String hashKey, double data) {
        return new BigDecimal(Double.toString(this.addDoubleBySync(key, hashKey, data))).subtract(new BigDecimal(Double.toString(data))).doubleValue();
    }

    /**
     * 浮点数减小
     * @see <a href="http://redis.io/commands/incrbyfloat">Redis Documentation: INCRBYFLOAT</a>
     * @since redis 2.6.0
     * @param key 键
     * @param data 步长
     * @return 返回相减后的值
     */
    public Double subtractDouble(String key, double data) {
        return this.addDouble(key, -data);
    }

    /**
     * 浮点数减小
     * @see <a href="http://redis.io/commands/get">Redis Documentation: GET</a>
     * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param data 步长
     * @return 返回相减后的值
     */
    public synchronized Double subtractDoubleBySync(String key, double data) {
        return this.addDoubleBySync(key, -data);
    }

    /**
     * 浮点数减小
     * @see <a href="http://redis.io/commands/hincrbyfloat">Redis Documentation: HINCRBYFLOAT</a>
     * @since redis 2.6.0
     * @param key 键
     * @param hashKey hash键
     * @param data 步长
     * @return 返回相减后的值
     */
    public Double subtractDouble(String key, String hashKey, double data) {
        return this.addDouble(key, hashKey, -data);
    }

    /**
     * 浮点数减小
     * @see <a href="http://redis.io/commands/hget">Redis Documentation: HGET</a>
     * @see <a href="http://redis.io/commands/hset">Redis Documentation: HSET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @param data 步长
     * @return 返回相减后的值
     */
    public synchronized Double subtractDoubleBySync(String key, String hashKey, double data) {
        return this.addDoubleBySync(key, hashKey, -data);
    }

    /**
     * 获取并减小浮点数
     * @see <a href="http://redis.io/commands/incrbyfloat">Redis Documentation: INCRBYFLOAT</a>
     * @since redis 2.6.0
     * @param key 键
     * @param data 步长
     * @return 返回原值
     */
    public Double getAndSubtractDouble(String key, double data) {
        return new BigDecimal(Double.toString(this.subtractDouble(key, data))).add(new BigDecimal(Double.toString(data))).doubleValue();
    }

    /**
     * 获取并减小浮点数
     * @see <a href="http://redis.io/commands/get">Redis Documentation: GET</a>
     * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param data 步长
     * @return 返回原值
     */
    public synchronized Double getAndSubtractDoubleBySync(String key, double data) {
        return new BigDecimal(Double.toString(this.subtractDoubleBySync(key, data))).add(new BigDecimal(Double.toString(data))).doubleValue();
    }

    /**
     * 获取并减小浮点数
     * @see <a href="http://redis.io/commands/hincrbyfloat">Redis Documentation: HINCRBYFLOAT</a>
     * @since redis 2.6.0
     * @param key 键
     * @param hashKey hash键
     * @param data 步长
     * @return 返回原值
     */
    public Double getAndSubtractDouble(String key, String hashKey, double data) {
        return new BigDecimal(Double.toString(this.subtractDouble(key, hashKey, data))).add(new BigDecimal(Double.toString(data))).doubleValue();
    }

    /**
     * 获取并减小浮点数
     * @see <a href="http://redis.io/commands/hget">Redis Documentation: HGET</a>
     * @see <a href="http://redis.io/commands/hset">Redis Documentation: HSET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @param data 步长
     * @return 返回原值
     */
    public synchronized Double getAndSubtractDoubleBySync(String key, String hashKey, double data) {
        return new BigDecimal(Double.toString(this.subtractDoubleBySync(key, hashKey, data))).add(new BigDecimal(Double.toString(data))).doubleValue();
    }

    /**
     * 设置长整数
     * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param value 值
     */
    public void setLong(String key, long value) {
        this.operations.set(key, value);
    }

    /**
     * 设置长整数
     * @see <a href="http://redis.io/commands/hset">Redis Documentation: HSET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @param value 值
     */
    public void setLong(String key, String hashKey, long value) {
        this.hashOperations.put(key, hashKey, value);
    }

    /**
     * 设置长整数(若存在则更新过期时间)
     * @see <a href="http://redis.io/commands/setex">Redis Documentation: SETEX</a>
     * @since redis 2.0.0
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void setLong(String key, long value, long timeout, TimeUnit unit) {
        this.operations.set(key, value, timeout, unit);
    }

    /**
     * 设置长整数如果不存在
     * @see <a href="http://redis.io/commands/setnx">Redis Documentation: SETNX</a>
     * @since redis 1.0.0
     * @param key 键
     * @param value 长整数
     * @return 返回布尔值,成功true,失败false
     */
    public Boolean setLongIfAbsent(String key, long value) {
        return this.operations.setIfAbsent(key, value);
    }

    /**
     * 设置长整数如果不存在
     * @see <a href="http://redis.io/commands/hsetnx">Redis Documentation: HSETNX</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @param value 长整数
     * @return 返回布尔值,成功true,失败false
     */
    public Boolean setLongIfAbsent(String key, String hashKey, long value) {
        return this.hashOperations.putIfAbsent(key, hashKey, value);
    }

    /**
     * 设置长整数并设置过期时间如果不存在
     * @see <a href="http://redis.io/commands/setnx">Redis Documentation: SETNX</a>
     * @since redis 2.6.12
     * @param key 键
     * @param value 长整数
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 返回布尔值,成功true,失败false
     */
    public Boolean setLongIfAbsent(String key, long value, long timeout, TimeUnit unit) {
        return this.operations.setIfAbsent(key, value, timeout, unit);
    }

    /**
     * 获取长整数
     * @see <a href="http://redis.io/commands/get">Redis Documentation: GET</a>
     * @since redis 1.0.0
     * @param key 键
     * @return 返回长整数
     */
    public Long getLong(String key) {
        Object value = this.operations.get(key);
        if (value!=null) {
            return Long.valueOf(value.toString());
        }
        return null;
    }

    /**
     * 获取长整数
     * @see <a href="http://redis.io/commands/hget">Redis Documentation: HGET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @return 返回长整数
     */
    public Long getLong(String key, String hashKey) {
        Object value = this.hashOperations.get(key, hashKey);
        if (value!=null) {
            return Long.valueOf(value.toString());
        }
        return null;
    }

    /**
     * 获取并设置长整数
     * @see <a href="http://redis.io/commands/getset">Redis Documentation: GETSET</a>
     * @since redis 1.0.0
     * @param key 键
     * @param newValue 新值
     * @return 返回原值
     */
    public Long getAndSetLong(String key, long newValue) {
        Object value = this.operations.getAndSet(key, newValue);
        if (value!=null) {
            return Long.valueOf(value.toString());
        }
        return null;
    }

    /**
     * 获取并设置长整数
     * @see <a href="http://redis.io/commands/hget">Redis Documentation: HGET</a>
     * @see <a href="http://redis.io/commands/hset">Redis Documentation: HSET</a>
     * @since redis 1.0.0
     * @param key 键
     * @param hashKey hash键
     * @param newValue 新值
     * @return 返回原值
     */
    public Long getAndSetLong(String key, String hashKey, long newValue) {
        Long value = this.getLong(key, hashKey);
        this.setLong(key, hashKey, newValue);
        return value;
    }

    /**
     * 增加长整数
     * @see <a href="http://redis.io/commands/incrby">Redis Documentation: INCRBY</a>
     * @since redis 1.0.0
     * @param key 键
     * @param data 步长
     * @return 返回增加后的值
     */
    public Long addLong(String key, long data) {
        return this.operations.increment(key, data);
    }

    /**
     * 增加长整数
     * @see <a href="http://redis.io/commands/hincrby">Redis Documentation: HINCRBY</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @param data 步长
     * @return 返回增加后的值
     */
    public Long addLong(String key, String hashKey, long data) {
        return this.hashOperations.increment(key, hashKey, data);
    }

    /**
     * 获取并增加长整数
     * @see <a href="http://redis.io/commands/incrby">Redis Documentation: INCRBY</a>
     * @since redis 1.0.0
     * @param key 键
     * @param data 步长
     * @return 返回原值
     */
    public Long getAndAddLong(String key, long data) {
        return this.addLong(key, data) - data;
    }

    /**
     * 获取并增加长整数
     * @see <a href="http://redis.io/commands/hincrby">Redis Documentation: HINCRBY</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @param data 步长
     * @return 返回原值
     */
    public Long getAndAddLong(String key, String hashKey, long data) {
        return this.addLong(key, hashKey, data) - data;
    }

    /**
     * 长整数自增
     * @see <a href="http://redis.io/commands/incr">Redis Documentation: INCR</a>
     * @since redis 1.0.0
     * @param key 键
     * @return 返回自增后的值
     */
    public Long incrementLong(String key) {
        return this.operations.increment(key);
    }

    /**
     * 长整数自增
     * @see <a href="http://redis.io/commands/hincrby">Redis Documentation: HINCRBY</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @return 返回自增后的值
     */
    public Long incrementLong(String key, String hashKey) {
        return this.addLong(key, hashKey, 1L);
    }

    /**
     * 获取并自增长整数
     * @see <a href="http://redis.io/commands/incr">Redis Documentation: INCR</a>
     * @since redis 1.0.0
     * @param key 键
     * @return 返回原值
     */
    public Long getAndIncrementLong(String key) {
        return this.incrementLong(key) - 1L;
    }

    /**
     * 获取并自增长整数
     * @see <a href="http://redis.io/commands/hincrby">Redis Documentation: HINCRBY</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @return 返回原值
     */
    public Long getAndIncrementLong(String key, String hashKey) {
        return this.incrementLong(key, hashKey) - 1L;
    }

    /**
     * 长整数减小
     * @see <a href="http://redis.io/commands/decrby">Redis Documentation: DECRBY</a>
     * @since redis 1.0.0
     * @param key 键
     * @param data 步长
     * @return 返回减小后的值
     */
    public Long subtractLong(String key, long data) {
        return this.operations.decrement(key, data);
    }

    /**
     * 长整数减小
     * @see <a href="http://redis.io/commands/hincrby">Redis Documentation: HINCRBY</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @param data 步长
     * @return 返回减小后的值
     */
    public Long subtractLong(String key, String hashKey, long data) {
        return this.hashOperations.increment(key, hashKey, -data);
    }

    /**
     * 获取并减小长整数
     * @see <a href="http://redis.io/commands/decrby">Redis Documentation: DECRBY</a>
     * @since redis 1.0.0
     * @param key 键
     * @param data 步长
     * @return 返回原值
     */
    public Long getAndSubtractLong(String key, long data) {
        return this.subtractLong(key, data) + data;
    }

    /**
     * 获取并减小长整数
     * @see <a href="http://redis.io/commands/hincrby">Redis Documentation: HINCRBY</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @param data 步长
     * @return 返回原值
     */
    public Long getAndSubtractLong(String key, String hashKey, long data) {
        return this.subtractLong(key, hashKey, data)  + data;
    }

    /**
     * 长整数递减
     * @see <a href="http://redis.io/commands/decr">Redis Documentation: DECR</a>
     * @since redis 1.0.0
     * @param key 键
     * @return 返回递减后的值
     */
    public Long decrementLong(String key) {
        return this.operations.decrement(key);
    }

    /**
     * 长整数递减
     * @see <a href="http://redis.io/commands/hincrby">Redis Documentation: HINCRBY</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @return 返回递减后的值
     */
    public Long decrementLong(String key, String hashKey) {
        return this.subtractLong(key, hashKey, 1L);
    }

    /**
     * 获取并递减长整数
     * @see <a href="http://redis.io/commands/decr">Redis Documentation: DECR</a>
     * @since redis 1.0.0
     * @param key 键
     * @return 返回原值
     */
    public Long getAndDecrementLong(String key) {
        return this.decrementLong(key) + 1L;
    }

    /**
     * 获取并递减长整数
     * @see <a href="http://redis.io/commands/hincrby">Redis Documentation: HINCRBY</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKey hash键
     * @return 返回原值
     */
    public Long getAndDecrementLong(String key, String hashKey) {
        return this.decrementLong(key, hashKey) + 1L;
    }

    /**
     * 移除
     * @see <a href="http://redis.io/commands/del">Redis Documentation: DEL</a>
     * @since redis 1.0.0
     * @param keys 键
     * @return 返回移除数量
     */
    public Long removeForValue(String ...keys) {
        return this.operations.getOperations().delete(Arrays.asList(keys));
    }

    /**
     * 移除
     * @see <a href="http://redis.io/commands/hdel">Redis Documentation: HDEL</a>
     * @since redis 2.0.0
     * @param key 键
     * @param hashKeys hash键
     * @return 返回移除数量
     */
    public Long removeForHash(String key, String ...hashKeys) {
        return this.hashOperations.delete(key, (Object[]) hashKeys);
    }

    /**
     * 获取spring redis模板
     * @return 返回对象模板
     */
    public RedisTemplate<String, Object> getRedisTemplate() {
        return this.redisTemplate;
    }

    /**
     * 获取spring string redis模板
     * @return 返回字符串模板
     */
    public StringRedisTemplate getStringRedisTemplate() {
        return this.stringRedisTemplate;
    }
}
