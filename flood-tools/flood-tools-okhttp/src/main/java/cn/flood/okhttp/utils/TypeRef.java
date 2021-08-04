package cn.flood.okhttp.utils;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Comparator;

public abstract class TypeRef<T> implements Comparator<T> {
    final Type type;
    final Class<? super T> rawType;

    protected TypeRef() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        } else {
            this.type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
            this.rawType = (Class<? super T>) ClassUtils.getRawType(this.type);
        }
    }

    public Type getType() {
        return this.type;
    }

    public Class<? super T> getRawType() {
        return this.rawType;
    }

    public int compare(T o1, T o2) {
        return 0;
    }
}
