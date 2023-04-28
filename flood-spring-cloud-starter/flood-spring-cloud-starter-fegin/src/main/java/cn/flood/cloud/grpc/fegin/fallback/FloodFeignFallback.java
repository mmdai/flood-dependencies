package cn.flood.cloud.grpc.fegin.fallback;

import cn.flood.base.core.exception.enums.GlobalErrorCodeEnum;
import cn.flood.base.core.json.JsonUtils;
import cn.flood.base.core.rpc.response.Result;
import cn.flood.base.core.rpc.response.ResultWapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.JsonNode;
import feign.FeignException;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/**
 * fallBack 代理处理
 *
 * @author mmdai
 */
@AllArgsConstructor
public class FloodFeignFallback<T> implements MethodInterceptor {

  private final static String CODE = "_code";
  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private final Class<T> targetType;
  private final String targetName;
  private final Throwable cause;

  @Nullable
  @Override
  public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy)
      throws Throwable {
    String errorMessage = cause.getMessage();
    log.error("FloodFeignFallback:[{}.{}] serviceId:[{}] message:[{}]", targetType.getName(),
        method.getName(), targetName, errorMessage);
    Class<?> returnType = method.getReturnType();
    // 暂时不支持 flux，rx，异步等，返回值不是 Result，直接返回 null。
    if (Result.class != returnType) {
      return null;
    }
    /***************************sentinel 异常***************************************/
    if (cause instanceof BlockException) {
      String code = null;
      String message = null;
      if (cause instanceof FlowException) {//限流异常
        // Return 429 (Too Many Requests) by default.
        //降级异常
        code = GlobalErrorCodeEnum.TOO_MANY_REQUESTS.getCode();
        message = GlobalErrorCodeEnum.TOO_MANY_REQUESTS.getZhName();
      } else if (cause instanceof DegradeException) {
        //降级异常
        code = GlobalErrorCodeEnum.DEGRADE_SERVER_ERROR.getCode();
        message = GlobalErrorCodeEnum.DEGRADE_SERVER_ERROR.getZhName();
      } else if (cause instanceof ParamFlowException) {
        //热点参数异常
        code = GlobalErrorCodeEnum.PARAM_FLOW_SERVER_ERROR.getCode();
        message = GlobalErrorCodeEnum.PARAM_FLOW_SERVER_ERROR.getZhName();

      } else if (cause instanceof SystemBlockException) {
        //系统保护规则
        code = GlobalErrorCodeEnum.SYSTEM_BLOCK_SERVER_ERROR.getCode();
        message = GlobalErrorCodeEnum.SYSTEM_BLOCK_SERVER_ERROR.getZhName();

      } else if (cause instanceof AuthorityException) {
        //授权规则
        code = GlobalErrorCodeEnum.AUTHORITY_SERVER_ERROR.getCode();
        message = GlobalErrorCodeEnum.AUTHORITY_SERVER_ERROR.getZhName();

      } else {
        //其他规则
        code = GlobalErrorCodeEnum.UNKNOWN.getCode();
        message = GlobalErrorCodeEnum.UNKNOWN.getZhName();
      }
      log.error(">>>retCode:{}, retMsg:{}", code, message);
      return ResultWapper.wrap(code, message);
    }
    /***************************sentinel 异常***************************************/
    // 非 FeignException
    if (!(cause instanceof FeignException)) {
      return ResultWapper.error(errorMessage);
    }
    FeignException exception = (FeignException) cause;
    //503 找不到可用服务
    if (exception.status() == HttpStatus.SERVICE_UNAVAILABLE.value()) {
      return ResultWapper.wrap(GlobalErrorCodeEnum.SERVICE_UNAVAILABLE.getCode(),
          GlobalErrorCodeEnum.SERVICE_UNAVAILABLE.getEnName());
    }
    //404 找不到接口
    if (exception.status() == HttpStatus.NOT_FOUND.value()) {
      return ResultWapper.wrap(GlobalErrorCodeEnum.NOT_FOUND.getCode(),
          GlobalErrorCodeEnum.NOT_FOUND.getEnName());
    }
    //超时
    if (exception.getCause() instanceof SocketTimeoutException || exception
        .getCause() instanceof ConnectTimeoutException) {
      if (exception.getCause().getMessage().indexOf("connect timed out") > -1) {
        return ResultWapper.wrap(GlobalErrorCodeEnum.CONNECTION_TIME_OUT.getCode(),
            GlobalErrorCodeEnum.CONNECTION_TIME_OUT.getEnName());
      } else if (exception.getCause().getMessage().indexOf("Read timed out") > -1 ||
          exception.getCause().getMessage().indexOf("timeout") > -1) {
        return ResultWapper.wrap(GlobalErrorCodeEnum.READ_TIME_OUT.getCode(),
            GlobalErrorCodeEnum.READ_TIME_OUT.getEnName());
      }
      return ResultWapper.wrap(GlobalErrorCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
          "ERROR【" + exception.getCause().getMessage() + "】");
    }
    if (exception.status() == -1) {
      return ResultWapper.wrap(GlobalErrorCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
          "ERROR【" + exception.getCause().getMessage() + "】");
    }
    Optional<ByteBuffer> contentOptional = exception.responseBody();
    // 如果返回的数据为空
    if (contentOptional != null) {
      return ResultWapper.error(errorMessage);
    }
    byte[] content = contentOptional.get().array();
    // 转换成 jsonNode 读取，因为直接转换，可能 对方放回的并 不是 R 的格式。
    JsonNode resultNode = JsonUtils.readTree(content);
    // 判断是否 R 格式 返回体
    if (resultNode.has(CODE)) {
      return JsonUtils.toJavaObject(content, Result.class);
    }
    return ResultWapper.error(resultNode.toString());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FloodFeignFallback<?> that = (FloodFeignFallback<?>) o;
    return targetType.equals(that.targetType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(targetType);
  }

}
