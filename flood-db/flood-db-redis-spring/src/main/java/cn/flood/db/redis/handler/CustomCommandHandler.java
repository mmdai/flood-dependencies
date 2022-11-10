package cn.flood.db.redis.handler;

import cn.flood.db.redis.util.ConvertUtil;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.core.RedisTemplate;
import cn.flood.db.redis.config.lettuce.LettuceTypeHint;

/**
 * 自定义命令助手
 * @author daimm
 * @date 2019/6/6
 * @since 1.8
 */
public final class CustomCommandHandler implements RedisHandler {
    /**
     * 对象模板
     */
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 自定义命令助手构造
     * @param dbIndex 数据库索引
     */
    CustomCommandHandler(Integer dbIndex) {
        this.redisTemplate = HandlerManager.createRedisTemplate(dbIndex);
    }

    /**
     * 自定义命令助手构造
     * @param transactionHandler 事务助手
     */
    CustomCommandHandler(TransactionHandler transactionHandler) {
        this.redisTemplate = transactionHandler.getRedisTemplate();
    }

    /**
     * 执行redis命令
     * @param command 命令
     * @param args 参数列表
     * @return 返回信息
     */
    public Object execute(String command, byte[] ...args) {
        return this.executeCommand(command, args);
    }

    /**
     * 序列化对象
     * @param value 对象
     * @return 返回字节数组
     */
    public byte[] serializeAsObj(Object value) {
        return ConvertUtil.toBytes(this.redisTemplate.getValueSerializer(), value);
    }

    /**
     * 序列化字符串
     * @param value 字符串
     * @return 返回字节数组
     */
    public byte[] serialize(String value) {
        return ConvertUtil.toBytes(this.redisTemplate.getKeySerializer(), value);
    }

    /**
     * 反序列化对象
     * @param bytes 字节数组
     * @param <T> 返回类型
     * @return 返回对象
     */
    @SuppressWarnings("unchecked")
    public <T> T deserializeAsObj(byte[] bytes) {
        return (T) ConvertUtil.toJavaType(this.redisTemplate.getValueSerializer().deserialize(bytes), Object.class);
    }

    /**
     * 反序列化对象
     * @param type 返回值类型
     * @param bytes 字节数组
     * @param <T> 返回类型
     * @return 返回对象
     */
    @SuppressWarnings("unchecked")
    public <T> T deserializeAsObj(Class<T> type, byte[] bytes) {
        return (T) ConvertUtil.toJavaType(this.redisTemplate.getValueSerializer().deserialize(bytes), type);
    }

    /**
     * 反序列化字符串
     * @param bytes 字节数组
     * @param <T> 返回类型
     * @return 返回对象
     */
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes) {
        return (T) this.redisTemplate.getKeySerializer().deserialize(bytes);
    }

    /**
     * 获取spring redis模板
     * @return 返回对象模板
     */
    public RedisTemplate<String, Object> getRedisTemplate() {
        return this.redisTemplate;
    }

    /**
     * 执行命令
     * @param command 命令
     * @param args 参数列表
     * @return 返回结果
     */
    private Object executeCommand(String command, byte[] ...args) {
        RedisConnection connection = this.redisTemplate.getRequiredConnectionFactory().getConnection();
        if (connection instanceof LettuceConnection) {
            return this.executeCommandByLettuce(connection, command, args);
        }
        return connection.execute(command, args);
    }

    /**
     * 执行命令(* @author daimm)
     * @param connection 连接对象
     * @param command 命令
     * @param args 参数列表
     * @return 返回结果
     */
    private Object executeCommandByLettuce(RedisConnection connection, String command, byte[] ...args) {
        if (args.length>0) {
            return ((LettuceConnection) connection).execute(
                    command,
                    LettuceTypeHint.getTypeHint(command, this.deserialize(args[0])),
                    args
            );
        }else {
            return ((LettuceConnection) connection).execute(
                    command,
                    LettuceTypeHint.getTypeHint(command, null),
                    args
            );
        }
    }
}
