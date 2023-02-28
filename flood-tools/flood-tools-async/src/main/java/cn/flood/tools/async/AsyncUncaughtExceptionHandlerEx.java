package cn.flood.tools.async;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;


/**
 * <p>Title: AsyncUncaughtExceptionHandlerEx</p>
 * <p>Description: 异步线程错误处理类</p>
 *
 * @author mmdai
 * @date 2018年8月13日
 */
public class AsyncUncaughtExceptionHandlerEx implements AsyncUncaughtExceptionHandler {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void handleUncaughtException(Throwable ex, Method method, Object... params) {
    logger.error("Method Name::{}", method.getName());
    for (Object param : params) {
      logger.error("Parameter value - {}", param);
    }
    ex.printStackTrace();
  }
}
