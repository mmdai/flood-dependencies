package cn.flood.filter;

import cn.flood.requestWrapper.XssFilterServletRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
/**
 * @author mmdai
 * @version 1.0
 * @date 2022/4/14 10:58
 */
@WebFilter(urlPatterns = "/*", filterName = "xssFilter")
public class XssFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(XssFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("==============XssFilter init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        log.debug("XssFilter filter start... {}", httpServletRequest.getRequestURI());
        XssFilterServletRequestWrapper xssFilterServletRequestWrapper = new XssFilterServletRequestWrapper(httpServletRequest);
        filterChain.doFilter(xssFilterServletRequestWrapper, servletResponse);
        log.debug("XssFilter filter end...");
    }

    @Override
    public void destroy() {
        log.info("==============XssFilter destroy");
    }
}
