package cn.flood.cloud.grpc.sentinel;

import cn.flood.base.core.json.JsonUtils;
import cn.flood.base.core.rpc.response.ResultWapper;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Sentinel统一限流策略
 *
 * @author mmdai
 */
@Slf4j
public class FloodBlockExceptionHandler implements BlockExceptionHandler {
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
		log.error("sentinel 降级 资源名称{}", e.getRule().getResource(), e);
		// Return 429 (Too Many Requests) by default.
		response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().print(JsonUtils.toJSONString(ResultWapper.error(e.getMessage())));
	}
}