package cn.flood.base.core.json;

import cn.flood.base.core.lang.Exceptions;
import cn.flood.base.core.lang.StringUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 
* <p>Title: JsonUtil</p>  
* <p>Description: (Json工具类)</p>  
* @author mmdai  
* @date 2018年10月23日
 */
@SuppressWarnings("unchecked")
public class JsonUtils {

	/**
	 * Don't let anyone instantiate this class
	 */
	private JsonUtils() {
		throw new AssertionError("Cannot create instance");
	}
	
	private static final Logger log =  LoggerFactory.getLogger(JsonUtils.class);

	private static ObjectMapper defaultMapper;

	static {
		// 默认的ObjectMapper
		defaultMapper = new ObjectMapper();
		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		defaultMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		// 如果存在未知属性，则忽略不报错
		defaultMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 允许key没有双引号
		defaultMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		// 允许key有单引号
		defaultMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	}

	/**
	 * 将对象转化为json数据
	 * @param obj
	 * @return
	 */
	public static String toJSONString(Object obj) {
		return obj != null ? toJSONString(obj, () -> "") : "";
	}

	/**
	 * 将对象转化为json数据
	 * @param obj
	 * @param defaultSupplier lambda 默认值
	 * @return
	 */
	public static String toJSONString(Object obj, Supplier<String> defaultSupplier) {
		try {
			return obj != null ? defaultMapper.writeValueAsString(obj) : defaultSupplier.get();
		} catch (Throwable e) {
			log.error(String.format("toJSONString %s", obj != null ? obj.toString() : "null"), e);
		}
		return defaultSupplier.get();
	}

	/**
	 * 将json数据转化为对象
	 * @param value
	 * @param tClass
	 * @param <T>
	 * @return
	 */
	public static <T> T toJavaObject(String value, Class<T> tClass) {
		return !ObjectUtils.isEmpty(value) ? toJavaObject(value, tClass, () -> null) : null;
	}

	/**
	 * 将json数据转化为对象
	 * @param obj
	 * @param tClass
	 * @param <T>
	 * @return
	 */
	public static <T> T toJavaObject(Object obj, Class<T> tClass) {
		return obj != null ? toJavaObject(toJSONString(obj), tClass, () -> null) : null;
	}

	/**
	 * 将json数据转化为对象
	 * @param value
	 * @param tClass
	 * @param defaultSupplier lambda 默认值
	 * @param <T>
	 * @return
	 */
	public static <T> T toJavaObject(String value, Class<T> tClass, Supplier<T> defaultSupplier) {
		try {
			if (ObjectUtils.isEmpty(value)) {
				return defaultSupplier.get();
			}
			return defaultMapper.readValue(value, tClass);
		} catch (Throwable e) {
//			log.error(String.format("toJavaObject exception: \n %s\n %s", value, tClass), e);
		}
		return defaultSupplier.get();
	}
    
    /**   
    * 获取泛型的Collection Type  
    * @param collectionClass 泛型的Collection   
    * @param elementClasses 元素类   
    * @return JavaType Java类型   
    * @since 1.0   
    */   
	public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
		return defaultMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}

	/**
	 * json数据转java List集合
	 * @param value
	 * @param tClass
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> toJavaObjectList(String value, Class<T> tClass) {
		return !ObjectUtils.isEmpty(value) ? toJavaObjectList(value, tClass, () -> null) : null;
	}

	/**
	 * json数据转java List集合
	 * @param obj
	 * @param tClass
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> toJavaObjectList(Object obj, Class<T> tClass) {
		return obj != null ? toJavaObjectList(toJSONString(obj), tClass, () -> null) : null;
	}

	/**
	 * json数据转java List集合
	 * @param value
	 * @param tClass
	 * @param defaultSupplier lambda 默认值
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> toJavaObjectList(String value, Class<T> tClass, Supplier<List<T>> defaultSupplier) {
		try {
			if (ObjectUtils.isEmpty(value)) {
				return defaultSupplier.get();
			}
			JavaType javaType = defaultMapper.getTypeFactory().constructParametricType(List.class, tClass);
			return defaultMapper.readValue(value, javaType);
		} catch (Throwable e) {
//			log.error(String.format("toJavaObjectList exception \n%s\n%s", value, tClass), e);
		}
		return defaultSupplier.get();
	}

	/**
	 * 简单地直接用json复制或者转换(Cloneable)
	 * @param obj
	 * @param tClass
	 * @param <T>
	 * @return
	 */
	public static <T> T jsonCopy(Object obj, Class<T> tClass) {
		return obj != null ? toJavaObject(toJSONString(obj), tClass) : null;
	}

	/**
	 * json数据转java Map
	 * @param value
	 * @return
	 */
	public static Map<String, Object> toMap(String value) {
		return !ObjectUtils.isEmpty(value) ? toMap(value, () -> null) : null;
	}

	/**
	 * json数据转java Map
	 * @param value
	 * @return
	 */
	public static Map<String, Object> toMap(Object value) {
		return value != null ? toMap(value, () -> null) : null;
	}

	/**
	 * json数据转java Map
	 * @param value
	 * @param defaultSupplier lambda 默认值
	 * @return
	 */
	public static Map<String, Object> toMap(Object value, Supplier<Map<String, Object>> defaultSupplier) {
		if (value == null) {
			return defaultSupplier.get();
		}
		try {
			if (value instanceof Map) {
				return (Map<String, Object>) value;
			}
		} catch (Exception e) {
			log.info("fail to convert" + toJSONString(value), e);
		}
		return toMap(toJSONString(value), defaultSupplier);
	}

	/**
	 * json数据转java Map
	 * @param value
	 * @param defaultSupplier lambda 默认值
	 * @return
	 */
	public static Map<String, Object> toMap(String value, Supplier<Map<String, Object>> defaultSupplier) {
		if (ObjectUtils.isEmpty(value)) {
			return defaultSupplier.get();
		}
		try {
			return toJavaObject(value, LinkedHashMap.class);
		} catch (Exception e) {
			log.error(String.format("toMap exception\n%s", value), e);
		}
		return defaultSupplier.get();
	}

	/**
	 * 是否是json数组
	 * @param str
	 * @return
	 */
	public static boolean isTypeJSONArray(String str) {
		return StringUtils.isBlank(str) ? false : StringUtils.isWrap(StringUtils.trim(str), '[', ']');
	}

	/**
	 * 是否是json对象
	 * @param str
	 * @return
	 */
	public static boolean isTypeJSONObject(String str) {
		return StringUtils.isBlank(str) ? false : StringUtils.isWrap(StringUtils.trim(str), '{', '}');
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	public static List<Object> toList(String value) {
		return !ObjectUtils.isEmpty(value) ? toList(value, () -> null) : null;
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	public static List<Object> toList(Object value) {
		return value != null ? toList(value, () -> null) : null;
	}

	/**
	 *
	 * @param value
	 * @param defaultSuppler
	 * @return
	 */
	public static List<Object> toList(String value, Supplier<List<Object>> defaultSuppler) {
		if (ObjectUtils.isEmpty(value)) {
			return defaultSuppler.get();
		}
		try {
			return toJavaObject(value, List.class);
		} catch (Exception e) {
			log.error("toList exception\n" + value, e);
		}
		return defaultSuppler.get();
	}

	/**
	 * 
	 * @param value
	 * @param defaultSuppler
	 * @return
	 */
	public static List<Object> toList(Object value, Supplier<List<Object>> defaultSuppler) {
		if (value == null) {
			return defaultSuppler.get();
		}
		if (value instanceof List) {
			return (List<Object>) value;
		}
		return toList(toJSONString(value), defaultSuppler);
	}

	/**
	 * 将json字符串转成 JsonNode
	 *
	 * @param jsonString jsonString
	 * @return jsonString json字符串
	 */
	public static JsonNode readTree(String jsonString){
		try {
			return defaultMapper.readTree(jsonString);
		} catch (IOException e) {
			log.error("readTree exception\n", e);
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json字符串转成 JsonNode
	 *
	 * @param in InputStream
	 * @return jsonString json字符串
	 */
	public static JsonNode readTree(InputStream in)  {
		try {
			return defaultMapper.readTree(in);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json字符串转成 JsonNode
	 *
	 * @param content content
	 * @return jsonString json字符串
	 */
	public static JsonNode readTree(byte[] content) {
		try {
			return defaultMapper.readTree(content);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json字符串转成 JsonNode
	 *
	 * @param jsonParser JsonParser
	 * @return jsonString json字符串
	 */
	public static JsonNode readTree(JsonParser jsonParser) {
		try {
			return defaultMapper.readTree(jsonParser);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

}
