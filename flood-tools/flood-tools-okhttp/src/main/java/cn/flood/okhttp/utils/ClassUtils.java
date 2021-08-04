package cn.flood.okhttp.utils;

import cn.flood.lang.Assert;

import java.lang.reflect.*;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

public class ClassUtils {

    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap(8);
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap(8);

    public ClassUtils() {
    }

    public static boolean isAssignable(Class<?> sourceType, Class<?> targetType) {
        Assert.notNull(sourceType, "Source type must not be null");
        Assert.notNull(targetType, "Target side type must not be null");
        if (sourceType.isAssignableFrom(targetType)) {
            return true;
        } else {
            Class resolvedPrimitive;
            if (sourceType.isPrimitive()) {
                resolvedPrimitive = (Class)primitiveWrapperTypeMap.get(targetType);
                if (sourceType == resolvedPrimitive) {
                    return true;
                }
            } else {
                resolvedPrimitive = (Class)primitiveTypeToWrapperMap.get(targetType);
                if (resolvedPrimitive != null && sourceType.isAssignableFrom(resolvedPrimitive)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type rawType = parameterizedType.getRawType();
            Assert.isTrue(rawType instanceof Class, "The rawType is not Class.");
            return (Class)rawType;
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType)type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();
        } else if (type instanceof TypeVariable) {
            return Object.class;
        } else if (type instanceof WildcardType) {
            return getRawType(((WildcardType)type).getUpperBounds()[0]);
        } else {
            String className = type == null ? "null" : type.getClass().getName();
            throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + className);
        }
    }

    static {
        primitiveWrapperTypeMap.put(Boolean.class, Boolean.TYPE);
        primitiveWrapperTypeMap.put(Byte.class, Byte.TYPE);
        primitiveWrapperTypeMap.put(Character.class, Character.TYPE);
        primitiveWrapperTypeMap.put(Double.class, Double.TYPE);
        primitiveWrapperTypeMap.put(Float.class, Float.TYPE);
        primitiveWrapperTypeMap.put(Integer.class, Integer.TYPE);
        primitiveWrapperTypeMap.put(Long.class, Long.TYPE);
        primitiveWrapperTypeMap.put(Short.class, Short.TYPE);
        Iterator var0 = primitiveWrapperTypeMap.entrySet().iterator();

        while(var0.hasNext()) {
            Map.Entry<Class<?>, Class<?>> entry = (Map.Entry)var0.next();
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
        }

    }
}
