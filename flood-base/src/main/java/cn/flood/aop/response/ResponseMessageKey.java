package cn.flood.aop.response;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 
 *  TODO 错误信息实体类
 * <p>
 * Created on 2017年6月19日 
 * <p>
 * @author mmdai
 * @date 2017年6月19日
 */
@Configuration
@ComponentScan("cn.flood.aop")
public class ResponseMessageKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5276841118425404723L;
	//错误码
	@Value("${flood.response.codekey:_code}")
	private String codeKey;

	//错误信息说明，实际从message.properties中翻译
	@Value("${flood.response.msgkey:_msg}")
	private String msgKey;
	
	@Value("${flood.response.usedatakey:true}")
	private boolean useDataKey;

	@Value("${flood.response.datakey:_data}")
	private String dataKey;
	
	@Value("${flood.response.errorkey:S00000}")
	private String errorKey;
	
	@Value("${flood.response.errormsg:SYSTEM ERROR}")
	private String errorMsg;
	
	@Value("${flood.response.errorparamkey:S00001}")
	private String errorParamKey;
	
	@Value("${flood.response.successcode:000000}")
	private String successCode;
	
	@Value("${flood.response.successmsg:success}")
	private String successMsg;

	/**
	 * @return the codeKey
	 */
	public String getCodeKey() {
		return codeKey;
	}

	/**
	 * @param codeKey the codeKey to set
	 */
	public void setCodeKey(String codeKey) {
		this.codeKey = codeKey;
	}

	/**
	 * @return the msgKey
	 */
	public String getMsgKey() {
		return msgKey;
	}

	/**
	 * @param msgKey the msgKey to set
	 */
	public void setMsgKey(String msgKey) {
		this.msgKey = msgKey;
	}

	/**
	 * @return the useDataKey
	 */
	public boolean isUseDataKey() {
		return useDataKey;
	}

	/**
	 * @param useDataKey the useDataKey to set
	 */
	public void setUseDataKey(boolean useDataKey) {
		this.useDataKey = useDataKey;
	}

	/**
	 * @return the dataKey
	 */
	public String getDataKey() {
		return dataKey;
	}

	/**
	 * @param dataKey the dataKey to set
	 */
	public void setDataKey(String dataKey) {
		this.dataKey = dataKey;
	}

	/**
	 * @return the errorKey
	 */
	public String getErrorKey() {
		return errorKey;
	}

	/**
	 * @param errorKey the errorKey to set
	 */
	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 * @return the errorParamKey
	 */
	public String getErrorParamKey() {
		return errorParamKey;
	}

	/**
	 * @param errorParamKey the errorParamKey to set
	 */
	public void setErrorParamKey(String errorParamKey) {
		this.errorParamKey = errorParamKey;
	}

	/**
	 * @return the successCode
	 */
	public String getSuccessCode() {
		return successCode;
	}

	/**
	 * @param successCode the successCode to set
	 */
	public void setSuccessCode(String successCode) {
		this.successCode = successCode;
	}

	/**
	 * @return the successMsg
	 */
	public String getSuccessMsg() {
		return successMsg;
	}

	/**
	 * @param successMsg the successMsg to set
	 */
	public void setSuccessMsg(String successMsg) {
		this.successMsg = successMsg;
	}

	
}
