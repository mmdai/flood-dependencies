package cn.flood.base.handle;

import cn.flood.base.core.exception.CoreException;
import cn.flood.base.core.exception.ErrorMessage;
import cn.flood.base.core.exception.enums.GlobalErrorCodeEnum;
import cn.flood.base.i18n.LocaleParser;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局Exception捕获RestController
 * <p>
 * Created on 2017年6月19日
 * <p>
 *
 * @author mmdai
 * @date 2017年6月19日
 */

@RestControllerAdvice
public class GlobalDefaultExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

  private static final String GLOBAL_HANDLER_TITLE = "GlobalExceptionHandler: {}";

  private static final int KILO = 1024;
  private static final String ZH_CN_1 = "zh-CN";
  private static final String ZH_CN_2 = "zh_CN";
  @Autowired
  @Qualifier("floodMessageSource")
  private MessageSource messageSource;
  @Autowired
  private LocaleParser localeParser;
  //i18默认方言
  @Value("${spring.messages.locale:zh_CN}")
  private String localeContent;

  /**
   * <p>Title: handleMaxUploadSizeExceededException</p>
   * <p>Description: file upload error 使用默认错误码S00001</p>
   * @param req
   * @param ex
   * @return
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public Object handleMaxUploadSizeExceededException(HttpServletRequest req,
      MaxUploadSizeExceededException ex) {
    logger.error(GLOBAL_HANDLER_TITLE, ex);
    String code = GlobalErrorCodeEnum.BAD_REQUEST.getCode();
    String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
    String message = "上传文件大小超出系统限制 (" + ex.getMaxUploadSize() / KILO / KILO + "MB)";
    if (!ObjectUtils.isEmpty(langContent)) {
      if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
        message =
            "upload file size exceeds system limit (" + ex.getMaxUploadSize() / KILO / KILO + "MB)";
      }
    } else {
      if (!ZH_CN_2.equals(localeContent)) {
        message =
            "upload file size exceeds system limit (" + ex.getMaxUploadSize() / KILO / KILO + "MB)";
      }
    }
    logger.info(">>>retCode:{}, retMsg:{}", code, message);
    return getErrorMessage(code, message);
  }

  /**
   * <p>Title: NoHandlerFoundException</p>
   * <p>Description: http method error 使用默认错误码404</p>
   * @param req
   * @param ex
   * @return
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  public Object handleNoHandlerFound(HttpServletRequest req,
      NoHandlerFoundException ex) {
    logger.error(GLOBAL_HANDLER_TITLE, ex);
    String code = GlobalErrorCodeEnum.NOT_FOUND.getCode();
    String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
    String message = GlobalErrorCodeEnum.NOT_FOUND.getZhName();
    if (!ObjectUtils.isEmpty(langContent)) {
      if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
        message = GlobalErrorCodeEnum.NOT_FOUND.getEnName();
      }
    } else {
      if (!ZH_CN_2.equals(localeContent)) {
        message = GlobalErrorCodeEnum.NOT_FOUND.getEnName();
      }
    }
    logger.info(">>>retCode:{}, retMsg:{}", code, message);
    return getErrorMessage(code, message);
  }

  /**
   * <p>Title: handleHttpRequestMethodNotSupportedException</p>
   * <p>Description: http method error 使用默认错误码405</p>
   * @param req
   * @param ex
   * @return
   */
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public Object handleHttpMediaTypeNotSupportedException(HttpServletRequest req,
      HttpMediaTypeNotSupportedException ex) {
    logger.error(GLOBAL_HANDLER_TITLE, ex);
    String code = GlobalErrorCodeEnum.MEDIA_TYPE_NOT_SUPPORTED.getCode();
    String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
    String message = GlobalErrorCodeEnum.MEDIA_TYPE_NOT_SUPPORTED.getZhName();
    if (!ObjectUtils.isEmpty(langContent)) {
      if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
        message = GlobalErrorCodeEnum.MEDIA_TYPE_NOT_SUPPORTED.getEnName();
      }
    } else {
      if (!ZH_CN_2.equals(localeContent)) {
        message = GlobalErrorCodeEnum.MEDIA_TYPE_NOT_SUPPORTED.getEnName();
      }
    }
    logger.info(">>>retCode:{}, retMsg:{}", code, message);
    return getErrorMessage(code, message);
  }
  /**
   * <p>Title: handleHttpRequestMethodNotSupportedException</p>
   * <p>Description: http method error 使用默认错误码406</p>
   * @param req
   * @param ex
   * @return
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public Object handleHttpRequestMethodNotSupportedException(HttpServletRequest req,
      HttpRequestMethodNotSupportedException ex) {
    logger.error(GLOBAL_HANDLER_TITLE, ex);
    String code = GlobalErrorCodeEnum.METHOD_NOT_ALLOWED.getCode();
    String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
    String message = GlobalErrorCodeEnum.METHOD_NOT_ALLOWED.getZhName();
    if (!ObjectUtils.isEmpty(langContent)) {
      if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
        message = GlobalErrorCodeEnum.METHOD_NOT_ALLOWED.getEnName();
      }
    } else {
      if (!ZH_CN_2.equals(localeContent)) {
        message = GlobalErrorCodeEnum.METHOD_NOT_ALLOWED.getEnName();
      }
    }
    logger.info(">>>retCode:{}, retMsg:{}", code, message);
    return getErrorMessage(code, message);
  }

  /**
   * <p>Title: handleMissingServletRequestParameterException</p>
   * <p>Description: http method error 使用默认错误码400</p>
   * @param req
   * @param ex
   * @return
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public Object handleMissingServletRequestParameterException(HttpServletRequest req,
      MissingServletRequestParameterException ex) {
    logger.error(GLOBAL_HANDLER_TITLE, ex);
    String code = GlobalErrorCodeEnum.BAD_REQUEST.getCode();
    String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
    String message = GlobalErrorCodeEnum.BAD_REQUEST.getZhName();
    if (!ObjectUtils.isEmpty(langContent)) {
      if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
        message = GlobalErrorCodeEnum.BAD_REQUEST.getEnName();
      }
    } else {
      if (!ZH_CN_2.equals(localeContent)) {
        message = GlobalErrorCodeEnum.BAD_REQUEST.getEnName();
      }
    }
    logger.info(">>>retCode:{}, retMsg:{}", code, message);
    return getErrorMessage(code, message);
  }

  /**
   * <p>Title: handleMissingServletRequestParameterException</p>
   * <p>Description: http method error 使用默认错误码400</p>
   * @param req
   * @param ex
   * @return
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public Object handleMethodArgumentTypeMismatchException(HttpServletRequest req,
      MethodArgumentTypeMismatchException ex) {
    logger.error(GLOBAL_HANDLER_TITLE, ex);
    String code = GlobalErrorCodeEnum.BAD_REQUEST.getCode();
    String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
    String message = GlobalErrorCodeEnum.BAD_REQUEST.getZhName();
    if (!ObjectUtils.isEmpty(langContent)) {
      if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
        message =  GlobalErrorCodeEnum.BAD_REQUEST.getEnName();
      }
    } else {
      if (!ZH_CN_2.equals(localeContent)) {
        message =  GlobalErrorCodeEnum.BAD_REQUEST.getEnName();
      }
    }
    logger.info(">>>retCode:{}, retMsg:{}", code, message);
    return getErrorMessage(code, message);
  }

  /**
   * 用于fegin抛出异常，接受异常信息
   * @param req
   * @param ex
   * @return
   */
  @ExceptionHandler(value = {ResponseStatusException.class})
  public Object handle(HttpServletRequest req, ResponseStatusException ex) {
    logger.error(GLOBAL_HANDLER_TITLE, ex);
    String code;
    String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
    String message;
    if (ex.getMessage().contains(HttpStatus.NOT_FOUND.toString())) {
      code = GlobalErrorCodeEnum.NOT_FOUND.getCode();
      message = GlobalErrorCodeEnum.NOT_FOUND.getZhName();
      if (!ObjectUtils.isEmpty(langContent)) {
        if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
          message = GlobalErrorCodeEnum.NOT_FOUND.getEnName();
        }
      } else {
        if (!ZH_CN_2.equals(localeContent)) {
          message = GlobalErrorCodeEnum.NOT_FOUND.getEnName();
        }
      }
    } else if (ex.getMessage().contains(HttpStatus.SERVICE_UNAVAILABLE.toString())) {
      code = GlobalErrorCodeEnum.SERVICE_UNAVAILABLE.getCode();
      message = GlobalErrorCodeEnum.SERVICE_UNAVAILABLE.getZhName();
      if (!ObjectUtils.isEmpty(langContent)) {
        if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
          message = GlobalErrorCodeEnum.SERVICE_UNAVAILABLE.getEnName();
        }
      } else {
        if (!ZH_CN_2.equals(localeContent)) {
          message = GlobalErrorCodeEnum.SERVICE_UNAVAILABLE.getEnName();
        }
      }
    } else if (ex.getMessage().contains(HttpStatus.GATEWAY_TIMEOUT.toString())) {
      code = GlobalErrorCodeEnum.READ_TIME_OUT.getCode();
      message = GlobalErrorCodeEnum.READ_TIME_OUT.getZhName();
      if (!ObjectUtils.isEmpty(langContent)) {
        if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
          message = GlobalErrorCodeEnum.READ_TIME_OUT.getEnName();
        }
      } else {
        if (!ZH_CN_2.equals(localeContent)) {
          message = GlobalErrorCodeEnum.READ_TIME_OUT.getEnName();
        }
      }
    } else if (ex.getMessage().contains(HttpStatus.TOO_MANY_REQUESTS.toString())) {
      code = GlobalErrorCodeEnum.TOO_MANY_REQUESTS.getCode();
      message = GlobalErrorCodeEnum.TOO_MANY_REQUESTS.getZhName();
      if (!ObjectUtils.isEmpty(langContent)) {
        if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
          message = GlobalErrorCodeEnum.TOO_MANY_REQUESTS.getEnName();
        }
      } else {
        if (!ZH_CN_2.equals(localeContent)) {
          message = GlobalErrorCodeEnum.TOO_MANY_REQUESTS.getEnName();
        }
      }
    } else {
      code = GlobalErrorCodeEnum.INTERNAL_SERVER_ERROR.getCode();
      message = GlobalErrorCodeEnum.INTERNAL_SERVER_ERROR.getZhName();
      if (!ObjectUtils.isEmpty(langContent)) {
        if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
          message = GlobalErrorCodeEnum.INTERNAL_SERVER_ERROR.getEnName();
        }
      }
    }
    logger.info(">>>retCode:{}, retMsg:{}", code, message);
    return getErrorMessage(code, message);
  }

  /**
   * <p>Title: bindErrorHandler</p>
   * <p>Description: on form submit,数据验证错误 都使用默认错误码S00001
   * 如果controller中@Valid LoginDTO loginDTO, 其中name属性定义了验证器如下
   *
   * @param ex
   * @return
   * @Pattern(regexp = "(^[\u4E00-\u9FA5]{1,6})", message="格式不正确") private String name;
   * 那么在properties中需要定义国际化翻译:beanname（bean的类名首字母小写）+属性字段 loginDTO.name=姓名格式不正确；
   * 则前端收到报文：{"_code":"A00000", "_msg":"姓名格式不正确"}
   * </p>
   */
  @ExceptionHandler(value = BindException.class)
  public ErrorMessage validErrorHandler(HttpServletRequest req, BindException ex) {
    logger.error(GLOBAL_HANDLER_TITLE, ex);
    ErrorMessage error = new ErrorMessage();
    String code = GlobalErrorCodeEnum.BAD_REQUEST.getCode();
    List<FieldError> errorList = ex.getFieldErrors();
    FieldError f_error = errorList.get(0);
    //优先获取@Valid Bean定义的message内容信息
    String message = f_error.getDefaultMessage();
    if (message == null) {
      String field_err_code = f_error.getObjectName() + "." + f_error.getField();
      message = messageSource.getMessage(field_err_code, null, getLocaleByLanguage(req));
    }
    return getErrorMessage(req, error, code, message);
  }


  /**
   * <p>Title: validJsonErrorHandler</p>
   * <p>Description: on json submit,数据验证错误 都使用默认错误码S00001
   * 如果controller中@Valid@RquestBody LoginDTO loginDTO, 其中name属性定义了验证器如下
   *
   * @param ex
   * @return
   * @Pattern(regexp = "(^[\u4E00-\u9FA5]{1,6})", message="{&NAME_ERROR}")
   * 国际化配置messages_zh_CN.properties {&NAME_ERROR}=姓名格式不正确 国际化配置messages_en_US.properties
   * {&NAME_ERROR}=name error private String name; 那么在properties中需要定义国际化翻译:beanname（bean的类名首字母小写）+属性字段
   * loginDTO.name=姓名格式不正确； 则前端收到报文：{"_code":"A00000", "_msg":"姓名格式不正确"}
   * </p>
   */
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ErrorMessage validJsonErrorHandler(HttpServletRequest req,
      MethodArgumentNotValidException ex) {
    logger.error(GLOBAL_HANDLER_TITLE, ex);
    ErrorMessage error = new ErrorMessage();
    String code = GlobalErrorCodeEnum.BAD_REQUEST.getCode();
    BindingResult bindingResult = ex.getBindingResult();
    FieldError f_error = bindingResult.getFieldError();
    //优先获取@Valid Bean定义的message内容信息
    String message = f_error.getDefaultMessage();
    if (ObjectUtils.isEmpty(message)) {
      String field_err_code = f_error.getObjectName() + "." + f_error.getField();
      message = messageSource.getMessage(field_err_code, null, getLocaleByLanguage(req));
    }
    return getErrorMessage(req, error, code, message);
  }

  /***************************sentinel 异常***************************************/
  @ExceptionHandler(value = UndeclaredThrowableException.class)
  public ErrorMessage throwableErrorHandler(HttpServletRequest req,
      UndeclaredThrowableException ex) {
    logger.error(GLOBAL_HANDLER_TITLE, ex.getUndeclaredThrowable());
    return sentinelErrorHandler(req, ex.getUndeclaredThrowable());
  }

  @ExceptionHandler(value = BlockException.class)
  public ErrorMessage blockErrorHandler(HttpServletRequest req, BlockException ex) {
    logger.error(GLOBAL_HANDLER_TITLE, ex);
    return sentinelErrorHandler(req, ex);
  }

  private ErrorMessage sentinelErrorHandler(HttpServletRequest req, Throwable ex) {
    String code = null;
    String message = null;
    if (ex instanceof FlowException) {//限流异常
      // Return 429 (Too Many Requests) by default.
      //降级异常
      code = GlobalErrorCodeEnum.TOO_MANY_REQUESTS.getCode();
      message = GlobalErrorCodeEnum.TOO_MANY_REQUESTS.getZhName();
      String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
      if (!ObjectUtils.isEmpty(langContent)) {
        if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
          message = GlobalErrorCodeEnum.TOO_MANY_REQUESTS.getEnName();
        }
      }
    } else if (ex instanceof DegradeException) {
      //降级异常
      code = GlobalErrorCodeEnum.DEGRADE_SERVER_ERROR.getCode();
      message = GlobalErrorCodeEnum.DEGRADE_SERVER_ERROR.getZhName();
      String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
      if (!ObjectUtils.isEmpty(langContent)) {
        if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
          message = GlobalErrorCodeEnum.DEGRADE_SERVER_ERROR.getEnName();
        }
      }
    } else if (ex instanceof ParamFlowException) {
      //热点参数异常
      code = GlobalErrorCodeEnum.PARAM_FLOW_SERVER_ERROR.getCode();
      message = GlobalErrorCodeEnum.PARAM_FLOW_SERVER_ERROR.getZhName();
      String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
      if (!ObjectUtils.isEmpty(langContent)) {
        if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
          message = GlobalErrorCodeEnum.PARAM_FLOW_SERVER_ERROR.getEnName();
        }
      }
    } else if (ex instanceof SystemBlockException) {
      //系统保护规则
      code = GlobalErrorCodeEnum.SYSTEM_BLOCK_SERVER_ERROR.getCode();
      message = GlobalErrorCodeEnum.SYSTEM_BLOCK_SERVER_ERROR.getZhName();
      String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
      if (!ObjectUtils.isEmpty(langContent)) {
        if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
          message = GlobalErrorCodeEnum.SYSTEM_BLOCK_SERVER_ERROR.getEnName();
        }
      }
    } else if (ex instanceof AuthorityException) {
      //授权规则
      code = GlobalErrorCodeEnum.AUTHORITY_SERVER_ERROR.getCode();
      message = GlobalErrorCodeEnum.AUTHORITY_SERVER_ERROR.getZhName();
      String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
      if (!ObjectUtils.isEmpty(langContent)) {
        if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
          message = GlobalErrorCodeEnum.AUTHORITY_SERVER_ERROR.getEnName();
        }
      }
    } else {
      //其他规则
      code = GlobalErrorCodeEnum.UNKNOWN.getCode();
      message = GlobalErrorCodeEnum.UNKNOWN.getZhName();
      String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
      if (!ObjectUtils.isEmpty(langContent)) {
        if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
          message = GlobalErrorCodeEnum.UNKNOWN.getEnName();
        }
      }
    }
    logger.info(">>>retCode:{}, retMsg:{}", code, message);
    return getErrorMessage(code, message);
  }
  /***************************sentinel 异常***************************************/
  /**
   * <p>Title: defaultErrorHandler</p>
   * <p>Description: 其他异常信息</p>
   *
   * @param req
   * @param ex
   * @return
   */
  @ExceptionHandler(value = Exception.class)
  public ErrorMessage defaultErrorHandler(HttpServletRequest req, Exception ex) {
    logger.error(GLOBAL_HANDLER_TITLE, ex);
    String code = null;
    String message = null;
    Object[] arguments = null;
    if (ex instanceof MessageSourceResolvable) {
      MessageSourceResolvable exm = (MessageSourceResolvable) ex;
      if (exm.getCodes() != null) {
        code = exm.getCodes()[0];
        message = exm.getDefaultMessage();
      }
      if (exm.getArguments() != null) {
        arguments = exm.getArguments();
      }
    } else if (ex instanceof UndeclaredThrowableException) { // on aop error
      UndeclaredThrowableException e = (UndeclaredThrowableException) ex;
      Throwable t = e.getUndeclaredThrowable();
      if (t instanceof CoreException) {
        CoreException c = (CoreException) t;
        code = c.getCode();
        message = c.getDefaultMessage();
      }
    } else {
      code = GlobalErrorCodeEnum.INTERNAL_SERVER_ERROR.getCode();
      message = GlobalErrorCodeEnum.INTERNAL_SERVER_ERROR.getZhName();
      String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
      if (!ObjectUtils.isEmpty(langContent)) {
        if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
          message = GlobalErrorCodeEnum.INTERNAL_SERVER_ERROR.getEnName();
        }
      }
    }
    if (ObjectUtils.isEmpty(message)) {
      message = messageSource.getMessage(code, arguments, getLocaleByLanguage(req));
    } else {
      String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
      if (ObjectUtils.isEmpty(langContent)) {
        langContent = localeContent;
      }
      message = localeParser.replacePlaceHolderByLocale(message, langContent);
    }
    logger.info(">>>retCode:{}, retMsg:{}", code, message);
    return getErrorMessage(code, message);
  }

  /**
   * @param req
   * @param error
   * @param code
   * @param message
   * @return
   */
  private ErrorMessage getErrorMessage(HttpServletRequest req, ErrorMessage error, String code,
      String message) {
    String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
    if (ObjectUtils.isEmpty(langContent)) {
      langContent = localeContent;
    }
    message = localeParser.replacePlaceHolderByLocale(message, langContent);
    logger.info(">>>retCode:{}, retMsg:{}", code, message);
    error.set_code(code);
    error.set_msg(message);
    return error;
  }

  /**
   * @param code
   * @param message
   * @return
   */
  private ErrorMessage getErrorMessage(String code, String message) {
    ErrorMessage error = new ErrorMessage();
    error.set_code(code);
    error.set_msg(message);
    return error;
  }

  /**
   * @param req
   * @return
   */
  private Locale getLocaleByLanguage(HttpServletRequest req) {
    Locale locale;
    String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
    if (StringUtils.isBlank(langContent)) {
      langContent = localeContent;
    }
    try {
      locale = LocaleUtils.toLocale(langContent);
    } catch (Exception e) {
      logger.warn("Failed to get locale: {}, set according to actual parameters", langContent);
      if (ZH_CN_1.equals(langContent) || ZH_CN_2.equals(langContent)) {
        locale = Locale.CHINA;
      } else {
        locale = Locale.US;
      }
    }
    return locale;
  }
}
