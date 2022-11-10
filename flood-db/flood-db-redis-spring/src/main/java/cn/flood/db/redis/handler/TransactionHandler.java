package cn.flood.db.redis.handler;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.HashMapper;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * 事务助手
* @author daimm
 * @date 2019/7/17
 * @since 1.8
 */
public final class TransactionHandler implements RedisHandler{
    /**
     * 对象模板
     */
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 字符串模板
     */
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 数据库索引
     */
    private int dbIndex;

    /**
     * 事务助手构造
     * @param dbIndex 数据库索引
     */
    @SuppressWarnings("unchecked")
    TransactionHandler(Integer dbIndex) {
        this.dbIndex = dbIndex;
        List<RedisTemplate> templateList = HandlerManager.createTemplate(this.dbIndex);
        this.redisTemplate = templateList.get(0);
        this.stringRedisTemplate = (StringRedisTemplate) templateList.get(1);
    }

    /**
     * 执行对象事务
     * @param executor 执行器
     * @return 返回结果列表
     */
    public List executeAsObj(Function<TransactionHandler, List> executor) {
        return this.execute(executor, this.redisTemplate);
    }

    /**
     * 执行字符串事务
     * @param executor 执行器
     * @return 返回结果列表
     */
    public List execute(Function<TransactionHandler, List> executor) {
        return this.execute(executor, this.stringRedisTemplate);
    }

    /**
     * 监控对象键
     * @see <a href="http://redis.io/commands/watch">Redis Documentation: WATCH</a>
     * @since redis 2.2.0
     * @param keys 键
     */
    public void watchAsObj(String ...keys) {
        this.redisTemplate.watch(Arrays.asList(keys));
    }

    /**
     * 监控字符串键
     * @see <a href="http://redis.io/commands/watch">Redis Documentation: WATCH</a>
     * @since redis 2.2.0
     * @param keys 键
     */
    public void watch(String ...keys) {
        this.stringRedisTemplate.watch(Arrays.asList(keys));
    }

    /**
     * 取消监控对象键
     * @see <a href="http://redis.io/commands/unwatch">Redis Documentation: UNWATCH</a>
     * @since redis 2.2.0
     */
    public void unwatchAsObj() {
        this.redisTemplate.unwatch();
    }

    /**
     * 取消监控字符串键
     * @see <a href="http://redis.io/commands/unwatch">Redis Documentation: UNWATCH</a>
     * @since redis 2.2.0
     */
    public void unwatch() {
        this.stringRedisTemplate.unwatch();
    }

    /**
     * 开始对象事务
     * @since redis 1.2.0
     * @see <a href="http://redis.io/commands/multi">Redis Documentation: MULTI</a>
     */
    public void beginTransactionAsObj() {
        this.redisTemplate.multi();
    }

    /**
     * 开始字符串事务
     * @since redis 1.2.0
     * @see <a href="http://redis.io/commands/multi">Redis Documentation: MULTI</a>
     */
    public void beginTransaction() {
        this.stringRedisTemplate.multi();
    }

    /**
     * 提交对象事务
     * @since redis 1.2.0
     * @see <a href="http://redis.io/commands/exec">Redis Documentation: EXEC</a>
     * @return 返回执行命令列表
     */
    public List commitAsObj() {
        return this.redisTemplate.exec();
    }

    /**
     * 提交字符串事务
     * @since redis 1.2.0
     * @see <a href="http://redis.io/commands/exec">Redis Documentation: EXEC</a>
     * @return 返回执行命令列表
     */
    public List commit() {
        return this.stringRedisTemplate.exec();
    }

    /**
     * 取消对象事务
     * @see <a href="http://redis.io/commands/discard">Redis Documentation: DISCARD</a>
     * @since redis 2.0.0
     */
    public void cancelTransactionAsObj() {
        this.redisTemplate.discard();
    }

    /**
     * 取消字符串事务
     * @see <a href="http://redis.io/commands/discard">Redis Documentation: DISCARD</a>
     * @since redis 2.0.0
     */
    public void cancelTransaction() {
        this.stringRedisTemplate.discard();
    }

    /**
     * 获取数据库助手
     * @return 返回数据库助手
     */
    public DBHandler getDBHandler() {
        return new DBHandler(this);
    }

    /**
     * 获取键助手
     * @return 返回键助手
     */
    public KeyHandler getKeyHandler() {
        return new KeyHandler(this);
    }

    /**
     * 获取数字助手
     * @return 返回数字助手
     */
    public NumberHandler getNumberHandler() {
        return new NumberHandler(this);
    }

    /**
     * 获取字符串助手
     * @return 返回字符串助手
     */
    public StringHandler getStringHandler() {
        return new StringHandler(this);
    }

    /**
     * 获取哈希助手
     * @return 返回哈希助手
     */
    public HashHandler getHashHandler() {
        return new HashHandler(this);
    }

    /**
     * 获取列表助手
     * @return 返回列表助手
     */
    public ListHandler getListHandler() {
        return new ListHandler(this);
    }

    /**
     * 获取无序集合助手
     * @return 返回无序集合助手
     */
    public SetHandler getSetHandler() {
        return new SetHandler(this);
    }

    /**
     * 获取有序集合助手
     * @return 返回有序集合助手
     */
    public ZsetHandler getZsetHandler() {
        return new ZsetHandler(this);
    }

    /**
     * 获取基数助手
     * @return 返回基数助手
     */
    public HyperLogLogHandler getHyperLogLogHandler() {
        return new HyperLogLogHandler(this);
    }

    /**
     * 获取位图助手
     * @return 返回位图助手
     */
    public BitmapHandler getBitmapHandler() {
        return new BitmapHandler(this);
    }

    /**
     * 获取地理位置助手
     * @return 返回地理位置助手
     */
    public GeoHandler getGeoHandler() {
        return new GeoHandler(this);
    }

    /**
     * 获取lua脚本助手
     * @return 返回lua脚本助手
     */
    public ScriptHandler getScriptHandler() {
        return new ScriptHandler(this);
    }

    /**
     * 获取发布订阅助手
     * @return 返回发布订阅助手
     */
    public PubSubHandler getPubSubHandler() {
        return new PubSubHandler(this);
    }

    /**
     * 获取流助手
     * @return 返回流助手
     */
    public StreamHandler getStreamHandler() {
        return new StreamHandler(this);
    }

    /**
     * 获取流助手
     * @param hashMapper 哈希映射
     * @return 返回流助手
     */
    public StreamHandler getStreamHandler(HashMapper<String, String, Object> hashMapper) {
        return new StreamHandler(this, hashMapper);
    }

    /**
     * 获取哨兵助手
     * @return 返回哨兵助手
     */
    public SentinelHandler getSentinelHandler() {
        return new SentinelHandler(this);
    }

    /**
     * 获取集群助手
     * @return 返回集群助手
     */
    public ClusterHandler getClusterHandler() {
        return new ClusterHandler(this);
    }

    /**
     * 获取自定义命令助手
     * @return 返回自定义命令助手
     */
    public CustomCommandHandler getCustomCommandHandler() {
        return new CustomCommandHandler(this);
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

    /**
     * 获取数据库索引
     * @return 返回数据库索引
     */
    public int getDbIndex() {
        return this.dbIndex;
    }

    /**
     * 执行事务
     * @param executor 执行器
     * @param redisTemplate redis模板
     * @return 返回结果列表
     */
    private List execute(Function<TransactionHandler, List> executor, RedisTemplate redisTemplate) {
        RedisConnectionFactory factory = redisTemplate.getRequiredConnectionFactory();
        // 绑定连接
        RedisConnectionUtils.bindConnection(factory, true);
        try {
            return executor.apply(this);
        } finally {
            // 解绑连接
            RedisConnectionUtils.unbindConnection(factory);
        }
    }
}


