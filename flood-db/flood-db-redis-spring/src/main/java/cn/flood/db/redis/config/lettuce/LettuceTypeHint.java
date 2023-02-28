package cn.flood.db.redis.config.lettuce;

import static io.lettuce.core.protocol.CommandType.APPEND;
import static io.lettuce.core.protocol.CommandType.ASKING;
import static io.lettuce.core.protocol.CommandType.AUTH;
import static io.lettuce.core.protocol.CommandType.BGREWRITEAOF;
import static io.lettuce.core.protocol.CommandType.BGSAVE;
import static io.lettuce.core.protocol.CommandType.BITCOUNT;
import static io.lettuce.core.protocol.CommandType.BITFIELD;
import static io.lettuce.core.protocol.CommandType.BITOP;
import static io.lettuce.core.protocol.CommandType.BITPOS;
import static io.lettuce.core.protocol.CommandType.BLPOP;
import static io.lettuce.core.protocol.CommandType.BRPOP;
import static io.lettuce.core.protocol.CommandType.BRPOPLPUSH;
import static io.lettuce.core.protocol.CommandType.BZPOPMAX;
import static io.lettuce.core.protocol.CommandType.BZPOPMIN;
import static io.lettuce.core.protocol.CommandType.CLIENT;
import static io.lettuce.core.protocol.CommandType.CLUSTER;
import static io.lettuce.core.protocol.CommandType.COMMAND;
import static io.lettuce.core.protocol.CommandType.CONFIG;
import static io.lettuce.core.protocol.CommandType.DBSIZE;
import static io.lettuce.core.protocol.CommandType.DEBUG;
import static io.lettuce.core.protocol.CommandType.DECR;
import static io.lettuce.core.protocol.CommandType.DECRBY;
import static io.lettuce.core.protocol.CommandType.DEL;
import static io.lettuce.core.protocol.CommandType.DISCARD;
import static io.lettuce.core.protocol.CommandType.DUMP;
import static io.lettuce.core.protocol.CommandType.ECHO;
import static io.lettuce.core.protocol.CommandType.EXEC;
import static io.lettuce.core.protocol.CommandType.EXISTS;
import static io.lettuce.core.protocol.CommandType.EXPIRE;
import static io.lettuce.core.protocol.CommandType.EXPIREAT;
import static io.lettuce.core.protocol.CommandType.FLUSHALL;
import static io.lettuce.core.protocol.CommandType.FLUSHDB;
import static io.lettuce.core.protocol.CommandType.GEOADD;
import static io.lettuce.core.protocol.CommandType.GEODIST;
import static io.lettuce.core.protocol.CommandType.GEOHASH;
import static io.lettuce.core.protocol.CommandType.GEOPOS;
import static io.lettuce.core.protocol.CommandType.GEORADIUS;
import static io.lettuce.core.protocol.CommandType.GEORADIUSBYMEMBER;
import static io.lettuce.core.protocol.CommandType.GET;
import static io.lettuce.core.protocol.CommandType.GETBIT;
import static io.lettuce.core.protocol.CommandType.GETRANGE;
import static io.lettuce.core.protocol.CommandType.GETSET;
import static io.lettuce.core.protocol.CommandType.HDEL;
import static io.lettuce.core.protocol.CommandType.HEXISTS;
import static io.lettuce.core.protocol.CommandType.HGET;
import static io.lettuce.core.protocol.CommandType.HGETALL;
import static io.lettuce.core.protocol.CommandType.HINCRBY;
import static io.lettuce.core.protocol.CommandType.HINCRBYFLOAT;
import static io.lettuce.core.protocol.CommandType.HKEYS;
import static io.lettuce.core.protocol.CommandType.HLEN;
import static io.lettuce.core.protocol.CommandType.HMGET;
import static io.lettuce.core.protocol.CommandType.HMSET;
import static io.lettuce.core.protocol.CommandType.HSCAN;
import static io.lettuce.core.protocol.CommandType.HSET;
import static io.lettuce.core.protocol.CommandType.HSETNX;
import static io.lettuce.core.protocol.CommandType.HSTRLEN;
import static io.lettuce.core.protocol.CommandType.HVALS;
import static io.lettuce.core.protocol.CommandType.INCR;
import static io.lettuce.core.protocol.CommandType.INCRBY;
import static io.lettuce.core.protocol.CommandType.INCRBYFLOAT;
import static io.lettuce.core.protocol.CommandType.INFO;
import static io.lettuce.core.protocol.CommandType.KEYS;
import static io.lettuce.core.protocol.CommandType.LASTSAVE;
import static io.lettuce.core.protocol.CommandType.LINDEX;
import static io.lettuce.core.protocol.CommandType.LINSERT;
import static io.lettuce.core.protocol.CommandType.LLEN;
import static io.lettuce.core.protocol.CommandType.LPOP;
import static io.lettuce.core.protocol.CommandType.LPUSH;
import static io.lettuce.core.protocol.CommandType.LPUSHX;
import static io.lettuce.core.protocol.CommandType.LRANGE;
import static io.lettuce.core.protocol.CommandType.LREM;
import static io.lettuce.core.protocol.CommandType.LSET;
import static io.lettuce.core.protocol.CommandType.LTRIM;
import static io.lettuce.core.protocol.CommandType.MGET;
import static io.lettuce.core.protocol.CommandType.MIGRATE;
import static io.lettuce.core.protocol.CommandType.MOVE;
import static io.lettuce.core.protocol.CommandType.MSET;
import static io.lettuce.core.protocol.CommandType.MSETNX;
import static io.lettuce.core.protocol.CommandType.MULTI;
import static io.lettuce.core.protocol.CommandType.OBJECT;
import static io.lettuce.core.protocol.CommandType.PERSIST;
import static io.lettuce.core.protocol.CommandType.PEXPIRE;
import static io.lettuce.core.protocol.CommandType.PEXPIREAT;
import static io.lettuce.core.protocol.CommandType.PFADD;
import static io.lettuce.core.protocol.CommandType.PFCOUNT;
import static io.lettuce.core.protocol.CommandType.PFMERGE;
import static io.lettuce.core.protocol.CommandType.PING;
import static io.lettuce.core.protocol.CommandType.PSETEX;
import static io.lettuce.core.protocol.CommandType.PTTL;
import static io.lettuce.core.protocol.CommandType.PUBLISH;
import static io.lettuce.core.protocol.CommandType.PUBSUB;
import static io.lettuce.core.protocol.CommandType.QUIT;
import static io.lettuce.core.protocol.CommandType.RANDOMKEY;
import static io.lettuce.core.protocol.CommandType.READONLY;
import static io.lettuce.core.protocol.CommandType.READWRITE;
import static io.lettuce.core.protocol.CommandType.RENAME;
import static io.lettuce.core.protocol.CommandType.RENAMENX;
import static io.lettuce.core.protocol.CommandType.RESTORE;
import static io.lettuce.core.protocol.CommandType.ROLE;
import static io.lettuce.core.protocol.CommandType.RPOP;
import static io.lettuce.core.protocol.CommandType.RPOPLPUSH;
import static io.lettuce.core.protocol.CommandType.RPUSH;
import static io.lettuce.core.protocol.CommandType.RPUSHX;
import static io.lettuce.core.protocol.CommandType.SADD;
import static io.lettuce.core.protocol.CommandType.SAVE;
import static io.lettuce.core.protocol.CommandType.SCAN;
import static io.lettuce.core.protocol.CommandType.SCARD;
import static io.lettuce.core.protocol.CommandType.SDIFF;
import static io.lettuce.core.protocol.CommandType.SDIFFSTORE;
import static io.lettuce.core.protocol.CommandType.SELECT;
import static io.lettuce.core.protocol.CommandType.SET;
import static io.lettuce.core.protocol.CommandType.SETBIT;
import static io.lettuce.core.protocol.CommandType.SETEX;
import static io.lettuce.core.protocol.CommandType.SETNX;
import static io.lettuce.core.protocol.CommandType.SETRANGE;
import static io.lettuce.core.protocol.CommandType.SHUTDOWN;
import static io.lettuce.core.protocol.CommandType.SINTER;
import static io.lettuce.core.protocol.CommandType.SINTERSTORE;
import static io.lettuce.core.protocol.CommandType.SISMEMBER;
import static io.lettuce.core.protocol.CommandType.SLAVEOF;
import static io.lettuce.core.protocol.CommandType.SLOWLOG;
import static io.lettuce.core.protocol.CommandType.SMEMBERS;
import static io.lettuce.core.protocol.CommandType.SMOVE;
import static io.lettuce.core.protocol.CommandType.SORT;
import static io.lettuce.core.protocol.CommandType.SPOP;
import static io.lettuce.core.protocol.CommandType.SRANDMEMBER;
import static io.lettuce.core.protocol.CommandType.SREM;
import static io.lettuce.core.protocol.CommandType.SSCAN;
import static io.lettuce.core.protocol.CommandType.STRLEN;
import static io.lettuce.core.protocol.CommandType.SUNION;
import static io.lettuce.core.protocol.CommandType.SUNIONSTORE;
import static io.lettuce.core.protocol.CommandType.SWAPDB;
import static io.lettuce.core.protocol.CommandType.SYNC;
import static io.lettuce.core.protocol.CommandType.TIME;
import static io.lettuce.core.protocol.CommandType.TOUCH;
import static io.lettuce.core.protocol.CommandType.TTL;
import static io.lettuce.core.protocol.CommandType.TYPE;
import static io.lettuce.core.protocol.CommandType.UNLINK;
import static io.lettuce.core.protocol.CommandType.UNWATCH;
import static io.lettuce.core.protocol.CommandType.WAIT;
import static io.lettuce.core.protocol.CommandType.WATCH;
import static io.lettuce.core.protocol.CommandType.XACK;
import static io.lettuce.core.protocol.CommandType.XADD;
import static io.lettuce.core.protocol.CommandType.XCLAIM;
import static io.lettuce.core.protocol.CommandType.XDEL;
import static io.lettuce.core.protocol.CommandType.XGROUP;
import static io.lettuce.core.protocol.CommandType.XLEN;
import static io.lettuce.core.protocol.CommandType.XPENDING;
import static io.lettuce.core.protocol.CommandType.XRANGE;
import static io.lettuce.core.protocol.CommandType.XREAD;
import static io.lettuce.core.protocol.CommandType.XREADGROUP;
import static io.lettuce.core.protocol.CommandType.XREVRANGE;
import static io.lettuce.core.protocol.CommandType.XTRIM;
import static io.lettuce.core.protocol.CommandType.ZADD;
import static io.lettuce.core.protocol.CommandType.ZCARD;
import static io.lettuce.core.protocol.CommandType.ZCOUNT;
import static io.lettuce.core.protocol.CommandType.ZINCRBY;
import static io.lettuce.core.protocol.CommandType.ZINTERSTORE;
import static io.lettuce.core.protocol.CommandType.ZLEXCOUNT;
import static io.lettuce.core.protocol.CommandType.ZPOPMAX;
import static io.lettuce.core.protocol.CommandType.ZPOPMIN;
import static io.lettuce.core.protocol.CommandType.ZRANGE;
import static io.lettuce.core.protocol.CommandType.ZRANGEBYLEX;
import static io.lettuce.core.protocol.CommandType.ZRANGEBYSCORE;
import static io.lettuce.core.protocol.CommandType.ZRANK;
import static io.lettuce.core.protocol.CommandType.ZREM;
import static io.lettuce.core.protocol.CommandType.ZREMRANGEBYLEX;
import static io.lettuce.core.protocol.CommandType.ZREMRANGEBYRANK;
import static io.lettuce.core.protocol.CommandType.ZREMRANGEBYSCORE;
import static io.lettuce.core.protocol.CommandType.ZREVRANGE;
import static io.lettuce.core.protocol.CommandType.ZREVRANGEBYLEX;
import static io.lettuce.core.protocol.CommandType.ZREVRANGEBYSCORE;
import static io.lettuce.core.protocol.CommandType.ZREVRANK;
import static io.lettuce.core.protocol.CommandType.ZSCAN;
import static io.lettuce.core.protocol.CommandType.ZSCORE;
import static io.lettuce.core.protocol.CommandType.ZUNIONSTORE;
import static io.lettuce.core.protocol.CommandType.valueOf;

import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.output.ArrayOutput;
import io.lettuce.core.output.BooleanOutput;
import io.lettuce.core.output.ByteArrayOutput;
import io.lettuce.core.output.CommandOutput;
import io.lettuce.core.output.DateOutput;
import io.lettuce.core.output.DoubleOutput;
import io.lettuce.core.output.GeoCoordinatesListOutput;
import io.lettuce.core.output.IntegerOutput;
import io.lettuce.core.output.KeyListOutput;
import io.lettuce.core.output.KeyScanOutput;
import io.lettuce.core.output.KeyValueOutput;
import io.lettuce.core.output.KeyValueScoredValueOutput;
import io.lettuce.core.output.MapOutput;
import io.lettuce.core.output.MapScanOutput;
import io.lettuce.core.output.MultiOutput;
import io.lettuce.core.output.NestedMultiOutput;
import io.lettuce.core.output.ScoredValueOutput;
import io.lettuce.core.output.ScoredValueScanOutput;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.output.StreamMessageListOutput;
import io.lettuce.core.output.StreamReadOutput;
import io.lettuce.core.output.StringValueListOutput;
import io.lettuce.core.output.ValueListOutput;
import io.lettuce.core.output.ValueOutput;
import io.lettuce.core.output.ValueScanOutput;
import io.lettuce.core.output.ValueSetOutput;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * lettuce命令映射 重写org.springframework.data.redis.connection.lettuce.LettuceConnection.TypeHints
 *
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
   *
   * @param command 命令
   * @param key     键
   * @return 返回命令输出对象
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static CommandOutput getTypeHint(String command, String key) {
    Class<? extends CommandOutput> type = COMMAND_OUTPUT_TYPE_MAPPING.get(valueOf(command));
    if (type.isAssignableFrom(StreamMessageListOutput.class)) {
      return new StreamMessageListOutput(ByteArrayCodec.INSTANCE, key);
    } else {
      return getTypeHint(type, new ByteArrayOutput<>(ByteArrayCodec.INSTANCE));
    }
  }

  /**
   * 获取命令输出对象
   *
   * @param type        类型
   * @param defaultType 默认类型
   * @return 返回命令输出对象
   */
  @SuppressWarnings("rawtypes")
  private static CommandOutput getTypeHint(Class<? extends CommandOutput> type,
      CommandOutput defaultType) {
    if (type == null) {
      return defaultType;
    }
    CommandOutput<?, ?, ?> outputType = instanciateCommandOutput(type);
    return outputType != null ? outputType : defaultType;
  }

  /**
   * 初始化命令输出对象
   *
   * @param type 类型
   * @return 返回命令输出对象
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private static CommandOutput<?, ?, ?> instanciateCommandOutput(
      Class<? extends CommandOutput> type) {
    Assert.notNull(type, "Cannot create instance for 'null' type.");
    Constructor<CommandOutput> constructor = CONSTRUCTORS.get(type);
    if (constructor == null) {
      constructor = (Constructor<CommandOutput>) ClassUtils
          .getConstructorIfAvailable(type, RedisCodec.class);
      CONSTRUCTORS.put(type, constructor);
    }
    return BeanUtils.instantiateClass(constructor, ByteArrayCodec.INSTANCE);
  }
}
