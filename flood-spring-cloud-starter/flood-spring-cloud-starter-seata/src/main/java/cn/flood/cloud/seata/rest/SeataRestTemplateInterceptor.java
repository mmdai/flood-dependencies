package cn.flood.cloud.seata.rest;

import cn.flood.base.core.Func;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.seata.core.context.RootContext;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/4/20 12:36
 */
public class SeataRestTemplateInterceptor implements RequestInterceptor {

  @Override
  public void apply(RequestTemplate requestTemplate) {
    String xid = RootContext.getXID();
    if (Func.isNotEmpty(xid)) {
      requestTemplate.header(RootContext.KEY_XID, xid);
    }
  }
}
