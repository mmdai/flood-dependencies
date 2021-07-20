package cn.flood.redis.config.lettuce;

import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.output.*;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.lettuce.core.protocol.CommandType.*;

/**
 * lettuce命令映射
 * 重写org.springframework.data.redis.connection.lettuce.LettuceConnection.TypeHints
 * @author daimm
 * @date 2019/7/4
 * @since 1.8
 */
public class LettuceTypeHint {
    /**
     * 命令输出对象字典
     */
    @SuppressWarnings("rawtypes")
    private static final Map<io.lettuce.core.protocol.CommandType, Class<? extends CommandOutput>> COMMAND_OUTPUT_TYPE_MAPPING = new HashMap<>();
    /**
     * 命令输出对象构造字典
     */
    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, Constructor<CommandOutput>> CONSTRUCTORS = new ConcurrentHashMap<>();

    static {
        // INTEGER
        COMMAND_OUTPUT_TYPE_MAPPING.put(BITCOUNT, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(BITOP, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(BITPOS, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(DBSIZE, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(DECR, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(DECRBY, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(DEL, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(GETBIT, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(HDEL, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(HINCRBY, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(HLEN, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(INCR, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(INCRBY, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(LINSERT, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(LLEN, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(LPUSH, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(LPUSHX, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(LREM, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(PTTL, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(PUBLISH, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(RPUSH, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(RPUSHX, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SADD, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SCARD, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SDIFFSTORE, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SETBIT, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SETRANGE, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SINTERSTORE, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SREM, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SUNIONSTORE, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(STRLEN, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(TTL, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZADD, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZCOUNT, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZINTERSTORE, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZRANK, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZREM, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZREMRANGEBYRANK, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZREMRANGEBYSCORE, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZREVRANK, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZUNIONSTORE, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(PFCOUNT, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(PFMERGE, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(PFADD, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(XACK, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(XDEL, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(XLEN, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(XTRIM, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(TOUCH, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(UNLINK, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(APPEND, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(HSTRLEN, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZCARD, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZLEXCOUNT, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZREMRANGEBYLEX, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(GEOADD, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(GEORADIUS, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(GEORADIUSBYMEMBER, IntegerOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(WAIT, IntegerOutput.class);

        // DOUBLE
        COMMAND_OUTPUT_TYPE_MAPPING.put(HINCRBYFLOAT, DoubleOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(INCRBYFLOAT, DoubleOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZINCRBY, DoubleOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZSCORE, DoubleOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(GEODIST, DoubleOutput.class);

        // SCORED VALUE
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZPOPMIN, ScoredValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZPOPMAX, ScoredValueOutput.class);

        // MAP
        COMMAND_OUTPUT_TYPE_MAPPING.put(CONFIG, MapOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(HGETALL, MapOutput.class);

        // KEY LIST
        COMMAND_OUTPUT_TYPE_MAPPING.put(HKEYS, KeyListOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(KEYS, KeyListOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(PUBSUB, KeyListOutput.class);

        // KEY VALUE
        COMMAND_OUTPUT_TYPE_MAPPING.put(BRPOP, KeyValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(BLPOP, KeyValueOutput.class);

        // SINGLE VALUE
        COMMAND_OUTPUT_TYPE_MAPPING.put(BRPOPLPUSH, ValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ECHO, ValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(GET, ValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(GETRANGE, ValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(GETSET, ValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(HGET, ValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(LINDEX, ValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(LPOP, ValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(RANDOMKEY, ValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(RENAME, ValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(RPOP, ValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(RPOPLPUSH, ValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SPOP, ValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SRANDMEMBER, ValueOutput.class);

        // STATUS VALUE
        COMMAND_OUTPUT_TYPE_MAPPING.put(AUTH, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(PING, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(READONLY, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(READWRITE, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SWAPDB, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(BGREWRITEAOF, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(BGSAVE, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(CLIENT, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(DEBUG, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(DISCARD, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(FLUSHALL, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(FLUSHDB, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(HMSET, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(INFO, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(LSET, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(LTRIM, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(MIGRATE, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(MSET, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(QUIT, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(RESTORE, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SAVE, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SELECT, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SET, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SETEX, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SHUTDOWN, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SLAVEOF, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SYNC, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(TYPE, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(WATCH, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(UNWATCH, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(XADD, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(XGROUP, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(OBJECT, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(PSETEX, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ASKING, StatusOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(CLUSTER, StatusOutput.class);

        // VALUE LIST
        COMMAND_OUTPUT_TYPE_MAPPING.put(HMGET, ValueListOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(MGET, ValueListOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(HVALS, ValueListOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(LRANGE, ValueListOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SORT, ValueListOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZRANGE, ValueListOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZRANGEBYSCORE, ValueListOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZREVRANGE, ValueListOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZREVRANGEBYSCORE, ValueListOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZREVRANGEBYLEX, ValueListOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZRANGEBYLEX, ValueListOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(TIME, ValueListOutput.class);

        // STRING VALUE LIST
        COMMAND_OUTPUT_TYPE_MAPPING.put(GEOHASH, StringValueListOutput.class);

        // BOOLEAN
        COMMAND_OUTPUT_TYPE_MAPPING.put(EXISTS, BooleanOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(EXPIRE, BooleanOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(EXPIREAT, BooleanOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(HEXISTS, BooleanOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(HSET, BooleanOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(HSETNX, BooleanOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(MOVE, BooleanOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(MSETNX, BooleanOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(PERSIST, BooleanOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(PEXPIRE, BooleanOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(PEXPIREAT, BooleanOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(RENAMENX, BooleanOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SETNX, BooleanOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SISMEMBER, BooleanOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SMOVE, BooleanOutput.class);

        // MULTI
        COMMAND_OUTPUT_TYPE_MAPPING.put(EXEC, MultiOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(MULTI, MultiOutput.class);


        // DATE
        COMMAND_OUTPUT_TYPE_MAPPING.put(LASTSAVE, DateOutput.class);

        // GEO COORDINATES LIST
        COMMAND_OUTPUT_TYPE_MAPPING.put(GEOPOS, GeoCoordinatesListOutput.class);


        // VALUE SET
        COMMAND_OUTPUT_TYPE_MAPPING.put(SDIFF, ValueSetOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SINTER, ValueSetOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SMEMBERS, ValueSetOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SUNION, ValueSetOutput.class);

        // KEY VALUE SCORED VALUE
        COMMAND_OUTPUT_TYPE_MAPPING.put(BZPOPMIN, KeyValueScoredValueOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(BZPOPMAX, KeyValueScoredValueOutput.class);

        // STREAM MESSAGE LIST
        COMMAND_OUTPUT_TYPE_MAPPING.put(XCLAIM, StreamMessageListOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(XREVRANGE, StreamMessageListOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(XRANGE, StreamMessageListOutput.class);

        // NESTED MULTI
        COMMAND_OUTPUT_TYPE_MAPPING.put(XPENDING, NestedMultiOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(SLOWLOG, NestedMultiOutput.class);

        // STREAM READ
        COMMAND_OUTPUT_TYPE_MAPPING.put(XREAD, StreamReadOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(XREADGROUP, StreamReadOutput.class);

        // ARRAY
        COMMAND_OUTPUT_TYPE_MAPPING.put(COMMAND, ArrayOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(ROLE, ArrayOutput.class);

        // BYTE ARRAY
        COMMAND_OUTPUT_TYPE_MAPPING.put(DUMP, ArrayOutput.class);
        COMMAND_OUTPUT_TYPE_MAPPING.put(BITFIELD, ArrayOutput.class);

        // KEY SCAN
        COMMAND_OUTPUT_TYPE_MAPPING.put(SCAN, KeyScanOutput.class);

        // MAP SCAN
        COMMAND_OUTPUT_TYPE_MAPPING.put(HSCAN, MapScanOutput.class);

        // VALUE SCAN
        COMMAND_OUTPUT_TYPE_MAPPING.put(SSCAN, ValueScanOutput.class);

        // SCORED VALUE SCAN
        COMMAND_OUTPUT_TYPE_MAPPING.put(ZSCAN, ScoredValueScanOutput.class);
    }

    /**
     * 获取命令输出对象
     * @param command 命令
     * @param key 键
     * @return 返回命令输出对象
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CommandOutput getTypeHint(String command, String key) {
        Class<? extends CommandOutput> type = COMMAND_OUTPUT_TYPE_MAPPING.get(valueOf(command));
        if (type.isAssignableFrom(StreamMessageListOutput.class)) {
            return new StreamMessageListOutput(ByteArrayCodec.INSTANCE, key);
        }else {
            return getTypeHint(type, new ByteArrayOutput<>(ByteArrayCodec.INSTANCE));
        }
    }

    /**
     * 获取命令输出对象
     * @param type 类型
     * @param defaultType 默认类型
     * @return 返回命令输出对象
     */
    @SuppressWarnings("rawtypes")
    private static CommandOutput getTypeHint(Class<? extends CommandOutput> type, CommandOutput defaultType) {
        if (type == null) {
            return defaultType;
        }
        CommandOutput<?, ?, ?> outputType = instanciateCommandOutput(type);
        return outputType != null ? outputType : defaultType;
    }

    /**
     * 初始化命令输出对象
     * @param type 类型
     * @return 返回命令输出对象
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static CommandOutput<?, ?, ?> instanciateCommandOutput(Class<? extends CommandOutput> type) {
        Assert.notNull(type, "Cannot create instance for 'null' type.");
        Constructor<CommandOutput> constructor = CONSTRUCTORS.get(type);
        if (constructor == null) {
            constructor = (Constructor<CommandOutput>) ClassUtils.getConstructorIfAvailable(type, RedisCodec.class);
            CONSTRUCTORS.put(type, constructor);
        }
        return BeanUtils.instantiateClass(constructor, ByteArrayCodec.INSTANCE);
    }
}
