package cn.flood.okhttp.utils;

import cn.flood.Func;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public final class JsonUtil {
    private static Gson gson = (new GsonBuilder()).create();

    private JsonUtil() {
        throw new UnsupportedOperationException();
    }

    public static <T> String toJson(T value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String)value;
        } else {
            UnicodeStringWriter writer = new UnicodeStringWriter();
            gson.toJson(value, writer);
            return writer.toString();
        }
    }

    public static <T> String toJson(T value, String... ignorePropertyNames) {
        if (Func.isEmpty(ignorePropertyNames)) {
            return toJson(value);
        } else {
            UnicodeStringWriter writer = new UnicodeStringWriter();
            (new GsonBuilder()).excludeFieldsWithModifiers(new int[]{8}).excludeFieldsWithModifiers(new int[]{128}).addSerializationExclusionStrategy(new PropertyNameExclusionStrategy(ignorePropertyNames)).create().toJson(value, writer);
            return writer.toString();
        }
    }

    public static <T> String toJson(T value, boolean toUnicode, String... ignorePropertyNames) {
        if (toUnicode) {
            return toJson(value, ignorePropertyNames);
        } else {
            return Func.isEmpty(ignorePropertyNames) ? gson.toJson(value) : (new GsonBuilder()).excludeFieldsWithModifiers(new int[]{8}).excludeFieldsWithModifiers(new int[]{128}).addSerializationExclusionStrategy(new PropertyNameExclusionStrategy(ignorePropertyNames)).create().toJson(value);
        }
    }

    public static <T> String toJson(T value, boolean toUnicode, boolean ignoreNull, String... ignorePropertyNames) {
        return ignoreNull ? toJson(value, toUnicode, ignorePropertyNames) : (new GsonBuilder()).serializeNulls().excludeFieldsWithModifiers(new int[]{8}).excludeFieldsWithModifiers(new int[]{128}).addSerializationExclusionStrategy(new PropertyNameExclusionStrategy(ignorePropertyNames)).create().toJson(value);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (Func.isEmpty(json)) {
            return null;
        } else {
            return clazz == null ? gson.fromJson(json, (new TypeToken<Map<String, Object>>() {
            }).getType()) : gson.fromJson(json, clazz);
        }
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return Func.isEmpty(json) ? null : gson.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String json, TypeRef<T> typeRef) {
        return Func.isEmpty(json) ? null : gson.fromJson(json, typeRef.getType());
    }
}
