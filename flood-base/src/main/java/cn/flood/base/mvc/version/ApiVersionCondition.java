package cn.flood.base.mvc.version;

import cn.flood.base.core.constants.HeaderConstant;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {


  private String apiVersion;

  private VersionOperator operator;

  public ApiVersionCondition(String apiVersion, VersionOperator operator) {
    this.apiVersion = apiVersion;
    this.operator = operator;
  }

  @Override
  public ApiVersionCondition combine(ApiVersionCondition other) {
    // 如果已有定义，返回原先的即可。
    if (other.operator != null) {
      int i = other.getApiVersion().compareToIgnoreCase(this.apiVersion);
      switch (operator) {
        case GT:
          return i > 0 ? this : other;
        case GTE:
          return i >= 0 ? this : other;
        case LT:
          return i < 0 ? this : other;
        case LTE:
          return i <= 0 ? this : other;
        case EQ:
          return i == 0 ? this : other;
        case NE:
          return i != 0 ? this : other;
        default:
          return other;
      }
    }
    return other;
  }

  @Override
  public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
    String v = request.getHeader(HeaderConstant.HEADER_VERSION);
    String version = HeaderConstant.DEFAULT_VERSION;
    if (StringUtils.isNotBlank(v)) {
      version = String.valueOf(v);
    }
    // 如果请求的版本号等于配置版本号，则满足
    if (this.operator != null) {
      int i = version.compareToIgnoreCase(this.apiVersion);
      switch (operator) {
        case GT:
          return i > 0 ? this : null;
        case GTE:
          return i >= 0 ? this : null;
        case LT:
          return i < 0 ? this : null;
        case LTE:
          return i <= 0 ? this : null;
        case EQ:
          return i == 0 ? this : null;
        case NE:
          return i != 0 ? this : null;
        default:
          return null;
      }
    }
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
