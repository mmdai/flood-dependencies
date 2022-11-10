package cn.flood.base.core.rpc.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T>{

	/** 编号 **/
	private String _code;
	/** 信息 **/
	private String _msg;
	/** 结果 **/
	private T _data;
   
	/**
	 * 成功码.
	 */
	public static final String SUCCESS_CODE = "000000";

	/**
	 * 成功信息.
	 */
	public static final String SUCCESS_MSG = "success";

	/**
	 * 错误码.
	 */
	public static final String ERROR_CODE = "S00000";

	/**
	 * 错误信息.
	 */
	public static final String ERROR_MESSAGE = "系统内部错误";

	@JsonIgnore
	public boolean is_succeed(){
		if ((this._code == null) || (this._code == "")) {
			return false;
		}
		return this._code.equals(SUCCESS_CODE);
	}
	
	@JsonIgnore
	public boolean error() {
		return !is_succeed();
	}

	Result() {
		this(SUCCESS_CODE, SUCCESS_MSG);
	}
	
	Result(String code, String msg) {
		this(code, msg, null);
	}
	
	Result(String code, String msg, T data) {
		super();
		code(code).message(msg).data(data);
	}
	
	/**
	 * Sets the 编号 , 返回自身的引用.
	 *
	 * @param code the new 编号
	 *
	 * @return the wrapper
	 */
	private Result<T> code(String code) {
		this.set_code(code);
		return this;
	}

	/**
	 * Sets the 信息 , 返回自身的引用.
	 *
	 * @param msg the new 信息
	 *
	 * @return the Result
	 */
	private Result<T> message(String msg) {
		this.set_msg(msg);
		return this;
	}

	/**
	 * Sets the 结果数据 , 返回自身的引用.
	 *
	 * @param data the new 结果数据
	 *
	 * @return the wrapper
	 */
	public Result<T> data(T data) {
		this.set_data(data);
		return this;
	}
	@Override
	public String toString() {
		return "Result [_code=" + _code + ", _msg=" + _msg + ", _data=" + _data + "]";
	}
	
	
}