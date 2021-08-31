package cn.flood.handle;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import cn.flood.exception.CoreException;
import cn.flood.exception.enums.GlobalErrorCodeEnum;
import cn.flood.i18n.LocaleParser;
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
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import cn.flood.exception.ErrorMessage;

/**
 * 
  *   全局Exception捕获RestController
 * <p>
 * Created on 2017年6月19日 
 * <p>
 * @author mmdai
 * @date 2017年6月19日
 */

@RestControllerAdvice
public class GlobalDefaultExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

	private static final String GLOBAL_HANDLER_TITLE = "GlobalExceptionHandler: ";

    private static final int KILO = 1024;
	@Autowired
	@Qualifier("floodMessageSource")
    private MessageSource messageSource;

	@Autowired
	private LocaleParser localeParser;

	private static final String ZH_CN_1 = "zh-CN";

	private static final String ZH_CN_2 = "zh_CN";
	//i18默认方言
	@Value("${spring.messages.locale:zh_CN}")
	private String localeContent;
	
	/**
	 * 
	 * <p>Title: handleMaxUploadSizeExceededException</p>  
	 * <p>Description: file upload error 使用默认错误码S00001</p>  
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
    public Object handleMaxUploadSizeExceededException(HttpServletRequest req, MaxUploadSizeExceededException ex) {
        logger.error(GLOBAL_HANDLER_TITLE, ex);
        ErrorMessage error = new ErrorMessage();
		String code = GlobalErrorCodeEnum.BAD_REQUEST.getCode();
		String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
		String message = "上传文件大小超出系统限制 (" + ex.getMaxUploadSize() / KILO / KILO + "MB)";
		if(!StringUtils.isEmpty(langContent)){
			if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
				message = "upload file size exceeds system limit (" + ex.getMaxUploadSize() / KILO / KILO + "MB)";
			}
		}else{
			if (!ZH_CN_2.equals(localeContent)){
				message = "upload file size exceeds system limit (" + ex.getMaxUploadSize() / KILO / KILO + "MB)";
			}
		}
		logger.error(">>>retCode:{}, retMsg:{}",code, message);
		error.set_code(code);
		error.set_msg(message);
		return error;
    }
	/**
	 * 
	 * <p>Title: bindErrorHandler</p>  
	 * <p>Description: on form submit,数据验证错误 都使用默认错误码S00001
	  *   如果controller中@Valid LoginDTO loginDTO, 
	  *   其中name属性定义了验证器如下
	 * @Pattern(regexp = "(^[\u4E00-\u9FA5]{1,6})", message="格式不正确")
	 * private String name;
	  *   那么在properties中需要定义国际化翻译:beanname（bean的类名首字母小写）+属性字段
	 * loginDTO.name=姓名格式不正确；
	  *  则前端收到报文：{"_code":"A00000", "_msg":"姓名格式不正确"}		 
	 * </p>  
	 * @param ex
	 * @return
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
		if(message == null) {
			String field_err_code = f_error.getObjectName() + "." +f_error.getField();
			message = messageSource.getMessage(field_err_code, null, getLocaleByLanguage(req));
		}
		return getErrorMessage(req, error, code, message);
	}

	private ErrorMessage getErrorMessage(HttpServletRequest req, ErrorMessage error, String code, String message) {
		String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
		if(StringUtils.isEmpty(langContent)){
			langContent = localeContent;
		}
		message = localeParser.replacePlaceHolderByLocale(message, langContent);
		logger.error(">>>retCode:{}, retMsg:{}",code, message);
		error.set_code(code);
		error.set_msg(message);
		return error;
	}

	/**
	 * 
	 * <p>Title: validJsonErrorHandler</p>  
	 * <p>Description: on json submit,数据验证错误 都使用默认错误码S00001
	  *    如果controller中@Valid@RquestBody LoginDTO loginDTO, 
	  *   其中name属性定义了验证器如下
	 * @Pattern(regexp = "(^[\u4E00-\u9FA5]{1,6})", message="{&NAME_ERROR}")
	 * 国际化配置messages_zh_CN.properties {&NAME_ERROR}=姓名格式不正确
	 * 国际化配置messages_en_US.properties {&NAME_ERROR}=name error
	 * private String name;
	  *   那么在properties中需要定义国际化翻译:beanname（bean的类名首字母小写）+属性字段
	 * loginDTO.name=姓名格式不正确；
	  *  则前端收到报文：{"_code":"A00000", "_msg":"姓名格式不正确"}		 
	 * </p>    
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ErrorMessage validJsonErrorHandler(HttpServletRequest req, MethodArgumentNotValidException ex) {
		logger.error(GLOBAL_HANDLER_TITLE, ex);
		ErrorMessage error = new ErrorMessage();
		String code = GlobalErrorCodeEnum.BAD_REQUEST.getCode();
		BindingResult bindingResult = ex.getBindingResult();
		FieldError f_error = bindingResult.getFieldError();
		//优先获取@Valid Bean定义的message内容信息
		String message = f_error.getDefaultMessage();
		if(StringUtils.isEmpty(message)) {
			String field_err_code = f_error.getObjectName() + "." +f_error.getField();
			message = messageSource.getMessage(field_err_code, null, getLocaleByLanguage(req));
		}
		return getErrorMessage(req, error, code, message);
	}
	/**
	 * 
	 * <p>Title: defaultErrorHandler</p>  
	 * <p>Description: 其他异常信息</p>
	 * @param req
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = Exception.class)
	public ErrorMessage defaultErrorHandler(HttpServletRequest req, Exception ex) {
		logger.error(GLOBAL_HANDLER_TITLE, ex);
		ErrorMessage error = new ErrorMessage();
		String code = null;
		String message = null;
		Object[] arguments = null;
		if (ex instanceof MessageSourceResolvable) {
			MessageSourceResolvable exm = (MessageSourceResolvable)ex;
			if (exm.getCodes() != null) {
				code = exm.getCodes()[0];
				message = exm.getDefaultMessage();
			}
			if(exm.getArguments() != null) {
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
			if(!StringUtils.isEmpty(langContent)){
				if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
					message = GlobalErrorCodeEnum.INTERNAL_SERVER_ERROR.getEnName();
				}
			}
		}
		if (StringUtils.isEmpty(message)) {
			message = messageSource.getMessage(code, arguments, getLocaleByLanguage(req));
		}else{
			String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
			if(StringUtils.isEmpty(langContent)){
				langContent = localeContent;
			}
			message = localeParser.replacePlaceHolderByLocale(message, langContent);
		}
		logger.error(">>>retCode:{}, retMsg:{}",code, message);
		error.set_code(code);
		error.set_msg(message);
		return error;
	}

	/**
	 *
	 * @param req
	 * @return
	 */
	private Locale getLocaleByLanguage(HttpServletRequest req){
		Locale locale;
		String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
		if(StringUtils.isBlank(langContent)){
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
