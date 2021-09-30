package cn.flood.mybatis.plus.plugins.tenant;

/**
 * 多租户条件查询值Factory
 *
 * @Author iloveoverfly
 * @LocalDateTime 2020/6/15 17:00
 **/
public interface MultiTenancyQueryValueFactory {

    Object buildMultiTenancyQueryValue();
}
