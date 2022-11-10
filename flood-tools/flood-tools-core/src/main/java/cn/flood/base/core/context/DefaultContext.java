package cn.flood.base.core.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultContext implements RequestContext {
    private final Map<String, Object> attributes = new HashMap<>();

    @Override
    public RequestContext add(String key, Object value) {
        attributes.put(key, value);
        return this;
    }

    @Override
    public Object get(String key) {
        return attributes.get(key);
    }

    @Override
    public RequestContext remove(String key) {
        attributes.remove(key);
        return this;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }
}
