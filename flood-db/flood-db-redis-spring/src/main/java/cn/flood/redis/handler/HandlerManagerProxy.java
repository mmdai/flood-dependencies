package cn.flood.redis.handler;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.HashMapper;

/**
 * 助手管理代理
* @author daimm
 * @date 2019/6/15
 * @since 1.8
 */
public class HandlerManagerProxy {

    /**
     * 助手管理实例
     */
    private final HandlerManager manager = new HandlerManager();

    /**
     * 获取助手
     * @param type 助手类型
     * @param <T> 返回类型
     * @return 返回助手
     */
    @SuppressWarnings("unchecked")
    public <T extends RedisHandler> T  getHandler(HandlerType type) {
        return (T) this.manager.getHandler(this.manager.getDefaultKey(), type);
    }

    /**
     * 获取助手
     * @param key KEY
     * @param type 助手类型
     * @param <T> 返回类型
     * @return 返回助手
     */
    @SuppressWarnings("unchecked")
    public <T extends RedisHandler> T getHandler(String key, HandlerType type) {
        return (T) this.manager.getHandler(key, type);
    }

    /**
     * 获取助手
     * @param transactionHandler 事务助手
     * @param type 助手类型
     * @param <T> 返回类型
     * @return 返回助手
     */
    @SuppressWarnings("unchecked")
    public <T extends RedisHandler> T getHandler(TransactionHandler transactionHandler, HandlerType type) {
        return (T) this.manager.getHandler(transactionHandler, type);
    }

    /**
     * 获取流助手
     * @param dbIndex 数据库索引
     * @param mapper 哈希映射器
     * @return 返回流助手
     */
    public StreamHandler getStreamHandler(int dbIndex, HashMapper<String, String, Object> mapper) {
        return new StreamHandler(dbIndex, mapper);
    }

    /**
     * 获取默认的对象模板
     * @return 返回默认的对象模板
     */
    public RedisTemplate<String, Object> getDefaultRedisTemplate() {
        return this.manager.getDefaultRedisTemplate();
    }

    /**
     * 获取默认的字符串模板
     * @return 返回默认的字符串模板
     */
    public StringRedisTemplate getDefaultStringRedisTemplate() {
        return this.manager.getDefaultStringRedisTemplate();
    }
}
