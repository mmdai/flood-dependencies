package cn.flood.cloud.fegin.retryer;

import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Date;

/**
 * retryer 重试错误码
 * @author mmdai
 * @version 1.0
 * @date 2022/5/26 9:41
 */
@Slf4j
public class FloodErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {
        if (response.status() == HttpStatus.BAD_REQUEST.value()) {
            log.error("请求服务400参数错误,返回:{}", response.body());
        }

        if (response.status() ==  HttpStatus.CONFLICT.value()) {
            log.error("请求服务409异常,返回:{}", response.body());
        }

        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            log.error("请求服务404异常,返回:{}", response.body());
        }
        if (response.status() == HttpStatus.SERVICE_UNAVAILABLE.value()) {
            log.error("请求服务503异常,返回:{}", response.body());
            throw new RetryableException(
                    response.status(),
                    HttpStatus.SERVICE_UNAVAILABLE.name(),
                    response.request().httpMethod(),
                    null,
                    response.request());
        }
        // 其他异常交给Default去解码处理
        // 这里使用单例即可，Default不用每次都去new
        return defaultErrorDecoder.decode(s, response);
    }
}
