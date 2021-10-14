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
package cn.flood.cloud.version;

import cn.flood.Func;
import cn.flood.http.HttpMediaType;
import cn.flood.mvc.annotation.ApiVersion;
import cn.flood.mvc.annotation.UrlVersion;
import cn.flood.mvc.version.ApiVersionCondition;
import feign.MethodMetadata;
import feign.Util;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.annotation.*;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 支持 flood-boot 的 版本 处理
 *
 * @see cn.flood.mvc.annotation.UrlVersion
 * @see cn.flood.mvc.annotation.ApiVersion
 * @author mmdai
 */
public class FloodSpringMvcContract extends SpringMvcContract {


	public FloodSpringMvcContract(){
		this(getDefaultAnnotatedArgumentsProcessors());
	}

	public FloodSpringMvcContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors) {
		this(annotatedParameterProcessors, new DefaultConversionService());
	}

	public FloodSpringMvcContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors, ConversionService conversionService) {
		super(annotatedParameterProcessors, conversionService);
	}

	@Override
	protected void processAnnotationOnMethod(MethodMetadata data, Annotation methodAnnotation, Method method) {
		if (RequestMapping.class.isInstance(methodAnnotation) || methodAnnotation.annotationType().isAnnotationPresent(RequestMapping.class)) {
			Class<?> targetType = method.getDeclaringClass();
			// url 上的版本，优先获取方法上的版本
			UrlVersion urlVersion = AnnotatedElementUtils.findMergedAnnotation(method, UrlVersion.class);
			// 再次尝试类上的版本
			if (urlVersion == null || Func.isEmpty(urlVersion.value())) {
				urlVersion = AnnotatedElementUtils.findMergedAnnotation(targetType, UrlVersion.class);
			}
			if (urlVersion != null && Func.isNotBlank(urlVersion.value())) {
				String versionUrl = "/" + urlVersion.value();
				data.template().uri(versionUrl);
			}
			// 添加默认Produces、Consumes为application/x-protobuf
			RequestMapping methodMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
			this.parseProduces(data, methodMapping);
			this.parseConsumes(data, methodMapping);
			// 注意：在父类之前 添加 url版本，在父类之后，(如果自定义Produces、Consumes覆盖默认)处理 Media Types 版本
			super.processAnnotationOnMethod(data, methodAnnotation, method);

			// 处理 Headers 版本信息
			ApiVersion apiVersion = AnnotatedElementUtils.findMergedAnnotation(method, ApiVersion.class);
			// 再次尝试类上的版本
			if (apiVersion == null || Func.isEmpty(apiVersion.value())) {
				apiVersion = AnnotatedElementUtils.findMergedAnnotation(targetType, ApiVersion.class);
			}
			if (apiVersion != null && Func.isNotBlank(apiVersion.value())) {
				data.template().header(ApiVersionCondition.HEADER_VERSION, apiVersion.value());
			}
		}
	}

	private void parseProduces(MethodMetadata md, RequestMapping annotation) {
		String[] serverProduces = annotation.produces();
		String clientAccepts = serverProduces.length == 0 ? null : Util.emptyToNull(serverProduces[0]);
		if (clientAccepts == null) {
			md.template().header(HttpHeaders.ACCEPT, new String[]{HttpMediaType.APPLICATION_PROTO_VALUE});
		}

	}

	private void parseConsumes(MethodMetadata md, RequestMapping annotation) {
		String[] serverConsumes = annotation.consumes();
		String clientProduces = serverConsumes.length == 0 ? null : Util.emptyToNull(serverConsumes[0]);
		if (clientProduces == null) {
			md.template().header(HttpHeaders.CONTENT_TYPE, new String[]{HttpMediaType.APPLICATION_PROTO_VALUE});
		}

	}

	/**
	 * 参考：https://gist.github.com/rmfish/0ed59a9af6c05157be2a60c9acea2a10
	 * @param annotations 注解
	 * @param paramIndex 参数索引
	 * @return 是否 http 注解
	 */
	@Override
	protected boolean processAnnotationsOnParameter(MethodMetadata data, Annotation[] annotations, int paramIndex) {
		boolean httpAnnotation = super.processAnnotationsOnParameter(data, annotations, paramIndex);
		// 在 springMvc 中如果是 Get 请求且参数中是对象 没有声明为@RequestBody 则默认为 Param
		if (!httpAnnotation) {
			String methodType = data.template().method().toUpperCase();
			if ("GET".equals(methodType)) {
				for (Annotation parameterAnnotation : annotations) {
					if (!(parameterAnnotation instanceof RequestBody)) {
						return false;
					}
				}
				data.queryMapIndex(paramIndex);
				return true;
			}
			// 在 springMvc 中如果不是 Get并且是protbuf是x-protobuf的是对象 则默认为 Param
			if (!"GET".equals(methodType) && isProtobufData(data)) {
				data.queryMapIndex(paramIndex);
				return true;
			}
		}
		return httpAnnotation;
	}

	private boolean isProtobufData(MethodMetadata data) {
		Collection<String> contentTypes = (Collection)data.template().headers().get("Content-Type");
		if (contentTypes != null && !contentTypes.isEmpty()) {
			String type = (String)contentTypes.iterator().next();
			try {
				return HttpMediaType.APPLICATION_PROTO_VALUE.equals(type);
			} catch (InvalidMediaTypeException var5) {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 新增的处理复杂对象类型查询参数
	 * @return
	 */
	private static List<AnnotatedParameterProcessor> getDefaultAnnotatedArgumentsProcessors() {
		List<AnnotatedParameterProcessor> annotatedArgumentResolvers = new ArrayList();
		annotatedArgumentResolvers.add(new MatrixVariableParameterProcessor());
		annotatedArgumentResolvers.add(new PathVariableParameterProcessor());
		annotatedArgumentResolvers.add(new RequestParamParameterProcessor());
		annotatedArgumentResolvers.add(new RequestHeaderParameterProcessor());
		annotatedArgumentResolvers.add(new QueryMapParameterProcessor());
		annotatedArgumentResolvers.add(new RequestPartParameterProcessor());
		return annotatedArgumentResolvers;
	}


}
