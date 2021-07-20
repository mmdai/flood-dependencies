/**
 * Copyright (c) 2018-2028,
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.flood.cloud.fegin;

import cn.flood.Func;
import cn.flood.json.JsonUtils;
import cn.flood.lang.StringUtils;
import cn.flood.rpc.response.Result;
import cn.flood.rpc.response.ResultWapper;
import com.fasterxml.jackson.databind.JsonNode;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * blade fallBack 代理处理
 *
 * @author mmdai
 */
@Slf4j
@AllArgsConstructor
public class FloodFeignFallback<T> implements MethodInterceptor {
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
		byte[] content = exception.content();
		// 如果返回的数据为空
		if (Func.isEmpty(content)) {
			return ResultWapper.error(errorMessage);
		}
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
