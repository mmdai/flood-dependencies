package cn.flood.redis.util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;
import cn.flood.redis.handler.StreamHandler;

import javax.management.openmbean.SimpleType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 转换工具
* @author daimm
 * @date 2019/4/28
 * @since 1.8
 */
public class ConvertUtil {

    /**
     * 转为二维字节数组
     * @param serializer 序列化器
     * @param args 参数
     * @return 返回二位字节数组
     */
    public static byte[][] toByteArray(RedisSerializer serializer, String ...args) {
        byte[][] bytes = new byte[args.length][];
        for (int i = 0; i < args.length; i++) {
            bytes[i] = toBytes(serializer, args[i]);
        }
        return bytes;
    }

    /**
     * 转为二维字节数组
     * @param serializer 序列化器
     * @param args2 参数列表2
     * @param args1 参数列表1
     * @return 返回二位字节数组
     */
    public static byte[][] toByteArray(RedisSerializer serializer, String[] args2, String ...args1) {
        int len1 = args1.length;
        int len2 = args2==null? 0 : args2.length;
        byte[][] bytes = new byte[len1+len2][];
        for (int i = 0; i < len1; i++) {
            bytes[i] = toBytes(serializer, args1[i]);
        }
        for (int i = 0; i < len2; i++) {
            bytes[i+len1] = toBytes(serializer, args2[i]);
        }
        return bytes;
    }

    /**
     * 转为二维字节数组
     * @param keySerializer 键序列化器
     * @param argsSerializer 参数序列化器
     * @param keys 键列表
     * @param args 参数列表
     * @param <K> 键类型
     * @return 返回键和参数的二维数组
     */
    @SuppressWarnings("unchecked")
    public static <K> byte[][] toByteArray(RedisSerializer keySerializer, RedisSerializer argsSerializer, List<K> keys, Object[] args) {
        Assert.notNull(keySerializer, "keySerializer must not be null");
        Assert.notNull(argsSerializer, "argsSerializer must not be null");

        final int keySize = keys != null ? keys.size() : 0;
        byte[][] keysAndArgs = new byte[args.length + keySize][];
        int i = 0;
        if (keys != null) {
            for (K key : keys) {
                if (key instanceof byte[]) {
                    keysAndArgs[i++] = (byte[]) key;
                } else {
                    keysAndArgs[i++] = keySerializer.serialize(key);
                }
            }
        }
        for (Object arg : args) {
            if (arg instanceof byte[]) {
                keysAndArgs[i++] = (byte[]) arg;
            } else {
                keysAndArgs[i++] = argsSerializer.serialize(arg);
            }
        }
        return keysAndArgs;
    }

    /**
     * 将字典转为TypedTuple类型的集合
     * @param map 字典
     * @param <T> 对象类型
     * @return 返回TypedTuple类型的集合
     */
    public static  <T> Set<ZSetOperations.TypedTuple<T>> toTypedTupleSet(Map<Double, T> map) {
        if(map==null){
            return null;
        }
        Set<ZSetOperations.TypedTuple<T>> set = new HashSet<>(map.size());
        for (Map.Entry<Double, T> entry : map.entrySet()) {
            set.add(new DefaultTypedTuple<>(entry.getValue(), entry.getKey()));
        }
        return set;
    }

    public static <T> List<T> toList(GeoResults<RedisGeoCommands.GeoLocation<T>> results) {
        List<T> list;
        if (results!=null) {
            list = new ArrayList<>(results.getContent().size());
            for (GeoResult<RedisGeoCommands.GeoLocation<T>> result : results) {
                list.add(result.getContent().getName());
            }
        }else {
            list = new ArrayList<>(0);
        }
        return list;
    }

    /**
     * 转换为字典
     * @param results 结果集
     * @param <T> 类型
     * @return 返回字典
     */
    public static <T> Map<T, Point> toMap(GeoResults<RedisGeoCommands.GeoLocation<T>> results) {
        Map<T, Point> map;
        if (results!=null) {
            map = new HashMap<>(results.getContent().size());
            for (GeoResult<RedisGeoCommands.GeoLocation<T>> result : results) {
                map.put(result.getContent().getName(), result.getContent().getPoint());
            }
        }else {
            map = new HashMap<>(0);
        }
        return map;
    }

    /**
     * 将TypedTuple类型的集合转为字典
     * @param set TypedTuple类型的集合
     * @param <T> 对象类型
     * @return 返回TypedTuple类型的集合
     */
    public static <T> Map<Double, T> toMap(Set<ZSetOperations.TypedTuple<T>> set) {
        if(set==null){
            return null;
        }
        Map<Double, T> map = new LinkedHashMap<>(set.size());
        for (ZSetOperations.TypedTuple<T> typedTuple : set) {
            map.put(typedTuple.getScore(), typedTuple.getValue());
        }
        return map;
    }

    /**
     * 将对象转为字典
     * @param values 对象
     * @param <T> 对象类型
     * @return 返回对象字典
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<Double, T> toMap(T ...values) {
        if(values==null){
            return null;
        }
        Map<Double, T> map = new HashMap<>(values.length);
        for (int i = 0; i < values.length; i++) {
            map.put((double) i, values[i]);
        }
        return map;
    }

    /**
     * 将列表转为字典
     * @param list 列表
     * @param type 数据类型
     * @return 返回字典
     */
    public static <T> Map<String, T> toMap(List<? extends Record<String, T>> list, StreamHandler.StreamDataType type) {
        Map<String, T> data;
        if (list!=null&&list.size()>0) {
            if (type== StreamHandler.StreamDataType.LATEST) {
                data = new HashMap<>(1);
                Record<String, T> record = list.get(list.size()-1);
                data.put(record.getId().getValue(), record.getValue());
            }else if (type== StreamHandler.StreamDataType.EARLIEST) {
                data = new HashMap<>(1);
                Record<String, T> record = list.get(0);
                data.put(record.getId().getValue(), record.getValue());
            }else {
                data = new HashMap<>(list.size());
                for (Record<String, T> records : list) {
                    data.put(records.getId().getValue(), records.getValue());
                }
            }
        }else {
            data = new HashMap<>(0);
        }
        return data;
    }

    /**
     * 转为字节数组
     * @param serializer 序列化器
     * @param value 值
     * @return 返回字节数组
     */
    @SuppressWarnings("unchecked")
    public static byte[] toBytes(RedisSerializer serializer, Object value) {
        Assert.notNull(serializer, "serializer must not be null");

        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        return serializer.serialize(value);
    }

    /**
     * 转为字符串
     * @param serializer 序列化器
     * @param bytes 字节数组
     * @return 返回字符串
     */
    public static String toStr(RedisSerializer serializer, byte[] bytes) {
        Assert.notNull(serializer, "serializer must not be null");

        Object o = serializer.deserialize(bytes);
        return o==null?null:o.toString();
    }

    /**
     * 转为java类型
     * @param t 对象
     * @param type 返回类型
     * @param <T> 返回类型
     * @return 返回对应的java对象
     */
    public static <T> Object toJavaType(Object t, Class<T> type) {
        if (t==null) {
            return null;
        }
        if (t instanceof JSONObject) {
            return JSONUtil.toBean((JSONObject) t, type);
        }else if (t instanceof JSONArray) {
            return JSONUtil.toList((JSONArray) t, type);
        }
        String str = t.toString();
        if (NumberUtil.isNumber(str)) {
            String typeName = type.getName();
            if (NumberUtil.isDouble(str)) {
                BigDecimal value = new BigDecimal(str);
                if (SimpleType.FLOAT.getTypeName().equals(typeName)) {
                    return value.floatValue();
                }
                return value.doubleValue();
            }else {
                BigInteger value = new BigInteger(str);
                if (SimpleType.INTEGER.getTypeName().equals(typeName)) {
                    return value.intValue();
                }else if (SimpleType.SHORT.getTypeName().equals(typeName)) {
                    return value.shortValue();
                }
                return value.longValue();
            }
        }else {
            return t;
        }
    }

    /**
     * 转为java类型
     * @param serializer 序列化器
     * @param result 对象
     * @return 返回对应的java对象
     */
    @SuppressWarnings("unchecked")
    public static Object toJavaType(RedisSerializer serializer, Object result) {
        if (result==null) {
            return null;
        }
        if (result instanceof List) {
            List newList = new ArrayList<>(20);
            parseList(serializer, newList, result);
            return newList;
        }else if (result instanceof byte[]) {
            return serializer.deserialize((byte[]) result);
        }else {
            return result;
        }
    }

    /**
     * 解析list列表
     * @param serializer 序列化器
     * @param list 列表
     * @param result 对象
     */
    @SuppressWarnings("unchecked")
    private static void parseList(RedisSerializer serializer, List<Object> list, Object result) {
        if (result instanceof List) {
            List<Object> resultList = (List<Object>) result;
            List<Object> newList = new ArrayList<>(resultList.size());
            for (Object o : resultList) {
                parseList(serializer, newList, o);
            }
            list.add(newList);
        }else {
            if (result instanceof byte[]) {
                list.add(serializer.deserialize((byte[]) result));
            }else {
                list.add(result);
            }
        }
    }
}
