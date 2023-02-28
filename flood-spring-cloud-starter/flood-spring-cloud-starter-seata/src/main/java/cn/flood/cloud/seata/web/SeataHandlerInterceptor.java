package cn.flood.cloud.seata.web;

import io.seata.common.util.StringUtils;
import io.seata.core.context.RootContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/4/25 10:29
 */
public class SeataHandlerInterceptor implements HandlerInterceptor {

  private static final Logger log = LoggerFactory.getLogger(SeataHandlerInterceptor.class);

  public SeataHandlerInterceptor() {
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    String xid = RootContext.getXID();
    String rpcXid = request.getHeader("TX_XID");
    if (log.isDebugEnabled()) {
      log.debug("xid in RootContext {} xid in RpcContext {}", xid, rpcXid);
    }

    if (StringUtils.isBlank(xid) && rpcXid != null) {
      RootContext.bind(rpcXid);
      if (log.isDebugEnabled()) {
        log.debug("bind {} to RootContext", rpcXid);
      }
    }

    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception e) {
    if (StringUtils.isNotBlank(RootContext.getXID())) {
      String rpcXid = request.getHeader("TX_XID");
      if (StringUtils.isEmpty(rpcXid)) {
        return;
      }

      String unbindXid = RootContext.unbind();
      if (log.isDebugEnabled()) {
        log.debug("unbind {} from RootContext", unbindXid);
      }

      if (!rpcXid.equalsIgnoreCase(unbindXid)) {
        log.warn("xid in change during RPC from {} to {}", rpcXid, unbindXid);
        if (unbindXid != null) {
          RootContext.bind(unbindXid);
          log.warn("bind {} back to RootContext", unbindXid);
        }
      }
    }

  }
}
