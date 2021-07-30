package cn.flood.cloud.gateway.handler;

import cn.flood.cloud.gateway.result.Result;
import cn.flood.cloud.gateway.result.ResultCode;
import cn.flood.cloud.gateway.result.ResultWapper;
import io.netty.channel.ConnectTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;


/**
 * 异常处理通知
 *
 * @author mmdai
 */
@Slf4j
@Component
public class ExceptionHandlerAdvice {
	@ExceptionHandler(value = {ResponseStatusException.class})
	public Result<?> handle(ResponseStatusException ex) {
		log.error("response status exception:{}", ex.getMessage());
		if (ex.getMessage().contains(HttpStatus.NOT_FOUND.toString()) ) {
			return ResultWapper.wrap(ResultCode.NOT_FOUND.getCode(), ResultCode.NOT_FOUND.getMsg());
		} else if(ex.getMessage().contains(HttpStatus.SERVICE_UNAVAILABLE.toString())) {
			return ResultWapper.wrap(ResultCode.SERVICE_UNAVAILABLE.getCode(),
					ResultCode.SERVICE_UNAVAILABLE.getMsg());
		}else if(ex.getMessage().contains(HttpStatus.GATEWAY_TIMEOUT.toString())){
				return ResultWapper.wrap(ResultCode.GATEWAY_TIMEOUT.getCode(),
						ResultCode.GATEWAY_TIMEOUT.getMsg());
		} else {
			return ResultWapper.wrap(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMsg());
		}
	}

	@ExceptionHandler(value = {ConnectTimeoutException.class})
	public Result<?> handle(ConnectTimeoutException ex) {
		log.error("connect timeout exception:{}", ex.getMessage());
		return ResultWapper.wrap(ResultCode.TIME_ERROR.getCode(), ResultCode.TIME_ERROR.getMsg());
	}

	@ExceptionHandler(value = {NotFoundException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Result<?> handle(NotFoundException ex) {
		log.error("not found exception:{}", ex.getMessage());
		return ResultWapper.wrap(ResultCode.NOT_FOUND.getCode(), ResultCode.NOT_FOUND.getMsg());
	}

	@ExceptionHandler(value = {RuntimeException.class})
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Result<?> handle(RuntimeException ex) {
		log.error("runtime exception:{}", ex.getMessage());
		return ResultWapper.error(ex.getMessage());
	}

	@ExceptionHandler(value = {Exception.class})
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Result<?> handle(Exception ex) {
		log.error("exception:{}", ex.getMessage());
		return ResultWapper.error();
	}

	@ExceptionHandler(value = {Throwable.class})
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
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