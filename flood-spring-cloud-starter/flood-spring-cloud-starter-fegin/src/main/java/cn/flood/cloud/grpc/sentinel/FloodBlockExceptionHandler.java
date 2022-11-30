package cn.flood.cloud.grpc.sentinel;

import cn.flood.base.core.exception.ErrorMessage;
import cn.flood.base.core.exception.enums.GlobalErrorCodeEnum;
import cn.flood.base.core.json.JsonUtils;
import cn.flood.base.core.rpc.response.ResultWapper;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Sentinel统一限流策略
 *
 * @author mmdai
 */
@Slf4j
public class FloodBlockExceptionHandler implements BlockExceptionHandler {

	private static final String UTF_8 = "UTF-8";

	private static final String ZH_CN_1 = "zh-CN";

	private static final String ZH_CN_2 = "zh_CN";

	@Override
	public void handle(HttpServletRequest req, HttpServletResponse response, BlockException ex) throws Exception {
		log.error("sentinel 降级 资源名称{}", ex.getRule().getResource(), ex);

		String code = "";
		String message = "";
		if(ex instanceof FlowException) {//限流异常
			// Return 429 (Too Many Requests) by default.
			//降级异常
			code = GlobalErrorCodeEnum.TOO_MANY_REQUESTS.getCode();
			message = GlobalErrorCodeEnum.TOO_MANY_REQUESTS.getZhName();
			String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
			if(!ObjectUtils.isEmpty(langContent)){
				if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
					message = GlobalErrorCodeEnum.TOO_MANY_REQUESTS.getEnName();
				}
			}
		}else if (ex instanceof DegradeException) {
			//降级异常
			code = GlobalErrorCodeEnum.DEGRADE_SERVER_ERROR.getCode();
			message = GlobalErrorCodeEnum.DEGRADE_SERVER_ERROR.getZhName();
			String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
			if(!ObjectUtils.isEmpty(langContent)){
				if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
					message = GlobalErrorCodeEnum.DEGRADE_SERVER_ERROR.getEnName();
				}
			}
		}else if (ex instanceof ParamFlowException) {
			//热点参数异常
			code = GlobalErrorCodeEnum.PARAM_FLOW_SERVER_ERROR.getCode();
			message = GlobalErrorCodeEnum.PARAM_FLOW_SERVER_ERROR.getZhName();
			String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
			if(!ObjectUtils.isEmpty(langContent)){
				if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
					message = GlobalErrorCodeEnum.PARAM_FLOW_SERVER_ERROR.getEnName();
				}
			}
		}else if (ex instanceof SystemBlockException) {
			//系统保护规则
			code = GlobalErrorCodeEnum.SYSTEM_BLOCK_SERVER_ERROR.getCode();
			message = GlobalErrorCodeEnum.SYSTEM_BLOCK_SERVER_ERROR.getZhName();
			String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
			if(!ObjectUtils.isEmpty(langContent)){
				if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
					message = GlobalErrorCodeEnum.SYSTEM_BLOCK_SERVER_ERROR.getEnName();
				}
			}
		}else if (ex instanceof AuthorityException) {
			//授权规则
			code = GlobalErrorCodeEnum.AUTHORITY_SERVER_ERROR.getCode();
			message = GlobalErrorCodeEnum.AUTHORITY_SERVER_ERROR.getZhName();
			String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
			if(!ObjectUtils.isEmpty(langContent)){
				if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
					message = GlobalErrorCodeEnum.AUTHORITY_SERVER_ERROR.getEnName();
				}
			}
		} else {
			//其他规则
			code = GlobalErrorCodeEnum.UNKNOWN.getCode();
			message = GlobalErrorCodeEnum.UNKNOWN.getZhName();
			String langContent = req.getHeader(HttpHeaders.CONTENT_LANGUAGE);
			if(!ObjectUtils.isEmpty(langContent)){
				if (!ZH_CN_1.equals(langContent) && !ZH_CN_2.equals(langContent)) {
					message = GlobalErrorCodeEnum.UNKNOWN.getEnName();
				}
			}
		}
		// Return 429 (Too Many Requests) by default.
		//在获取流对象之前告诉浏览器使用什么字符集
		response.setCharacterEncoding(UTF_8);
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().print(JsonUtils.toJSONString(ResultWapper.wrap(code, message)));
	}

}
