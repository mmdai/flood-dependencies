package cn.flood.context;


import com.alibaba.ttl.TransmittableThreadLocal;

public class ContextHolder {
    private static final ThreadLocal<RequestContext> contextHolder = new TransmittableThreadLocal<>();


    public static RequestContext getCurrentContext() {
        return contextHolder.get();
    }
    
    public static void setCurrentContext(RequestContext requestContext) {
        contextHolder.set(requestContext);
    }


    public static void clearCurrentContext() {
        contextHolder.remove();
    }
}
