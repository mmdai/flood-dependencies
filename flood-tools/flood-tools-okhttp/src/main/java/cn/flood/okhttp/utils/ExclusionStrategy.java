package cn.flood.okhttp.utils;

import com.google.gson.FieldAttributes;

public interface ExclusionStrategy {

    boolean shouldSkipField(FieldAttributes var1);

    boolean shouldSkipClass(Class<?> var1);
}
