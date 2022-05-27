/**
 * Copyright (c) 2018-2028,
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.flood.http;

import cn.flood.Func;
import cn.flood.UserToken;
import cn.flood.json.JsonUtils;
import cn.flood.lang.ClassUtil;
import cn.flood.lang.StringUtils;
import cn.flood.rpc.response.Result;
import cn.flood.rpc.response.ResultWapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import reactor.core.publisher.Mono;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;


/**
 * Miscellaneous utilities for web applications.
 *
 * @author mmdai
 */
public class WebUtil extends org.springframework.web.util.WebUtils {

	private static final Logger log = LoggerFactory.getLogger(WebUtil.class);

	public static final String REQUEST_TOKEN_NAME = "flood-token";  // request中存储token的name

	public static final String USER_AGENT_HEADER = "user-agent";

	public static final String UN_KNOWN = "unknown";

	/**
	 * 判断是否ajax请求
	 * spring ajax 返回含有 ResponseBody 或者 RestController注解
	 *
	 * @param handlerMethod HandlerMethod
	 * @return 是否ajax请求
	 */
	public static boolean isBody(HandlerMethod handlerMethod) {
		ResponseBody responseBody = ClassUtil.getAnnotation(handlerMethod, ResponseBody.class);
		return responseBody != null;
	}

	/**
	 * 读取cookie
	 *
	 * @param name cookie name
	 * @return cookie value
	 */
	@Nullable
	public static String getCookieVal(String name) {
		HttpServletRequest request = WebUtil.getRequest();
		Assert.notNull(request, "request from RequestContextHolder is null");
		return getCookieVal(request, name);
	}

	/**
	 * 读取cookie
	 *
	 * @param request HttpServletRequest
	 * @param name    cookie name
	 * @return cookie value
	 */
	@Nullable
	public static String getCookieVal(HttpServletRequest request, String name) {
		Cookie cookie = getCookie(request, name);
		return cookie != null ? cookie.getValue() : null;
	}

	/**
	 * 清除 某个指定的cookie
	 *
	 * @param response HttpServletResponse
	 * @param key      cookie key
	 */
	public static void removeCookie(HttpServletResponse response, String key) {
		setCookie(response, key, null, 0);
	}

	/**
	 * 设置cookie
	 *
	 * @param response        HttpServletResponse
	 * @param name            cookie name
	 * @param value           cookie value
	 * @param maxAgeInSeconds maxage
	 */
	public static void setCookie(HttpServletResponse response, String name, @Nullable String value, int maxAgeInSeconds) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(maxAgeInSeconds);
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
	}

	/**
	 * 获取 HttpServletRequest
	 *
	 * @return {HttpServletRequest}
	 */
	public static HttpServletRequest getRequest() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		return (requestAttributes == null) ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
	}

	/**
	 * 返回json
	 *
	 * @param response HttpServletResponse
	 * @param result   结果对象
	 */
	public static void renderJson(HttpServletResponse response, Object result) {
		renderJson(response, result, MediaType.APPLICATION_JSON_VALUE);
	}

	/**
	 * 返回json
	 *
	 * @param response    HttpServletResponse
	 * @param result      结果对象
	 * @param contentType contentType
	 */
	public static void renderJson(HttpServletResponse response, Object result, String contentType) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType(contentType);
		try (PrintWriter out = response.getWriter()) {
			out.append(JsonUtils.toJSONString(result));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 获取ip
	 *
	 * @return {String}
	 */
	public static String getIP() {
		return getIP(WebUtil.getRequest());
	}

	/**
	 * 获取ip
	 *
	 * @param request HttpServletRequest
	 * @return {String}
	 */
	@Nullable
	public static String getIP(HttpServletRequest request) {
		Assert.notNull(request, "HttpServletRequest is null");
		String ip = request.getHeader("X-Requested-For");
		if (Func.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (Func.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (Func.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (Func.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (Func.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (Func.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return Func.isEmpty(ip) ? null : ip.split(",")[0];
	}


	/***
	 * 获取 request 中 json 字符串的内容
	 *
	 * @param request request
	 * @return 字符串内容
	 */
	public static String getRequestParamString(HttpServletRequest request) {
		try {
			return getRequestStr(request);
		} catch (Exception ex) {
			return "";
		}
	}

	/**
	 * 获取 request 请求内容
	 *
	 * @param request request
	 * @return String
	 * @throws IOException IOException
	 */
	public static String getRequestStr(HttpServletRequest request) throws IOException {
		String queryString = request.getQueryString();
		if (StringUtils.isNotNull(queryString)) {
			return new String(queryString.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8).replaceAll("&amp;", "&").replaceAll("%22", "\"");
		}
		return getRequestStr(request, getRequestBytes(request));
	}

	/**
	 * 获取 request 请求的 byte[] 数组
	 *
	 * @param request request
	 * @return byte[]
	 * @throws IOException IOException
	 */
	public static byte[] getRequestBytes(HttpServletRequest request) throws IOException {
		int contentLength = request.getContentLength();
		if (contentLength < 0) {
			return null;
		}
		byte[] buffer = new byte[contentLength];
		for (int i = 0; i < contentLength; ) {

			int readlen = request.getInputStream().read(buffer, i, contentLength - i);
			if (readlen == -1) {
				break;
			}
			i += readlen;
		}
		return buffer;
	}

	/**
	 * 获取 request 请求内容
	 *
	 * @param request request
	 * @param buffer buffer
	 * @return String
	 * @throws IOException IOException
	 */
	public static String getRequestStr(HttpServletRequest request, byte[] buffer) throws IOException {
		String charEncoding = request.getCharacterEncoding();
		if (charEncoding == null) {
			charEncoding = "utf-8";
		}
		String str = new String(buffer, charEncoding).trim();
		if (ObjectUtils.isEmpty(str)) {
			StringBuilder sb = new StringBuilder();
			Enumeration<String> parameterNames = request.getParameterNames();
			while (parameterNames.hasMoreElements()) {
				String key = parameterNames.nextElement();
				String value = request.getParameter(key);
				StringUtils.appendBuilder(sb, key, "=", value, "&");
			}
			str = StringUtils.removeSuffix(sb.toString(), "&");
		}
		return str.replaceAll("&amp;", "&");
	}

	/**
	 * 获取用户信息
	 *
	 * @return User
	 */
	public static UserToken getUser() {
		HttpServletRequest request = WebUtil.getRequest();
		if (request == null) {
			return null;
		}
		// 优先从 request 中获取
		Object floodUser = request.getAttribute(REQUEST_TOKEN_NAME);
		if (floodUser == null) {
			if (floodUser != null) {
				// 设置到 request 中
				request.setAttribute(REQUEST_TOKEN_NAME, floodUser);
			}
		}
		return (UserToken) floodUser;
	}

	/**
	 * 设置响应
	 *
	 * @param response    HttpServletResponse
	 * @param contentType content-type
	 * @param status      http状态码
	 * @param value       响应内容
	 * @throws IOException IOException
	 */
	public static void responseWriter(HttpServletResponse response, String contentType,
									  int status, Object value) throws IOException {
		response.setContentType(contentType);
		response.setStatus(status);
		response.getOutputStream().write(Func.toJson(value).getBytes());
	}

	/**
	 * 设置webflux模型响应
	 *
	 * @param response    ServerHttpResponse
	 * @param contentType content-type
	 * @param status      http状态码
	 * @param value       响应内容
	 * @return Mono<Void>
	 */
	public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String contentType,
												   HttpStatus status, Object value) {
		response.setStatusCode(status);
		response.getHeaders().add(HttpHeaders.CONTENT_TYPE, contentType);
		Result<?> result = ResultWapper.wrap(String.valueOf(status.value()), value.toString());
		DataBuffer dataBuffer = response.bufferFactory().wrap(Func.toJson(result).getBytes());
		return response.writeWith(Mono.just(dataBuffer));
	}


}

