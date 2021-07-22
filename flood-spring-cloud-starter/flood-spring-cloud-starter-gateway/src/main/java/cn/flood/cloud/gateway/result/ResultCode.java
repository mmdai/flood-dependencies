package cn.flood.cloud.gateway.result;

import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

/**
 * 返回码实现
 *
 */

public enum ResultCode {

	/**
	 * 操作成功
	 */
	SUCCESS("000000", "success"),
	/**
	 * 业务异常
	 */
	FAILURE("400", "Business Error"),
	/**
	 * 服务未找到
	 */
	NOT_FOUND("404", "Not Found"),
	/**
	 * 服务异常
	 */
	ERROR("500", "Internal Server Error"),
	/**
	 * 服务超时
	 */
	TIME_ERROR("504", "Service Connect Timeout"),
	/**
	 * Too Many Requests
	 */
	TOO_MANY_REQUESTS("429", "Too Many Requests"),
	/**
	 * Service Unavailable
	 */
	SERVICE_UNAVAILABLE("503", "Service Unavailable"),
	/**
	 * GATEWAY_TIMEOUT
	 */
	GATEWAY_TIMEOUT("504",  "Gateway Timeout");

	/**
	 * 状态码
	 */
	final String code;
	/**
	 * 消息内容
	 */
	final String msg;


	// 构造方法
	ResultCode(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	// 普通方法
	public static String getMsg(String code) {
		for (ResultCode c : ResultCode.values()) {
			if (c.getCode().equals(code)) {
				return c.msg;
			}
		}
		return null;
	}

	// get set 方法


	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public static ResultCode valueOfEnum(String code) {
		return Stream.of(ResultCode.values()).
				filter(resultEnum -> resultEnum.code.equals(code)).
				findFirst().orElse(null);
	}
}
