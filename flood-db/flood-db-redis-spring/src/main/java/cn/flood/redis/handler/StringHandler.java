package cn.flood.redis.handler;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import cn.flood.redis.util.ConvertUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 字符串助手
* @author daimm
 * @date 2019/4/8
 * @since 1.8
 */
public final class StringHandler implements RedisHandler {
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
     * 字符串模板
     */
    private ValueOperations<String, String> stringOperations;

    /**
     * 字符串助手构造
     * @param dbIndex 数据库索引
     */
    @SuppressWarnings("unchecked")
    StringHandler(Integer dbIndex) {
        List<RedisTemplate> templateList = HandlerManager.createTemplate(dbIndex);
        this.redisTemplate = templateList.get(0);
        this.stringRedisTemplate = (StringRedisTemplate) templateList.get(1);
        this.operations = redisTemplate.opsForValue();
        this.stringOperations = stringRedisTemplate.opsForValue();
    }

    /**
     * 字符串助手构造
     * @param transactionHandler 事务助手
     */
    StringHandler(TransactionHandler transactionHandler) {
        this.redisTemplate = transactionHandler.getRedisTemplate();
        this.stringRedisTemplate = transactionHandler.getStringRedisTemplate();
        this.operations = this.redisTemplate.opsForValue();
        this.stringOperations = this.stringRedisTemplate.opsForValue();
    }

    /**
     * 移除对象
     * @see <a href="http://redis.io/commands/del">Redis Documentation: DEL</a>
     * @since redis 1.0.0
     * @param keys 键
     * @return 返回移除数量
     */
    public Long removeAsObj(String ...keys) {
        return this.operations.getOperations().delete(Arrays.asList(keys));
    }

    /**
     * 移除字符串
     * @see <a href="http://redis.io/commands/del">Redis Documentation: DEL</a>
     * @since redis 1.0.0
     * @param keys 键
     * @return 返回移除数量
     */
    public Long remove(String ...keys) {
        return this.stringOperations.getOperations().delete(Arrays.asList(keys));
    }

    /**
     * 设置对象
     * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param value 对象
     */
    public void setAsObj(String key, Object value) {
        this.operations.set(key, value);
    }

    /**
     * 设置字符串
     * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
     * @since redis 2.0.0
     * @param key 键
     * @param value 字符串
     */
    public void set(String key, String value) {
        this.stringOperations.set(key, value);
    }

    /**
     * 设置对象(若存在则更新过期时间)
     * @see <a href="http://redis.io/commands/setex">Redis Documentation: SETEX</a>
     * @since redis 2.0.0
     * @param key 键
     * @param value 对象
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void setAsObj(String key, Object value, long timeout, TimeUnit unit) {
        this.operations.set(key, value, timeout, unit);
    }

    /**
     * 设置字符串(若存在则更新过期时间)
     * @see <a href="http://redis.io/commands/setex">Redis Documentation: SETEX</a>
     * @since redis 2.0.0
     * @param key 键
     * @param value 字符串
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        this.stringOperations.set(key, value, timeout, unit);
    }

    /**
     * 批量设置对象
     * @see <a href="http://redis.io/commands/mset">Redis Documentation: MSET</a>
     * @since redis 1.0.1
     * @param map 对象集合
     */
    public void msetAsObj(Map<String, Object> map) {
        this.operations.multiSet(map);
    }

    /**
     * 批量设置字符串
     * @see <a href="http://redis.io/commands/mset">Redis Documentation: MSET</a>
     * @since redis 1.0.1
     * @param map 字符串集合
     */
    public void mset(Map<String, String> map) {
        this.stringOperations.multiSet(map);
    }

    /**
     * 追加新字符串
     * @see <a href="http://redis.io/commands/append">Redis Documentation: APPEND</a>
     * @since redis 2.0.0
     * @param key 键
     * @param value 字符串
     */
    public void append(String key, String value) {
        this.stringOperations.append(key, value);
    }

    /**
     * 设置对象如果不存在
     * @see <a href="http://redis.io/commands/setnx">Redis Documentation: SETNX</a>
     * @since redis 2.6.12
     * @param key 键
     * @param value 对象
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 返回布尔值,成功true,失败false
     */
    public Boolean setIfAbsentAsObj(String key, Object value, long timeout, TimeUnit unit) {
        return this.operations.setIfAbsent(key, value, timeout, unit);
    }

    /**
     * 设置字符串如果不存在
     * @see <a href="http://redis.io/commands/setnx">Redis Documentation: SETNX</a>
     * @since redis 2.6.12
     * @param key 键
     * @param value 字符串
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 返回布尔值,成功true,失败false
     */
    public Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        return this.stringOperations.setIfAbsent(key, value, timeout, unit);
    }

    /**
     * 设置对象如果不存在
     * @see <a href="http://redis.io/commands/setnx">Redis Documentation: SETNX</a>
     * @since redis 1.0.0
     * @param key 键
     * @param value 对象
     * @return 返回布尔值,成功true,失败false
     */
    public Boolean setIfAbsentAsObj(String key, Object value) {
        return this.operations.setIfAbsent(key, value);
    }

    /**
     * 设置字符串如果不存在
     * @see <a href="http://redis.io/commands/setnx">Redis Documentation: SETNX</a>
     * @since redis 1.0.0
     * @param key 键
     * @param value 字符串
     * @return 返回布尔值,成功true,失败false
     */
    public Boolean setIfAbsent(String key, String value) {
        return this.stringOperations.setIfAbsent(key, value);
    }

    /**
     * 批量设置对象如果不存在
     * @see <a href="http://redis.io/commands/msetnx">Redis Documentation: MSETNX</a>
     * @since redis 1.0.1
     * @param map 对象集合
     * @return 返回布尔值,成功true,失败false
     */
    public Boolean msetIfAbsentAsObj(Map<String, Object> map) {
        return this.operations.multiSetIfAbsent(map);
    }

    /**
     * 批量设置字符串如果不存在
     * @see <a href="http://redis.io/commands/msetnx">Redis Documentation: MSETNX</a>
     * @since redis 1.0.1
     * @param map 字符串集合
     * @return 返回布尔值,成功true,失败false
     */
    public Boolean msetIfAbsent(Map<String, String> map) {
        return this.stringOperations.multiSetIfAbsent(map);
    }

    /**
     * 获取对象
     * @see <a href="http://redis.io/commands/get">Redis Documentation: GET</a>
     * @since redis 1.0.0
     * @param key 键
     * @param <T> 返回类型
     * @return 返回对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getAsObj(String key) {
        return (T) ConvertUtil.toJavaType(this.operations.get(key), Object.class);
    }

    /**
     * 获取对象
     * @see <a href="http://redis.io/commands/get">Redis Documentation: GET</a>
     * @since redis 1.0.0
     * @param type 返回值类型
     * @param key 键
     * @param <T> 返回类型
     * @return 返回对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getAsObj(Class<T> type, String key) {
        return (T) ConvertUtil.toJavaType(this.operations.get(key), type);
    }

    /**
     * 获取字符串
     * @see <a href="http://redis.io/commands/get">Redis Documentation: GET</a>
     * @since redis 1.0.0
     * @param key 键
     * @return 返回字符串
     */
    public String get(String key) {
        return this.stringOperations.get(key);
    }

    /**
     * 获取并设置新对象
     * @see <a href="http://redis.io/commands/getset">Redis Documentation: GETSET</a>
     * @since redis 1.0.0
     * @param key 键
     * @param value 对象
     * @param <T> 返回类型
     * @return 返回对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getAndSetAsObj(String key, Object value) {
        return (T) ConvertUtil.toJavaType(this.operations.getAndSet(key, value), Object.class);
    }

    /**
     * 获取并设置新对象
     * @see <a href="http://redis.io/commands/getset">Redis Documentation: GETSET</a>
     * @since redis 1.0.0
     * @param type 返回值类型
     * @param key 键
     * @param value 对象
     * @param <T> 返回类型
     * @return 返回对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getAndSetAsObj(Class<T> type, String key, Object value) {
        return (T) ConvertUtil.toJavaType(this.operations.getAndSet(key, value), type);
    }

    /**
     * 获取并设置新字符串
     * @see <a href="http://redis.io/commands/getset">Redis Documentation: GETSET</a>
     * @since redis 1.0.0
     * @param key 键
     * @param value 字符串
     * @return 返回字符串
     */
    public String getAndSet(String key, String value) {
        return this.stringOperations.getAndSet(key, value);
    }

    /**
     * 批量获取对象
     * @see <a href="http://redis.io/commands/mget">Redis Documentation: MGET</a>
     * @since redis 1.0.0
     * @param keys 键
     * @return 返回对象列表
     */
    public List mgetAsObj(String ...keys) {
        return this.operations.multiGet(Arrays.asList(keys));
    }

    /**
     * 批量获取字符串
     * @see <a href="http://redis.io/commands/mget">Redis Documentation: MGET</a>
     * @since redis 1.0.0
     * @param keys 键
     * @return 返回字符串列表
     */
    public List<String> mget(String ...keys) {
        return this.stringOperations.multiGet(Arrays.asList(keys));
    }

    /**
     * 获取字符串的长度
     * @see <a href="http://redis.io/commands/strlen">Redis Documentation: STRLEN</a>
     * @since redis 2.2.0
     * @param key 键
     * @return 返回字符串长度
     */
    public Long length(String key) {
        return this.stringOperations.size(key);
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
