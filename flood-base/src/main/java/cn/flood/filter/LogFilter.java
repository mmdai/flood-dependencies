package cn.flood.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;


/**
 * 
 *  TODO 配置请求上线文添加sessionId和requestId，用于日志跟踪查询
 * <p>
 * 自定义过滤器 将一个实现了javax.servlet.Filter接口的类定义为过滤器 属性filterName声明过滤器的名称,可选属性urlPatterns指定要过滤 的URL模式,也可使用属性value来声明.(指定要过滤的URL模式是必选属性) 
 * <p>
 * @author mmdai
 * @date 2017年6月19日
 */

@WebFilter(filterName = "filterEx", urlPatterns = "/*")
public class LogFilter implements Filter {

//	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 会话ID
	 */
	private final static String SESSION_KEY = "sessionId";

	private final static String REQUEST_ID = "requestId";

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
//		logger.info("------->销毁 FilterEx");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub

		setData(request);
//		logger.info("------->执行  FilterEx 操作 ");
		try {
			chain.doFilter(request, response);
		} finally {
			clearData();
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
//		logger.info("------->初始化 FilterEx");
	}

	private void setData(ServletRequest request) {
		HttpServletRequest req = (HttpServletRequest) request;
		
		String req_id = UUID.randomUUID().toString();
		MDC.put(SESSION_KEY, req.getSession().getId());
		req.getSession().setAttribute(REQUEST_ID, req_id);
		MDC.put(REQUEST_ID, req_id);
	}

	private void clearData() {

		// 删除
		MDC.remove(SESSION_KEY);
		MDC.remove(REQUEST_ID);
	}
}
