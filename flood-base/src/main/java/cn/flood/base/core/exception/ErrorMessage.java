package cn.flood.base.core.exception;

import java.io.Serializable;

/**
 * 
 * 错误信息实体类
 * <p>
 * Created on 2017年6月19日 
 * <p>
 * @author mmdai
 * @date 2017年6月19日
 */
public class ErrorMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5276841118425404723L;
	/**
	 * 错误码
	 */
	private String _code;

	/**
	 * 错误信息说明，实际从message.properties中翻译
	 */
	private String _msg;


	public String get_code() {
		return _code;
	}

	public void set_code(String _code) {
		this._code = _code;
	}

	public String get_msg() {
		return _msg;
	}

	public void set_msg(String _msg) {
		this._msg = _msg;
	}
}
