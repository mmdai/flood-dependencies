package cn.flood.cloud.gateway.handler;

import cn.flood.cloud.gateway.result.Result;
import cn.flood.cloud.gateway.result.ResultCode;
import cn.flood.cloud.gateway.result.ResultWapper;
import io.netty.channel.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;


/**
 * 异常处理通知
 *
 * @author mmdai
 */
public class ExceptionHandlerAdvice {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @ExceptionHandler(value = {ResponseStatusException.class})
  public Result<?> handle(ResponseStatusException ex) {
    log.error("response status exception:{}", ex.getMessage());
    if (ex.getMessage().contains(HttpStatus.NOT_FOUND.toString())) {
      return ResultWapper.wrap(ResultCode.NOT_FOUND.getCode(), ResultCode.NOT_FOUND.getMsg());
    } else if (ex.getMessage().contains(HttpStatus.SERVICE_UNAVAILABLE.toString())) {
      return ResultWapper.wrap(ResultCode.SERVICE_UNAVAILABLE.getCode(),
          ResultCode.SERVICE_UNAVAILABLE.getMsg());
    } else if (ex.getMessage().contains(HttpStatus.GATEWAY_TIMEOUT.toString())) {
      return ResultWapper.wrap(ResultCode.GATEWAY_TIMEOUT.getCode(),
          ResultCode.GATEWAY_TIMEOUT.getMsg());
    } else if (ex.getMessage().contains(HttpStatus.TOO_MANY_REQUESTS.toString())) {
      return ResultWapper.wrap(ResultCode.TOO_MANY_REQUESTS.getCode(),
          ResultCode.TOO_MANY_REQUESTS.getMsg());
    } else {
      return ResultWapper.error();
    }
  }

  @ExceptionHandler(value = {ConnectTimeoutException.class})
  public Result<?> handle(ConnectTimeoutException ex) {
    log.error("connect timeout exception:{}", ex.getLocalizedMessage());
    return ResultWapper.wrap(ResultCode.TIME_ERROR.getCode(), ResultCode.TIME_ERROR.getMsg());
  }

  @ExceptionHandler(value = {NotFoundException.class})
  public Result<?> handle(NotFoundException ex) {
    log.error("not found exception:{}", ex.getLocalizedMessage());
    return ResultWapper.wrap(ResultCode.NOT_FOUND.getCode(), ResultCode.NOT_FOUND.getMsg());
  }

  @ExceptionHandler(value = {RuntimeException.class})
  public Result<?> handle(RuntimeException ex) {
    log.error("runtime exception:{}", ex.getLocalizedMessage());
    return ResultWapper.error();
  }

  @ExceptionHandler(value = {Exception.class})
  public Result<?> handle(Exception ex) {
    log.error("exception:{}", ex.getLocalizedMessage());
    return ResultWapper.error();
  }

  @ExceptionHandler(value = {Throwable.class})
  public Result<?> handle(Throwable throwable) {
    Result result = ResultWapper.error();
    if (throwable instanceof ResponseStatusException) {
      result = handle((ResponseStatusException) throwable);
    } else if (throwable instanceof ConnectTimeoutException) {
      result = handle((ConnectTimeoutException) throwable);
    } else if (throwable instanceof NotFoundException) {
      result = handle((NotFoundException) throwable);
    } else if (throwable instanceof RuntimeException) {
      result = handle((RuntimeException) throwable);
    } else if (throwable instanceof Exception) {
      result = handle((Exception) throwable);
    }
    return result;
  }
}
