package cn.flood.core.security.handle;

import cn.flood.http.WebUtil;
import cn.flood.rpc.response.ResultWapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FloodAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
		int status = HttpServletResponse.SC_UNAUTHORIZED;
		WebUtil.responseWriter(response, MediaType.APPLICATION_JSON_VALUE, status, ResultWapper.wrap(String.valueOf(status), "访问令牌不合法"));
	}
}
