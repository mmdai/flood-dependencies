package cn.flood.base.core.context;


import com.alibaba.ttl.TransmittableThreadLocal;

public class ContextHolder {

  private static final ThreadLocal<RequestContext> CONTEXT_HOLDER = new TransmittableThreadLocal<>();


  public static RequestContext getCurrentContext() {
    return CONTEXT_HOLDER.get();
  }

  public static void setCurrentContext(RequestContext requestContext) {
    CONTEXT_HOLDER.set(requestContext);
  }


  public static void clearCurrentContext() {
    CONTEXT_HOLDER.remove();
  }
}
