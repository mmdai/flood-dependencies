package cn.flood.core.security.handle;

import cn.flood.exception.enums.GlobalErrorCodeEnum;
import cn.flood.http.WebUtil;
import cn.flood.rpc.response.ResultWapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FloodAccessDeniedHandler implements AccessDeniedHandler {
	@Override
	public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
		WebUtil.responseWriter(httpServletResponse, MediaType.APPLICATION_JSON_VALUE, HttpServletResponse.SC_FORBIDDEN,
				ResultWapper.wrap(GlobalErrorCodeEnum.FORBIDDEN.getCode(), GlobalErrorCodeEnum.FORBIDDEN.getEnName()));
	}
}
