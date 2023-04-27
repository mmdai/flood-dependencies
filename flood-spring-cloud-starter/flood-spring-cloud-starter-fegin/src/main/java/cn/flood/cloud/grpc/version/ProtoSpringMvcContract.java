package cn.flood.cloud.grpc.version;

import cn.flood.base.core.Func;
import cn.flood.base.core.constants.HeaderConstant;
import cn.flood.base.core.http.HttpMediaType;
import cn.flood.base.mvc.annotation.ApiVersion;
import cn.flood.base.mvc.annotation.UrlVersion;
import feign.MethodMetadata;
import feign.Util;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.annotation.MatrixVariableParameterProcessor;
import org.springframework.cloud.openfeign.annotation.PathVariableParameterProcessor;
import org.springframework.cloud.openfeign.annotation.QueryMapParameterProcessor;
import org.springframework.cloud.openfeign.annotation.RequestHeaderParameterProcessor;
import org.springframework.cloud.openfeign.annotation.RequestParamParameterProcessor;
import org.springframework.cloud.openfeign.annotation.RequestPartParameterProcessor;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 支持 flood-boot 的 版本 处理
 *
 * @author mmdai
 * @see cn.flood.base.mvc.annotation.UrlVersion
 * @see cn.flood.base.mvc.annotation.ApiVersion
 */
@SuppressWarnings("unchecked")
public class ProtoSpringMvcContract extends SpringMvcContract {


  public ProtoSpringMvcContract() {
    this(getDefaultAnnotatedArgumentsProcessors());
  }

  public ProtoSpringMvcContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors) {
    this(annotatedParameterProcessors, new DefaultConversionService());
  }

  public ProtoSpringMvcContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors,
      ConversionService conversionService) {
    super(annotatedParameterProcessors, conversionService);
  }

  /**
   * 获取该对象的类,类的所有实现接口,类的父类的名称
   *
   * @param clazz
   * @return
   */
  private static Set<String> getAllInterfaces(Class<?> clazz) {
    Set<String> types = new HashSet<>();
    Stack<Class<?>> stack = new Stack<>();
    stack.push(clazz);
    while (!stack.empty()) {
      Class<?> c = stack.pop();
      types.add(c.getName());
      Class<?> superClass = c.getSuperclass();
      if (superClass != null) {
        stack.push(superClass);
      }
      Class<?>[] cs = c.getInterfaces();
      for (Class<?> superClazzs : cs) {
        stack.push(superClazzs);
      }
    }
    return types;
  }

  /**
   * 新增的处理复杂对象类型查询参数
   *
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

  @Override
  protected void processAnnotationOnMethod(MethodMetadata data, Annotation methodAnnotation,
      Method method) {
    if (RequestMapping.class.isInstance(methodAnnotation) || methodAnnotation.annotationType()
        .isAnnotationPresent(RequestMapping.class)) {
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
      RequestMapping methodMapping = AnnotatedElementUtils
          .findMergedAnnotation(method, RequestMapping.class);
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
      if (apiVersion != null && Func.isNotEmpty(apiVersion.value())) {
        data.template().header(HeaderConstant.HEADER_VERSION, apiVersion.value());
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
      md.template()
          .header(HttpHeaders.CONTENT_TYPE, new String[]{HttpMediaType.APPLICATION_PROTO_VALUE});
    }

  }

  /**
   * 参考：https://gist.github.com/rmfish/0ed59a9af6c05157be2a60c9acea2a10
   *
   * @param annotations 注解
   * @param paramIndex  参数索引
   * @return 是否 http 注解
   */
  @Override
  protected boolean processAnnotationsOnParameter(MethodMetadata data, Annotation[] annotations,
      int paramIndex) {
    boolean httpAnnotation = super.processAnnotationsOnParameter(data, annotations, paramIndex);
    // 在 springMvc 中如果是 Get 请求且参数中是对象 没有声明为@RequestBody 则默认为 Param
    if (!httpAnnotation) {
//			String methodType = data.template().method().toUpperCase();
      //如果是请求RequestBody 则false
      for (Annotation parameterAnnotation : annotations) {
        if (parameterAnnotation instanceof RequestBody) {
          return false;
        }
      }
      // 在 springMvc 中如果 RequestBody是protbuf是x-protobuf的是对象 则默认为 Param
      if (isProtobufData(data)) {
        data.queryMapIndex(paramIndex);
//				Class<?>[] classes = data.method().getParameterTypes();
//				for(int i=0; i<=classes.length-1; i++){
//					Field[] fields = classes[i].getDeclaredFields();
//					for(Field field : fields){
//						String name = field.getName();
//						data.formParams().add(name);
//					}
//					fields = classes[i].getSuperclass().getDeclaredFields();
//					for(Field field : fields){
//						String name = field.getName();
//						data.formParams().add(name);
//					}
//				}
//				List<String> formParams = data.formParams();
//				StringBuffer stringBuffer = new StringBuffer("{");
//				for (int i = 0; i < formParams.size(); i++) {
//					boolean isList = false;
//
//					Set<String> types = getAllInterfaces(data.method().getParameterTypes()[i]);
//					if (types.contains("java.util.Collection")) {
//						isList = true;
//					}
//
//					String param = formParams.get(i);
//					stringBuffer.append("\"" + param + "\"").append(":");
//					if (isList) {
//						stringBuffer.append("[");
//					}
//					stringBuffer.append("{").append(param).append("}");
//					if (isList) {
//						stringBuffer.append("]");
//					}
//					if (i < formParams.size() - 1) {
//						stringBuffer.append(",");
//					}
//				}
//				stringBuffer.append("}");
//				data.template().bodyTemplate(stringBuffer.toString());
        return true;
      }
    }
    return httpAnnotation;
  }

  private boolean isProtobufData(MethodMetadata data) {
    Collection<String> contentTypes = (Collection) data.template().headers().get("Content-Type");
    if (contentTypes != null && !contentTypes.isEmpty()) {
      String type = (String) contentTypes.iterator().next();
      try {
        return HttpMediaType.APPLICATION_PROTO_VALUE.equals(type);
      } catch (InvalidMediaTypeException var5) {
        return false;
      }
    } else {
      return false;
    }
  }

}
