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
package cn.flood.base.mvc.version;

import cn.flood.base.mvc.annotation.ApiVersion;
import cn.flood.base.mvc.annotation.UrlVersion;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * url版本号处理 和 header 版本处理
 *
 * <p>
 *     url: /v1/user/{id}
 *     header: Accept application/vnd.flood.VERSION+json
 * </p>
 *
 * 注意：c 代表客户端版本
 *
 * @author mmdai
 */
public class FloodRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

	@Nullable
	@Override
	protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
		RequestMappingInfo mappinginfo = super.getMappingForMethod(method, handlerType);
		if (mappinginfo != null) {
			RequestMappingInfo apiVersionMappingInfo = getApiVersionMappingInfo(method, handlerType);
			return apiVersionMappingInfo == null ? mappinginfo : apiVersionMappingInfo.combine(mappinginfo);
		}
		return null;
	}

	/**
	 * 自定义类型注解匹配，即Controller接口类匹配
	 * @param handlerType
	 * @return
	 */
	@Override
	protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
		// Headers version 版本信息
		ApiVersion apiVersion = AnnotatedElementUtils.findMergedAnnotation(handlerType, ApiVersion.class);
		return createCondition(apiVersion);
	}

	/**
	 * 自定义方法注解匹配，即具体方法级别的注解匹配
	 * @param method
	 * @return
	 */
	@Override
	protected RequestCondition<ApiVersionCondition>getCustomMethodCondition(Method method) {
		// Headers version 版本信息
		ApiVersion apiVersion = AnnotatedElementUtils.findMergedAnnotation(method, ApiVersion.class);
		return createCondition(apiVersion);
	}

	@Nullable
	private RequestMappingInfo getApiVersionMappingInfo(Method method, Class<?> handlerType) {
		// url 上的版本，优先获取方法上的版本
		UrlVersion urlVersion = AnnotatedElementUtils.findMergedAnnotation(method, UrlVersion.class);
		// 再次尝试类上的版本
		if (urlVersion == null || StringUtils.isBlank(urlVersion.value())) {
			urlVersion = AnnotatedElementUtils.findMergedAnnotation(handlerType, UrlVersion.class);
		}
		boolean nonUrlVersion = urlVersion == null || StringUtils.isBlank(urlVersion.value());
		// 先判断
		if (nonUrlVersion) {
			return null;
		}
		return RequestMappingInfo.paths(urlVersion.value()).build();
	}


	private RequestCondition<ApiVersionCondition>createCondition(ApiVersion apiVersion) {
		return apiVersion == null ? null : new ApiVersionCondition(apiVersion.value(), apiVersion.op());
	}
	@Override
	protected void handlerMethodsInitialized(Map<RequestMappingInfo, HandlerMethod> handlerMethods) {
		// 打印路由信息 spring boot 2.1 去掉了这个 日志的打印
		if (logger.isInfoEnabled()) {
			for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
				RequestMappingInfo mapping = entry.getKey();
				HandlerMethod handlerMethod = entry.getValue();
				logger.info("Mapped \"" + mapping + "\" onto " + handlerMethod);
			}
		}
		super.handlerMethodsInitialized(handlerMethods);
	}
}
