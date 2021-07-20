package cn.flood.context;

import java.util.Map;

public interface RequestContext {

    RequestContext add(String key, Object value);

    Object get(String key);

    RequestContext remove(String key);

    Map<String, Object> getAttributes();

}
