package cn.flood.base.core.support.xss;


import cn.flood.base.core.Func;
import cn.flood.base.core.http.HttpMediaType;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/4/14 10:58
 */
@AllArgsConstructor
public class XssFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(XssFilter.class);

    private final XssProperties xssProperties;
    private final XssUrlProperties xssUrlProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("=========XssFilter init========");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String path = httpServletRequest.getServletPath();
        if (isSkip(path)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            String contentType = httpServletRequest.getHeader("Content-Type");
            if(Func.isNotEmpty(contentType) && contentType.contains(HttpMediaType.APPLICATION_JSON_VALUE)){
                log.debug("XssFilter filter start... {}", httpServletRequest.getRequestURI());
                XssFilterServletRequestWrapper xssFilterServletRequestWrapper = new XssFilterServletRequestWrapper(httpServletRequest);
                filterChain.doFilter(xssFilterServletRequestWrapper, servletResponse);
                log.debug("XssFilter filter end...");
            }else{
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }

    }

    @Override
    public void destroy() {
        log.info("=========XssFilter destroy========");
    }


    private boolean isSkip(String path) {
        return (xssUrlProperties.getExcludePatterns().stream().anyMatch(pattern -> antPathMatcher.match(pattern, path)))
                || (xssProperties.getSkipUrl().stream().anyMatch(pattern -> antPathMatcher.match(pattern, path)));
    }

}
