package cn.flood.mvc.version;

import cn.flood.constants.HeaderConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {


    private String[] apiVersion;

    public ApiVersionCondition(String[] apiVersion) {
            this.apiVersion = apiVersion;
    }
    @Override
    public ApiVersionCondition combine(ApiVersionCondition other) {
        //如果已有定义，返回原先的即可。
        if(Arrays.asList(this.apiVersion).contains(other.getApiVersion())) return this;
        return other;
    }

    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        String v = request.getHeader(HeaderConstant.HEADER_VERSION);
        String version = HeaderConstant.DEFAULT_VERSION;
        if(StringUtils.isNotBlank(v)) {
            version = String.valueOf(v);
        }
        // 如果请求的版本号等于配置版本号， 则满足
        if(Arrays.asList(this.apiVersion).contains(version))
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

    public String[] getApiVersion() {
        return apiVersion;
    }

}
