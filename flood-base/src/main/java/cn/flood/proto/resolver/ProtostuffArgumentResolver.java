package cn.flood.proto.resolver;

import cn.flood.proto.ProtostuffRequest;
import cn.flood.proto.ProtostuffUtils;
import cn.flood.proto.RequestWrapper;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("unchecked")
public class ProtostuffArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 用于判定是否需要处理该参数分解，返回true为需要，并会去调用下面的方法resolveArgument。
     *
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ProtostuffRequest.class);
    }

    /**
     * 真正用于处理参数分解的方法，返回的Object就是controller方法上的形参对象。
     *
     * @param parameter
     * @param mavContainer
     * @param webRequest
     * @param binderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String reqBody = new RequestWrapper(request).getBodyString(request);
        Class clazz = parameter.getParameterType();// 获得参数类型
        return ProtostuffUtils.deserialize(reqBody.getBytes(), clazz);
    }

}
