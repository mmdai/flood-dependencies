package cn.flood.okhttp.utils;

import cn.flood.lang.ArrayUtils;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

class PropertyNameExclusionStrategy implements ExclusionStrategy {
    private final String[] ignorePropertyNames;

    PropertyNameExclusionStrategy(String[] ignorePropertyNames) {
        this.ignorePropertyNames = ignorePropertyNames;
    }

    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return this.ignorePropertyNames != null && ArrayUtils.containsElement(this.ignorePropertyNames, fieldAttributes.getName());
    }

    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
