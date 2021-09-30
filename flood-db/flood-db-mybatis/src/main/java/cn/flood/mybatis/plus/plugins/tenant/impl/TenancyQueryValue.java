package cn.flood.mybatis.plus.plugins.tenant.impl;

import cn.flood.context.TenantContextHolder;
import cn.flood.mybatis.plus.plugins.tenant.MultiTenancyQueryValueFactory;

public class TenancyQueryValue implements MultiTenancyQueryValueFactory {

    @Override
    public Object buildMultiTenancyQueryValue() {
        return TenantContextHolder.getTenantId();
    }
}
