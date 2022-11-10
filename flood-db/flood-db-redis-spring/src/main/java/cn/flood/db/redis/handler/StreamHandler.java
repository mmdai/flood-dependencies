package cn.flood.db.redis.handler;

import cn.flood.db.redis.util.ConvertUtil;
import cn.flood.db.redis.util.RedisUtil;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.hash.HashMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流助手
* @author daimm
 * @date 2019/6/25
 * @since 1.8
 */
public final class StreamHandler implements RedisHandler {
    /**
     * 对象模板
     */
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 对象模板
     */
    private StreamOperations<String, String, Object> streamOperations;
    /**
     * 数据库索引
     */
    private int dbIndex;

    /**
     * 流助手构造
     * @param dbIndex 数据库索引
     */
    StreamHandler(Integer dbIndex) {
        this.dbIndex = dbIndex;
        this.redisTemplate = HandlerManager.createRedisTemplate(dbIndex);
        this.streamOperations = this.redisTemplate.opsForStream();
    }

    /**
     * 流助手构造
     * @param dbIndex 数据库索引
     * @param hashMapper 哈希映射
     */
    StreamHandler(Integer dbIndex, HashMapper<String, String, Object> hashMapper) {
        this.dbIndex = dbIndex;
        this.redisTemplate = HandlerManager.createRedisTemplate(dbIndex);
        this.streamOperations = this.redisTemplate.opsForStream(hashMapper);
    }

    /**
     * 流助手构造
     * @param transactionHandler 事务助手
     */
    StreamHandler(TransactionHandler transactionHandler) {
        this.dbIndex = transactionHandler.getDbIndex();
        this.redisTemplate = transactionHandler.getRedisTemplate();
        this.streamOperations = this.redisTemplate.opsForStream();
    }

    /**
     * 流助手构造
     * @param transactionHandler 事务助手
     * @param hashMapper 哈希映射
     */
    StreamHandler(TransactionHandler transactionHandler, HashMapper<String, String, Object> hashMapper) {
        this.dbIndex = transactionHandler.getDbIndex();
        this.redisTemplate = transactionHandler.getRedisTemplate();
        this.streamOperations = this.redisTemplate.opsForStream(hashMapper);
    }

    /**
     * 条目数量
     * @see <a href="http://redis.io/commands/xlen">Redis Documentation: XLEN</a>
     * @since redis 5.0.0
     * @param key 键
     * @return 返回条目数量
     */
    public Long size(String key) {
        return this.streamOperations.size(key);
    }

    /**
     * 添加条目
     * @see <a href="http://redis.io/commands/xadd">Redis Documentation: XADD</a>
     * @since redis 5.0.0
     * @param key 键
     * @param value 元素字典
     * @return 返回 RecordId 对象
     */
    public RecordId add(String key, Map<String, Object> value) {
        return this.streamOperations.add(key, value);
    }

    /**
     * 添加条目
     * @see <a href="http://redis.io/commands/xadd">Redis Documentation: XADD</a>
     * @since redis 5.0.0
     * @param key 键
     * @param value 元素
     * @return 返回 RecordId 对象
     */
    public RecordId add(String key, Object value) {
        return this.streamOperations.add(StreamRecords.newRecord().in(key).ofObject(value));
    }

    /**
     * 添加条目
     * @see <a href="http://redis.io/commands/xadd">Redis Documentation: XADD</a>
     * @since redis 5.0.0
     * @param record 条目
     * @return 返回 RecordId 对象
     */
    @SuppressWarnings("unchecked")
    public RecordId add(Record record) {
        return this.streamOperations.add(record);
    }

    /**
     * 裁剪条目(保留最新)
     * @see <a href="http://redis.io/commands/xtrim">Redis Documentation: XTRIM</a>
     * @since redis 5.0.0
     * @param key 键
     * @param count 保留数量
     * @return 返回被裁剪的数量
     */
    public Long trim(String key, long count) {
        return this.streamOperations.trim(key, count);
    }

    /**
     * 移除条目
     * @see <a href="http://redis.io/commands/xdel">Redis Documentation: XDEL</a>
     * @since redis 5.0.0
     * @param key 键
     * @param recordIds 条目id列表
     * @return 返回被删除的数量
     */
    public Long remove(String key, String ...recordIds) {
        return this.streamOperations.delete(key, recordIds);
    }

    /**
     * 创建消费者组
     * @see <a href="http://redis.io/commands/xgroup">Redis Documentation: XGROUP CREATE</a>
     * @since redis 5.0.0
     * @param key 键
     * @param groupName 消费者组名称
     */
    public void createGroup(String key, String groupName) {
        this.streamOperations.createGroup(key, groupName);
    }

    /**
     * 创建消费者组
     * @see <a href="http://redis.io/commands/xgroup">Redis Documentation: XGROUP CREATE</a>
     * @since redis 5.0.0
     * @param key 键
     * @param readOffset 偏移量
     * @param groupName 消费者组名称
     */
    public void createGroup(String key, ReadOffset readOffset, String groupName) {
        this.streamOperations.createGroup(key, readOffset, groupName);
    }

    /**
     * 移除消费者组
     * @see <a href="http://redis.io/commands/xgroup">Redis Documentation: XGROUP DESTROY</a>
     * @since redis 5.0.0
     * @param key 键
     * @param groupName 消费者组名称
     * @return 返回布尔值，成功true，失败false
     */
    public Boolean removeGroup(String key, String groupName) {
        return this.streamOperations.destroyGroup(key, groupName);
    }

    /**
     * 移除消费者
     * @see <a href="http://redis.io/commands/xgroup">Redis Documentation: XGROUP DELCONSUMER</a>
     * @since redis 5.0.0
     * @param key 键
     * @param groupName 消费者组名称
     * @param consumerName 消费者名称
     * @return 返回布尔值，成功true，失败false
     */
    public Boolean removeConsumer(String key, String groupName, String consumerName) {
        return this.streamOperations.deleteConsumer(key, Consumer.from(groupName, consumerName));
    }

    /**
     * 确认条目
     * @see <a href="http://redis.io/commands/xack">Redis Documentation: XACK</a>
     * @since redis 5.0.0
     * @param key 键
     * @param groupName 消费者组名称
     * @param recordIds 条目id列表
     * @return 返回成功确认的消息数
     */
    public Long ack(String key, String groupName, String... recordIds) {
        return this.streamOperations.acknowledge(key, groupName, recordIds);
    }

    /**
     * 获取所有权
     * @param key 键
     * @param groupName 消费者组名称
     * @param consumerName 消费者名称
     * @param timeout 消息空闲时间
     * @param recordIds 条目id列表
     * @return 返回条目
     */
    public Object claim(String key, String groupName, String consumerName, long timeout, String... recordIds) {
        return RedisUtil.getCustomCommandHandler(this.dbIndex).execute(
                "XCLAIM",
                ConvertUtil.toByteArray(
                        this.redisTemplate.getKeySerializer(),
                        recordIds,
                        key,
                        groupName,
                        consumerName,
                        String.valueOf(timeout)
                )
        );
    }

    /**
     * 待处理条目
     * @see <a href="http://redis.io/commands/xpending">Redis Documentation: XPENDING</a>
     * @since redis 5.0.0
     * @param key 键
     * @param groupName 消费者组名称
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> pending(String key, String groupName) {
        List<Object> list = (List<Object>) ConvertUtil.toJavaType(
                this.redisTemplate.getKeySerializer(),
                RedisUtil.getCustomCommandHandler(this.dbIndex).execute(
                        "XPENDING",
                        ConvertUtil.toByteArray(this.redisTemplate.getKeySerializer(), key, groupName)
                )
        );
        Map<String, Object> data = new HashMap<>(4);
        if (list==null) {
            data.put("count", 0);
            data.put("minId", null);
            data.put("maxId", null);
            data.put("consumers", new ArrayList<>(0));
        }else {
            List<Object> dataList = (List<Object>) list.get(0);
            data.put("count", dataList.get(0));
            data.put("minId", dataList.get(1));
            data.put("maxId", dataList.get(2));
            data.put("consumers", dataList.get(3));
        }
        return data;
    }

    /**
     * 待处理条目
     * @see <a href="http://redis.io/commands/xpending">Redis Documentation: XPENDING</a>
     * @since redis 5.0.0
     * @param key 键
     * @param groupName 消费者组名称
     * @param count 返回数量
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> pending(String key, String groupName, int count) {
        return this.toPendingResult(
                (List<Object>) ConvertUtil.toJavaType(
                        this.redisTemplate.getKeySerializer(),
                        RedisUtil.getCustomCommandHandler(this.dbIndex).execute(
                                "XPENDING",
                                ConvertUtil.toByteArray(
                                        this.redisTemplate.getKeySerializer(),
                                        key,
                                        groupName,
                                        "-",
                                        "+",
                                        String.valueOf(count)
                                )
                        )
                )
        );
    }

    /**
     * 待处理条目
     * @see <a href="http://redis.io/commands/xpending">Redis Documentation: XPENDING</a>
     * @since redis 5.0.0
     * @param key 键
     * @param groupName 消费者组名称
     * @param consumerName 消费者名称
     * @param count 返回数量
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> pending(String key, String groupName, String consumerName, int count) {
        return this.toPendingResult(
                (List<Object>) ConvertUtil.toJavaType(
                        this.redisTemplate.getKeySerializer(),
                        RedisUtil.getCustomCommandHandler(this.dbIndex).execute(
                                "XPENDING",
                                ConvertUtil.toByteArray(
                                        this.redisTemplate.getKeySerializer(),
                                        key,
                                        groupName,
                                        "-",
                                        "+",
                                        String.valueOf(count),
                                        consumerName
                                )
                        )
                )
        );
    }

    /**
     * 读取条目(最早)
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param keys 键
     * @return 返回条目字典
     */
    public Map<String, Object> readEarliest(String... keys) {
        return this.readEarliest(StreamReadOptions.empty(), keys);
    }

    /**
     * 读取条目(最早)
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param keys 键
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> readEarliest(Class<T> resultType, String... keys) {
        return this.readEarliest(resultType, StreamReadOptions.empty(), keys);
    }

    /**
     * 读取条目(最早)
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param readOptions 读取选项
     * @param keys 键
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> readEarliest(StreamReadOptions readOptions, String... keys) {
        return ConvertUtil.toMap(
                this.streamOperations.read(
                        readOptions.count(1),
                        this.createStreamOffsetByKeys(keys)
                ),
                StreamDataType.EARLIEST
        );
    }

    /**
     * 读取条目(最早)
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param readOptions 读取选项
     * @param keys 键
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> readEarliest(Class<T> resultType, StreamReadOptions readOptions, String... keys) {
        return ConvertUtil.toMap(
                this.streamOperations.read(
                        resultType,
                        readOptions.count(1),
                        this.createStreamOffsetByKeys(keys)
                ),
                StreamDataType.EARLIEST
        );
    }

    /**
     * 读取条目(最新)
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param keys 键
     * @return 返回条目字典
     */
    public Map<String, Object> readLatest(String... keys) {
        return this.readLatest(StreamReadOptions.empty(), keys);
    }

    /**
     * 读取条目(最新)
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param keys 键
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> readLatest(Class<T> resultType, String... keys) {
        return this.readLatest(resultType, StreamReadOptions.empty(), keys);
    }

    /**
     * 读取条目(最新)
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param readOptions 读取选项
     * @param keys 键
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> readLatest(StreamReadOptions readOptions, String... keys) {
        return ConvertUtil.toMap(
                this.streamOperations.read(
                        readOptions,
                        this.createStreamOffsetByKeys(keys)
                ),
                StreamDataType.LATEST
        );
    }

    /**
     * 读取条目(最新)
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param readOptions 读取选项
     * @param keys 键
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> readLatest(Class<T> resultType, StreamReadOptions readOptions, String... keys) {
        return ConvertUtil.toMap(
                this.streamOperations.read(
                        resultType,
                        readOptions,
                        this.createStreamOffsetByKeys(keys)
                ),
                StreamDataType.LATEST
        );
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param key 键
     * @param recordIds 条目id列表
     * @return 返回条目字典
     */
    public Map<String, Object> read(String key, String... recordIds) {
        return this.read(StreamReadOptions.empty(), key, recordIds);
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param key 键
     * @param recordIds 条目id列表
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> read(Class<T> resultType, String key, String... recordIds) {
        return this.read(resultType, StreamReadOptions.empty(), key, recordIds);
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param readOptions 读取选项
     * @param key 键
     * @param recordIds 条目id列表
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> read(StreamReadOptions readOptions, String key, String... recordIds) {
        return ConvertUtil.toMap(
                this.streamOperations.read(
                        readOptions,
                        this.createStreamOffsetByRecordIds(key, recordIds)
                ),
                StreamDataType.ALL
        );
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param readOptions 读取选项
     * @param key 键
     * @param recordIds 条目id列表
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> read(Class<T> resultType, StreamReadOptions readOptions, String key, String... recordIds) {
        return ConvertUtil.toMap(
                this.streamOperations.read(
                        resultType,
                        readOptions,
                        this.createStreamOffsetByRecordIds(key, recordIds)
                ),
                StreamDataType.ALL
        );
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param keyRecordIdMap 键与条目id字典
     * @return 返回条目字典
     */
    public Map<String, Object> read(Map<String, String> keyRecordIdMap) {
        return this.read(StreamReadOptions.empty(), keyRecordIdMap);
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param keyRecordIdMap 键与条目id字典
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> read(Class<T> resultType, Map<String, String> keyRecordIdMap) {
        return this.read(resultType, StreamReadOptions.empty(), keyRecordIdMap);
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param readOptions 读取选项
     * @param keyRecordIdMap 键与条目id字典
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> read(StreamReadOptions readOptions, Map<String, String> keyRecordIdMap) {
        return ConvertUtil.toMap(
                this.streamOperations.read(
                        readOptions,
                        this.createStreamOffset(keyRecordIdMap)
                ),
                StreamDataType.ALL
        );
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xread">Redis Documentation: XREAD</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param readOptions 读取选项
     * @param keyRecordIdMap 键与条目id字典
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> read(Class<T> resultType, StreamReadOptions readOptions, Map<String, String> keyRecordIdMap) {
        return ConvertUtil.toMap(
                this.streamOperations.read(
                        resultType,
                        readOptions,
                        this.createStreamOffset(keyRecordIdMap)
                ),
                StreamDataType.ALL
        );
    }

    /**
     * 读取条目(最早)
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param consumer 消费者对象
     * @param keys 键
     * @return 返回条目字典
     */
    public Map<String, Object> readEarliestByConsumer(Consumer consumer, String... keys) {
        return this.readEarliestByConsumer(consumer, StreamReadOptions.empty(), keys);
    }

    /**
     * 读取条目(最早)
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param consumer 消费者对象
     * @param keys 键
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> readEarliestByConsumer(Class<T> resultType, Consumer consumer, String... keys) {
        return this.readEarliestByConsumer(resultType, consumer, StreamReadOptions.empty(), keys);
    }

    /**
     * 读取条目(最早)
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param consumer 消费者对象
     * @param readOptions 读取选项
     * @param keys 键
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> readEarliestByConsumer(Consumer consumer, StreamReadOptions readOptions, String... keys) {
        return ConvertUtil.toMap(
                this.streamOperations.read(
                        consumer,
                        readOptions.count(1),
                        this.createStreamOffsetByKeys(keys)
                ),
                StreamDataType.EARLIEST
        );
    }

    /**
     * 读取条目(最早)
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param consumer 消费者对象
     * @param readOptions 读取选项
     * @param keys 键
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> readEarliestByConsumer(Class<T> resultType, Consumer consumer, StreamReadOptions readOptions, String... keys) {
        return ConvertUtil.toMap(
                this.streamOperations.read(
                        resultType,
                        consumer,
                        readOptions.count(1),
                        this.createStreamOffsetByKeys(keys)
                ),
                StreamDataType.EARLIEST
        );
    }

    /**
     * 读取条目(最新)
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param consumer 消费者对象
     * @param keys 键
     * @return 返回条目字典
     */
    public Map<String, Object> readLatestByConsumer(Consumer consumer, String... keys) {
        return this.readLatestByConsumer(consumer, StreamReadOptions.empty(), keys);
    }

    /**
     * 读取条目(最新)
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param consumer 消费者对象
     * @param keys 键
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> readLatestByConsumer(Class<T> resultType, Consumer consumer, String... keys) {
        return this.readLatestByConsumer(resultType, consumer, StreamReadOptions.empty(), keys);
    }

    /**
     * 读取条目(最新)
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param consumer 消费者对象
     * @param readOptions 读取选项
     * @param keys 键
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> readLatestByConsumer(Consumer consumer, StreamReadOptions readOptions, String... keys) {
        return ConvertUtil.toMap(
                this.streamOperations.read(
                        consumer,
                        readOptions,
                        this.createStreamOffsetByKeys(keys)
                ),
                StreamDataType.LATEST
        );
    }

    /**
     * 读取条目(最新)
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param consumer 消费者对象
     * @param readOptions 读取选项
     * @param keys 键
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> readLatestByConsumer(Class<T> resultType, Consumer consumer, StreamReadOptions readOptions, String... keys) {
        return ConvertUtil.toMap(
                this.streamOperations.read(
                        resultType,
                        consumer,
                        readOptions,
                        this.createStreamOffsetByKeys(keys)
                ),
                StreamDataType.LATEST
        );
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param consumer 消费者对象
     * @param key 键
     * @param recordIds 条目id列表
     * @return 返回条目字典
     */
    public Map<String, Object> readByConsumer(Consumer consumer, String key, String... recordIds) {
        return this.readByConsumer(consumer, StreamReadOptions.empty(), key, recordIds);
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param consumer 消费者对象
     * @param key 键
     * @param recordIds 条目id列表
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> readByConsumer(Class<T> resultType, Consumer consumer, String key, String... recordIds) {
        return this.readByConsumer(resultType, consumer, StreamReadOptions.empty(), key, recordIds);
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param consumer 消费者对象
     * @param readOptions 读取选项
     * @param key 键
     * @param recordIds 条目id列表
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> readByConsumer(Consumer consumer, StreamReadOptions readOptions, String key, String... recordIds) {
        return ConvertUtil.toMap(this.streamOperations.read(consumer, readOptions, this.createStreamOffsetByRecordIds(key, recordIds)), StreamDataType.ALL);
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param consumer 消费者对象
     * @param readOptions 读取选项
     * @param key 键
     * @param recordIds 条目id列表
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> readByConsumer(Class<T> resultType, Consumer consumer, StreamReadOptions readOptions, String key, String... recordIds) {
        return ConvertUtil.toMap(this.streamOperations.read(resultType, consumer, readOptions, this.createStreamOffsetByRecordIds(key, recordIds)), StreamDataType.ALL);
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param consumer 消费者对象
     * @param keyRecordIdMap 键与条目id字典
     * @return 返回条目字典
     */
    public Map<String, Object> readByConsumer(Consumer consumer, Map<String, String> keyRecordIdMap) {
        return this.readByConsumer(consumer, StreamReadOptions.empty(), keyRecordIdMap);
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param consumer 消费者对象
     * @param keyRecordIdMap 键与条目id字典
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> readByConsumer(Class<T> resultType, Consumer consumer, Map<String, String> keyRecordIdMap) {
        return this.readByConsumer(resultType, consumer, StreamReadOptions.empty(), keyRecordIdMap);
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param consumer 消费者对象
     * @param keyRecordIdMap 键与条目id字典
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> readByConsumer(Consumer consumer, StreamReadOptions readOptions, Map<String, String> keyRecordIdMap) {
        return ConvertUtil.toMap(this.streamOperations.read(consumer, readOptions, this.createStreamOffset(keyRecordIdMap)), StreamDataType.ALL);
    }

    /**
     * 读取条目
     * @see <a href="http://redis.io/commands/xreadgroup">Redis Documentation: XREADGROUP</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param consumer 消费者对象
     * @param keyRecordIdMap 键与条目id字典
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> readByConsumer(Class<T> resultType, Consumer consumer, StreamReadOptions readOptions, Map<String, String> keyRecordIdMap) {
        return ConvertUtil.toMap(this.streamOperations.read(resultType, consumer, readOptions, this.createStreamOffset(keyRecordIdMap)), StreamDataType.ALL);
    }

    /**
     * 获取条目
     * @see <a href="http://redis.io/commands/xrange">Redis Documentation: XRANGE</a>
     * @since redis 5.0.0
     * @param key 键
     * @param recordId 条目id
     * @return 返回条目字典
     */
    public Map<String, Object> get(String key, String recordId) {
        return this.range(key, recordId, recordId).get(recordId);
    }

    /**
     * 获取条目
     * @see <a href="http://redis.io/commands/xrange">Redis Documentation: XRANGE</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param key 键
     * @param recordId 条目id
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> T get(Class<T> resultType, String key, String recordId) {
        return this.range(resultType, key, recordId, recordId).get(recordId);
    }

    /**
     * 获取条目
     * @see <a href="http://redis.io/commands/xrange">Redis Documentation: XRANGE</a>
     * @since redis 5.0.0
     * @param key 键
     * @return 返回条目字典
     */
    public Map<String, Map<String, Object>> range(String key) {
        return this.range(key, "-", "+");
    }

    /**
     * 获取条目
     * @see <a href="http://redis.io/commands/xrange">Redis Documentation: XRANGE</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param key 键
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> range(Class<T> resultType, String key) {
        return this.range(resultType, key, "-", "+");
    }

    /**
     * 获取条目
     * @see <a href="http://redis.io/commands/xrange">Redis Documentation: XRANGE</a>
     * @since redis 5.0.0
     * @param key 键
     * @param count 返回数量
     * @return 返回条目字典
     */
    public Map<String, Map<String, Object>> range(String key, int count) {
        return this.range(key, "-", "+", count);
    }

    /**
     * 获取条目
     * @see <a href="http://redis.io/commands/xrange">Redis Documentation: XRANGE</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param key 键
     * @param count 返回数量
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> range(Class<T> resultType, String key, int count) {
        return this.range(resultType, key, "-", "+", count);
    }

    /**
     * 获取条目
     * @see <a href="http://redis.io/commands/xrange">Redis Documentation: XRANGE</a>
     * @since redis 5.0.0
     * @param key 键
     * @param minRecordId 最小条目id
     * @param maxRecordId 最大条目id
     * @return 返回条目字典
     */
    public Map<String, Map<String, Object>> range(String key, String minRecordId, String maxRecordId) {
        return ConvertUtil.toMap(
                this.streamOperations.range(
                        key,
                        Range.of(Range.Bound.inclusive(minRecordId), Range.Bound.inclusive(maxRecordId))
                ),
                StreamDataType.ALL
        );
    }

    /**
     * 获取条目
     * @see <a href="http://redis.io/commands/xrange">Redis Documentation: XRANGE</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param key 键
     * @param minRecordId 最小条目id
     * @param maxRecordId 最大条目id
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> range(Class<T> resultType, String key, String minRecordId, String maxRecordId) {
        return ConvertUtil.toMap(
                this.streamOperations.range(
                        resultType,
                        key,
                        Range.of(Range.Bound.inclusive(minRecordId), Range.Bound.inclusive(maxRecordId))
                ),
                StreamDataType.ALL
        );
    }

    /**
     * 获取条目
     * @see <a href="http://redis.io/commands/xrange">Redis Documentation: XRANGE</a>
     * @since redis 5.0.0
     * @param key 键
     * @param minRecordId 最小条目id
     * @param maxRecordId 最大条目id
     * @param count 返回数量
     * @return 返回条目字典
     */
    public Map<String, Map<String, Object>> range(String key, String minRecordId, String maxRecordId, int count) {
        return ConvertUtil.toMap(
                this.streamOperations.range(
                        key,
                        Range.of(Range.Bound.inclusive(minRecordId), Range.Bound.inclusive(maxRecordId)),
                        RedisZSetCommands.Limit.limit().count(count)
                ),
                StreamDataType.ALL
        );
    }

    /**
     * 获取条目
     * @see <a href="http://redis.io/commands/xrange">Redis Documentation: XRANGE</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param key 键
     * @param minRecordId 最小条目id
     * @param maxRecordId 最大条目id
     * @param count 返回数量
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> range(Class<T> resultType, String key, String minRecordId, String maxRecordId, int count) {
        return ConvertUtil.toMap(
                this.streamOperations.range(
                        resultType,
                        key,
                        Range.of(Range.Bound.inclusive(minRecordId), Range.Bound.inclusive(maxRecordId)),
                        RedisZSetCommands.Limit.limit().count(count)
                ),
                StreamDataType.ALL
        );
    }

    /**
     * 获取条目(反向)
     * @see <a href="http://redis.io/commands/xrevrange">Redis Documentation: XREVRANGE</a>
     * @since redis 5.0.0
     * @param key 键
     * @return 返回条目字典
     */
    public Map<String, Map<String, Object>> reverseRange(String key) {
        return this.reverseRange(key, "-", "+");
    }

    /**
     * 获取条目(反向)
     * @see <a href="http://redis.io/commands/xrevrange">Redis Documentation: XREVRANGE</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param key 键
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> reverseRange(Class<T> resultType, String key) {
        return this.reverseRange(resultType, key, "-", "+");
    }

    /**
     * 获取条目(反向)
     * @see <a href="http://redis.io/commands/xrevrange">Redis Documentation: XREVRANGE</a>
     * @since redis 5.0.0
     * @param key 键
     * @param count 返回数量
     * @return 返回条目字典
     */
    public Map<String, Map<String, Object>> reverseRange(String key, int count) {
        return this.reverseRange(key, "-", "+", count);
    }

    /**
     * 获取条目(反向)
     * @see <a href="http://redis.io/commands/xrevrange">Redis Documentation: XREVRANGE</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param key 键
     * @param count 返回数量
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> reverseRange(Class<T> resultType, String key, int count) {
        return this.reverseRange(resultType, key, "-", "+", count);
    }

    /**
     * 获取条目(反向)
     * @see <a href="http://redis.io/commands/xrevrange">Redis Documentation: XREVRANGE</a>
     * @since redis 5.0.0
     * @param key 键
     * @param maxRecordId 最大条目id
     * @param minRecordId 最小条目id
     * @return 返回条目字典
     */
    public Map<String, Map<String, Object>> reverseRange(String key, String maxRecordId, String minRecordId) {
        return ConvertUtil.toMap(
                this.streamOperations.reverseRange(
                        key,
                        Range.of(Range.Bound.inclusive(maxRecordId), Range.Bound.inclusive(minRecordId))
                ),
                StreamDataType.ALL
        );
    }

    /**
     * 获取条目(反向)
     * @see <a href="http://redis.io/commands/xrevrange">Redis Documentation: XREVRANGE</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param key 键
     * @param maxRecordId 最大条目id
     * @param minRecordId 最小条目id
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> reverseRange(Class<T> resultType, String key, String maxRecordId, String minRecordId) {
        return ConvertUtil.toMap(
                this.streamOperations.reverseRange(
                        resultType,
                        key,
                        Range.of(Range.Bound.inclusive(maxRecordId), Range.Bound.inclusive(minRecordId))
                ),
                StreamDataType.ALL
        );
    }

    /**
     * 获取条目(反向)
     * @see <a href="http://redis.io/commands/xrevrange">Redis Documentation: XREVRANGE</a>
     * @since redis 5.0.0
     * @param key 键
     * @param maxRecordId 最大条目id
     * @param minRecordId 最小条目id
     * @param count 返回数量
     * @return 返回条目字典
     */
    public Map<String, Map<String, Object>> reverseRange(String key, String maxRecordId, String minRecordId, int count) {
        return ConvertUtil.toMap(
                this.streamOperations.reverseRange(
                        key,
                        Range.of(Range.Bound.inclusive(maxRecordId), Range.Bound.inclusive(minRecordId)),
                        RedisZSetCommands.Limit.limit().count(count)
                ),
                StreamDataType.ALL
        );
    }

    /**
     * 获取条目(反向)
     * @see <a href="http://redis.io/commands/xrevrange">Redis Documentation: XREVRANGE</a>
     * @since redis 5.0.0
     * @param resultType 返回类型
     * @param key 键
     * @param maxRecordId 最大条目id
     * @param minRecordId 最小条目id
     * @param count 返回数量
     * @param <T> 返回类型
     * @return 返回条目字典
     */
    public <T> Map<String, T> reverseRange(Class<T> resultType, String key, String maxRecordId, String minRecordId, int count) {
        return ConvertUtil.toMap(
                this.streamOperations.reverseRange(
                        resultType,
                        key,
                        Range.of(Range.Bound.inclusive(maxRecordId), Range.Bound.inclusive(minRecordId)),
                        RedisZSetCommands.Limit.limit().count(count)
                ),
                StreamDataType.ALL
        );
    }

    /**
     * 流信息(客户端暂不支持)
     * @see <a href="http://redis.io/commands/xinfo">Redis Documentation: XINFO STREAM</a>
     * @since redis 5.0.0
     * @param key 键
     * @return 返回流信息
     */
    private Object streamInfo(String key) {
        CustomCommandHandler customCommandHandler = RedisUtil.getCustomCommandHandler(this.dbIndex);
        return RedisUtil.getCustomCommandHandler(this.dbIndex).execute(
                "XINFO",
                customCommandHandler.serialize("STREAM"),
                customCommandHandler.serialize(key)
        );
    }

    /**
     * 消费者组信息(客户端暂不支持)
     * @see <a href="http://redis.io/commands/xinfo">Redis Documentation: XINFO GROUPS</a>
     * @since redis 5.0.0
     * @param key 键
     * @return 组信息
     */
    private Object groupInfo(String key) {
        CustomCommandHandler customCommandHandler = RedisUtil.getCustomCommandHandler(this.dbIndex);
        return customCommandHandler.execute(
                "XINFO",
                customCommandHandler.serialize("GROUPS"),
                customCommandHandler.serialize(key)
        );
    }

    /**
     * 消费者信息(客户端暂不支持)
     * @see <a href="http://redis.io/commands/xinfo">Redis Documentation: XINFO CONSUMERS</a>
     * @since redis 5.0.0
     * @param key 键
     * @param groupName 消费者组名
     * @return 消费者信息
     */
    private Object consumerInfo(String key, String groupName) {
        CustomCommandHandler customCommandHandler = RedisUtil.getCustomCommandHandler(this.dbIndex);
        return customCommandHandler.execute(
                "XINFO",
                customCommandHandler.serialize("CONSUMERS"),
                customCommandHandler.serialize(key),
                customCommandHandler.serialize(groupName)
        );
    }

    /**
     * 获取spring redis模板
     * @return 返回对象模板
     */
    public RedisTemplate<String, Object> getRedisTemplate() {
        return this.redisTemplate;
    }

    /**
     * 创建流偏移对象
     * @param keyRecordIdMap 键与条目id字典
     * @return 返回流偏移对象数组
     */
    private StreamOffset[] createStreamOffset(Map<String, String> keyRecordIdMap) {
        StreamOffset[] streamOffsets = new StreamOffset[keyRecordIdMap.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : keyRecordIdMap.entrySet()) {
            streamOffsets[i] = StreamOffset.create(entry.getKey(), ReadOffset.from(entry.getValue()));
            i++;
        }
        return streamOffsets;
    }

    /**
     * 创建流偏移对象
     * @param key 键
     * @param recordIds 条目id
     * @return 返回流偏移对象数组
     */
    private StreamOffset[] createStreamOffsetByRecordIds(String key, String... recordIds) {
        int len = recordIds.length;
        StreamOffset[] streamOffsets = new StreamOffset[len];
        for (int i = 0; i < len; i++) {
            streamOffsets[i] = StreamOffset.create(key, ReadOffset.from(recordIds[i]));
        }
        return streamOffsets;
    }

    /**
     * 创建流偏移对象
     * @param keys 键
     * @return 返回流偏移对象数组
     */
    private StreamOffset[] createStreamOffsetByKeys(String... keys) {
        int len = keys.length;
        StreamOffset[] streamOffsets = new StreamOffset[len];
        for (int i = 0; i < len; i++) {
            streamOffsets[i] = StreamOffset.fromStart(keys[i]);
        }
        return streamOffsets;
    }

    /**
     * 转换待处理条目结果
     * @param list 条目列表
     * @return 返回待处理条目结果列表
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> toPendingResult(List<Object> list) {
        List<Map<String, Object>> dataList;
        Map<String, Object> data;
        if (list==null) {
            dataList = new ArrayList<>(1);
            data = new HashMap<>(4);
            data.put("recordId", null);
            data.put("consumer", null);
            data.put("time", null);
            data.put("count", 0);
            dataList.add(data);
        }else {
            List<Object> oList = (List<Object>) list.get(0);
            dataList = new ArrayList<>(oList.size());
            List<Object> $dataList;
            for (Object o : oList) {
                $dataList = (List<Object>) o;
                data = new HashMap<>(4);
                data.put("recordId", $dataList.get(0));
                data.put("consumer", $dataList.get(1));
                data.put("time", $dataList.get(2));
                data.put("count", $dataList.get(3));
                dataList.add(data);
            }
        }
        return dataList;
    }

    /**
     * 流数据类型
     */
    public enum StreamDataType {
        /**
         * 全部
         */
        ALL,
        /**
         * 最新
         */
        LATEST,
        /**
         * 最早
         */
        EARLIEST
    }
}
