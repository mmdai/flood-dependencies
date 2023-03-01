package cn.flood.base.core.trace;

import java.util.UUID;
import org.slf4j.MDC;

/**
 * 日志追踪工具类
 */
public class MDCTraceUtils {

  /**
   * 追踪id的名称
   */
  public static final String KEY_TRACE_ID = "X-B3-TraceId";

  /**
   * 日志链路追踪id信息头
   */
  public static final String TRACE_ID_HEADER = "x-traceId-header";

  /**
   * 上下文
   */
  public static final String CONTEXT_KEY = "CONTEXT_KEY";


  /**
   * filter的优先级，值越低越优先
   */
  public static final int FILTER_ORDER = -1;

  /**
   * 创建traceId并赋值MDC
   */
  public static void addTraceId() {
    MDC.put(KEY_TRACE_ID, createTraceId());
  }

  /**
   * 赋值MDC
   */
  public static void putTraceId(String traceId) {
    MDC.put(KEY_TRACE_ID, traceId);
  }

  /**
   * 获取MDC中的traceId值
   */
  public static String getTraceId() {
    return MDC.get(KEY_TRACE_ID);
  }

  /**
   * 清除MDC的值
   */
  public static void removeTraceId() {
    MDC.remove(KEY_TRACE_ID);
  }

  /**
   * 创建traceId
   */
  public static String createTraceId() {
    return UUID.randomUUID().toString().replace("-", "").toUpperCase();
  }
}
