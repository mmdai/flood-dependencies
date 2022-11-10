package cn.flood.db.mybatis.plus.plugins.tenant.impl;

import cn.flood.base.core.context.TenantContextHolder;
import cn.flood.db.mybatis.plus.plugins.tenant.MultiTenancyQueryValueFactory;

public class TenancyQueryValue implements MultiTenancyQueryValueFactory {

    @Override
    public Object buildMultiTenancyQueryValue() {
        return TenantContextHolder.getTenantId();
    }
}
