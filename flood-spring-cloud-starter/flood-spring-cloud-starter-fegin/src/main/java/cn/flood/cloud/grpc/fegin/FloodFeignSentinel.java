package cn.flood.cloud.grpc.fegin;

import cn.flood.base.core.Func;
import cn.flood.cloud.grpc.fegin.fallback.FloodFallbackFactory;
import cn.flood.cloud.grpc.sentinel.FloodSentinelInvocationHandler;
import cn.flood.base.core.constants.HeaderConstant;
import cn.flood.base.core.trace.MDCTraceUtils;
import com.alibaba.cloud.sentinel.feign.SentinelContractHolder;
import feign.*;
import feign.form.spring.SpringFormEncoder;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * feign集成sentinel自动配置
 * 重写 {@link com.alibaba.cloud.sentinel.feign.SentinelFeign} 适配最新API
 *EnumConfigurationHelper
 */
@SuppressWarnings("unchecked")
public class FloodFeignSentinel {

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder extends Feign.Builder implements ApplicationContextAware {
		private Contract contract = new Contract.Default();
		private ApplicationContext applicationContext;
		private FeignContext feignContext;
		private SpringFormEncoder encoder = new SpringFormEncoder();

		@Override
		public Feign.Builder invocationHandlerFactory(
			InvocationHandlerFactory invocationHandlerFactory) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Builder contract(Contract contract) {
			this.contract = contract;
			return this;
		}

		@Override
		public Feign build() {
			super.invocationHandlerFactory(new InvocationHandlerFactory() {
				@SneakyThrows
				@Override
				public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
					// 注解取值以避免循环依赖的问题，循环依赖ERROR[ Is there an unresolvable circular reference?]
					FeignClient feignClient = AnnotationUtils.findAnnotation(target.type(), FeignClient.class);
					Class fallback = feignClient.fallback();
					Class fallbackFactory = feignClient.fallbackFactory();
					String contextId = feignClient.contextId();

					if (!StringUtils.hasText(contextId)) {
						contextId = feignClient.name();
					}

					Object fallbackInstance;
					FallbackFactory fallbackFactoryInstance;
					// 判断fallback类型
					if (void.class != fallback) {
						fallbackInstance = getFromContext(contextId, "fallback", fallback, target.type());
						return new FloodSentinelInvocationHandler(target, dispatch, new FallbackFactory.Default(fallbackInstance));
					}
					if (void.class != fallbackFactory) {
						fallbackFactoryInstance = (FallbackFactory) getFromContext(contextId, "fallbackFactory", fallbackFactory, FallbackFactory.class);
						return new FloodSentinelInvocationHandler(target, dispatch, fallbackFactoryInstance);
					}
					// 默认fallbackFactory
					FloodFallbackFactory floodFallbackFactory = new FloodFallbackFactory(target);
					return new FloodSentinelInvocationHandler(target, dispatch, floodFallbackFactory);
				}

				private Object getFromContext(String name, String type, Class fallbackType, Class targetType) {
					Object fallbackInstance = feignContext.getInstance(name, fallbackType);
					if (fallbackInstance == null) {
						throw new IllegalStateException(
							String.format("No %s instance of type %s found for feign client %s",
								type, fallbackType, name)
						);
					}

					if (!targetType.isAssignableFrom(fallbackType)) {
						throw new IllegalStateException(
							String.format("Incompatible %s instance. Fallback/fallbackFactory of type %s is not assignable to %s for feign client %s",
								type, fallbackType, targetType, name)
						);
					}
					return fallbackInstance;
				}
			});
			super.contract(new SentinelContractHolder(contract));
			super.requestInterceptor(new RequestInterceptor() {
				@Override
				public void apply(RequestTemplate requestTemplate) {
					ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
							.getRequestAttributes();
					if (attributes != null) {
						HttpServletRequest request = attributes.getRequest();
						String contentLanguage = request.getHeader(HttpHeaders.CONTENT_LANGUAGE);
						if (Func.isNotEmpty(contentLanguage)) {
							requestTemplate.header(HttpHeaders.CONTENT_LANGUAGE, contentLanguage);
						}
						String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
						if (Func.isNotEmpty(authorization)) {
							requestTemplate.header(HttpHeaders.AUTHORIZATION, authorization);
						}
						requestTemplate.header(HttpHeaders.COOKIE, request.getHeader(HttpHeaders.COOKIE));
						//传递日志traceId
						String traceId = request.getHeader(MDCTraceUtils.TRACE_ID_HEADER);
						if (Func.isEmpty(traceId)) {
							traceId = MDCTraceUtils.getTraceId();
						}
						requestTemplate.header(MDCTraceUtils.TRACE_ID_HEADER, traceId);
						//传递版本号
						String version =  request.getHeader(HeaderConstant.HEADER_VERSION);
						if (Func.isNotEmpty(version) && !requestTemplate.headers().containsKey(HeaderConstant.HEADER_VERSION)) {
							requestTemplate.header(HeaderConstant.HEADER_VERSION, version);
						}
						//传递租户ID
						requestTemplate.header(HeaderConstant.TENANT_ID, Func.toStr(request.getHeader(HeaderConstant.TENANT_ID), HeaderConstant.DEFAULT_TENANT_ID));

//						if(requestTemplate.headers().get(HttpHeaders.CONTENT_TYPE).contains(HttpMediaType.APPLICATION_JSON_VALUE)){
//							// 通过template获取到请求体（已经被转成json）
//							String jsonBody = new String(requestTemplate.body());
//							// 构造通用的请求体
//							Map<String, Object> baseReq = Func.parse(jsonBody, Map.class);
//							requestTemplate.header(HttpHeaders.CONTENT_TYPE, HttpMediaType.APPLICATION_PROTO_VALUE);
//							// 通过encoder的encode方法，将我们的数据 改成表单数据，并替换掉原来的template中的body
//							encoder.encode(baseReq, Encoder.MAP_STRING_WILDCARD, requestTemplate);
//						}

					}
				}
			});
			return super.build();
		}

		@Override
		public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			this.applicationContext = applicationContext;
			feignContext = this.applicationContext.getBean(FeignContext.class);
		}
	}

}
