package cn.flood.base.aop.response;

import cn.flood.base.core.exception.ErrorMessage;
import cn.flood.base.core.rpc.response.Result;
import cn.flood.base.core.rpc.response.ResultWapper;
import cn.flood.base.i18n.LocaleParser;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * <p>
 * Created on 2017年6月19日
 * <p>
 *
 * @author mmdai
 * @date 2017年6月19日
 */
@SuppressWarnings("rawtypes")
@RestControllerAdvice
public class ResponseBodyAdviceConfig implements ResponseBodyAdvice<Object> {

  @Autowired
  private LocaleParser localeParser;

  //i18默认方言
  @Value("${spring.messages.locale:zh_CN}")
  private String localeContent;

  /* (non-Javadoc)
   * <p>Title: supports</p>
   * <p>Description: </p>
   * @param methodParameter
   * @param aClass
   * @return
   * @see org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice#supports(org.springframework.core.MethodParameter, java.lang.Class)
   */
  @Override
  public boolean supports(MethodParameter methodParameter,
      Class<? extends HttpMessageConverter<?>> aClass) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType,
      MediaType selectedContentType,
      Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
    if (body != null) {
      if (body instanceof ErrorMessage) {
        //GlobalDefaultExceptionHandler全局异常捕获  返回 ErrorMessage对象
        ErrorMessage message = (ErrorMessage) body;
        return ResultWapper.wrap(message.get_code(), message.get_msg());
      } else if (body instanceof Result) {
        Result ret = (Result) body;
        if (ret.is_succeed() || !ret.get_msg().contains("{&")) {
          return ret;
        }
        String langContent = localeContent;
        List<String> langContentList = request.getHeaders().get(HttpHeaders.CONTENT_LANGUAGE);
        if (!CollectionUtils.isEmpty(langContentList)) {
          langContent = langContentList.get(0);
        }
        String msg = localeParser.replacePlaceHolderByLocale(ret.get_msg(), langContent);
        ret.set_msg(msg);
        return ret;
      }
    }
    return body;
  }


}
