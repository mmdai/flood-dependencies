package cn.flood.mvc.version;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;

public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {
    /**
     * 标准协议指定 请求header头需带上版本号flood-api-version
     */
    public static final String HEADER_VERSION = "flood-api-version";
    /**
     * 为了兼容旧版本无请求头,默认从1.0版本开始
     */
    private static final String DEFAULT_VERSION = "1.0";
    private String apiVersion;

    public ApiVersionCondition(String apiVersion) {
            this.apiVersion = apiVersion;
    }
    @Override
    public ApiVersionCondition combine(ApiVersionCondition other) {
        //如果已有定义，返回原先的即可。
        if(this.apiVersion.equals(other.getApiVersion())) return this;
        return other;
    }

    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        String v = request.getHeader(HEADER_VERSION);
        String version = DEFAULT_VERSION;
        if(StringUtils.isNotBlank(v)) {
            version = String.valueOf(v);
        }
        // 如果请求的版本号等于配置版本号， 则满足
        if(version.equals(this.apiVersion))
            return this;
        return null;
    }

    /**
    * 如果匹配到两个都符合版本需求的（理论上不应该有）
    */
    @Override
    public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
        return 0;
        // 优先匹配最新的版本号，业务上暂不需要此功能
        //return Double.compare(other.getApiVersion(), this.apiVersion);
    }

    public String getApiVersion() {
        return apiVersion;
    }

}
