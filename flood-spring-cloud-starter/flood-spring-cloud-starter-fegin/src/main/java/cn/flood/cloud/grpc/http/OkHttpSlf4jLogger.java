package cn.flood.cloud.grpc.http;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OkHttp Slf4j logger
 *
 * @author mmdai
 */
public class OkHttpSlf4jLogger implements HttpLoggingInterceptor.Logger {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public void log(String message) {
    log.info(message);
  }
}
