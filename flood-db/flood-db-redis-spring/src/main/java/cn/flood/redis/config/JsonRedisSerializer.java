package cn.flood.redis.config;

import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import javax.management.openmbean.SimpleType;
import java.nio.charset.StandardCharsets;

/**
 * redis序列化器
 * @author daimm
 * @date 2019/7/5
 * @since 1.8
 */
public class JsonRedisSerializer implements RedisSerializer<Object> {

    /**
     * 序列化
     * @param t 对象
     * @return 返回字节数组
     * @throws SerializationException 序列化异常
     */
    @Override
    public byte[] serialize(Object t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        byte[] bytes;
        if (this.isSimpleType(t)) {
            bytes = t.toString().getBytes(StandardCharsets.UTF_8);
        }else {
            try {
                bytes = JSONUtil.toJsonStr(t).getBytes(StandardCharsets.UTF_8);
            } catch (Exception ex) {
                throw new SerializationException("Could not serialize: " + ex.getMessage(), ex);
            }
        }
        return bytes;
    }

    /**
     * 反序列化
     * @param bytes 字节数组
     * @return 返回对象
     * @throws SerializationException 序列化异常
     */
    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        return this.tryDeserialize(bytes);
    }

    /**
     * 尝试反序列化
     * @param bytes 字节数组
     * @return 返回对象
     * @throws SerializationException 序列化异常
     */
    private Object tryDeserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        String str = new String(bytes, StandardCharsets.UTF_8);
        if (JSONUtil.isTypeJSONArray(str)) {
            return JSONUtil.parseArray(str);
        }else if (JSONUtil.isTypeJSONObject(str)) {
            return JSONUtil.parseObj(str);
        }else {
            return str;
        }
    }

    /**
     * 是否简单类型
     * @param t 实体
     * @return 返回布尔值
     */
    private boolean isSimpleType(Object t) {
        return SimpleType.BYTE.isValue(t) ||
               SimpleType.CHARACTER.isValue(t) ||
               SimpleType.SHORT.isValue(t) ||
               SimpleType.INTEGER.isValue(t) ||
               SimpleType.LONG.isValue(t) ||
               SimpleType.FLOAT.isValue(t) ||
               SimpleType.DOUBLE.isValue(t) ||
               SimpleType.BOOLEAN.isValue(t) ||
               SimpleType.BIGINTEGER.isValue(t) ||
               SimpleType.BIGDECIMAL.isValue(t);
    }
}
