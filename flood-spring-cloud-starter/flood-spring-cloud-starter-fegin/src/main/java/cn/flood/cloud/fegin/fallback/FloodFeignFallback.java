package cn.flood.cloud.fegin.fallback;

import cn.flood.constants.CoreConstant;
import cn.flood.json.JsonUtils;
import cn.flood.rpc.response.Result;
import cn.flood.rpc.response.ResultWapper;
import com.fasterxml.jackson.databind.JsonNode;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;

/**
 * fallBack 代理处理
 *
 * @author mmdai
 */
@AllArgsConstructor
public class FloodFeignFallback<T> implements MethodInterceptor {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Class<T> targetType;
	private final String targetName;
	private final Throwable cause;
	private final static String CODE = "_code";

	@Nullable
	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		String errorMessage = cause.getMessage();
		log.error("FloodFeignFallback:[{}.{}] serviceId:[{}] message:[{}]", targetType.getName(), method.getName(), targetName, errorMessage);
		Class<?> returnType = method.getReturnType();
		// 暂时不支持 flux，rx，异步等，返回值不是 Result，直接返回 null。
		if (Result.class != returnType) {
			return null;
		}
		// 非 FeignException
		if (!(cause instanceof FeignException)) {
			return ResultWapper.error(errorMessage);
		}
		FeignException exception = (FeignException) cause;
		//503 找不到可用服务
		if(exception.status() == HttpStatus.SERVICE_UNAVAILABLE.value()){
			return ResultWapper.wrap(CoreConstant.HTTP_ERROR_CODE.A00503, "ERROR【Service Unavailable】");
		}
		//超时
		if(exception.getCause() instanceof SocketTimeoutException || exception.getCause() instanceof ConnectTimeoutException) {
			if (exception.getCause().getMessage().indexOf("connect timed out") > -1) {
				return ResultWapper.wrap(CoreConstant.HTTP_ERROR_CODE.A00501, "ERROR【Connection timed out】");
			} else if (exception.getCause().getMessage().indexOf("Read timed out") > -1 ||
					exception.getCause().getMessage().indexOf("timeout") > -1) {
				return ResultWapper.wrap(CoreConstant.HTTP_ERROR_CODE.A00502, "ERROR【Read timed out】");
			}
			return ResultWapper.wrap(CoreConstant.HTTP_ERROR_CODE.A00500, "ERROR【" + exception.getCause().getMessage() + "】");
		}
		if(exception.status() == -1){
			return ResultWapper.wrap(CoreConstant.HTTP_ERROR_CODE.A00500, "ERROR【" + exception.getCause().getMessage() + "】");
		}
		Optional<ByteBuffer> contentOptional = exception.responseBody();
		// 如果返回的数据为空
		if (ObjectUtils.isEmpty(contentOptional.empty())) {
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