package cn.flood.oauth.configuration;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(prefix = "fpermission")
public class FloodPermissionProperties {

  /**
   * 监控中心和swagger需要访问的url
   */
  private static final String[] ENDPOINTS = {
      "/actuator/**",
      "/monitor/**",
      "/v2/api-docs/**",
      "/v3/api-docs/**",
      "/swagger/api-docs",
      "/swagger-ui.html",
      "/doc.html",
      "/swagger-resources/**",
      "/webjars/**",
      "/druid/**",
      "/error/**",
      "/assets/**",
      "/js/**",
      "/favicon.ico"
  };

  /**
   * token存储方式，0 redis存储，1 数据库存储
   */
  private Integer storeType = 0;

  /**
   * url自动对应权限方式，0 简易模式，1 RESTful模式
   */
  private Integer urlPermType;

  /**
   * 拦截路径，多个路径用逗号分隔
   */
  private String[] path = new String[]{"/**"};

  /**
   * 排除拦截路径，多个路径用逗号分隔
   */
  private String[] excludePath = new String[]{};

  /**
   * 单个用户最大的token数量
   */
  private Integer maxToken = -1;

  /**
   * 自定义查询用户角色的sql
   */
  private String findRolesSql;

  /**
   * 自定义查询用户权限的sql
   */
  private String findPermissionsSql;

  /**
   * 统一认证中心地址
   */
  private String authCenterUrl;

  private String viewResolverPrefix = "/static/";

  private String viewResolverSuffix = ".html";

  public Integer getStoreType() {
    return storeType;
  }

  public void setStoreType(Integer storeType) {
    this.storeType = storeType;
  }

  public Integer getUrlPermType() {
    return urlPermType;
  }

  public void setUrlPermType(Integer urlPermType) {
    this.urlPermType = urlPermType;
  }

  public String[] getPath() {
    return path;
  }

  public void setPath(String[] path) {
    this.path = path;
  }

  public String[] getExcludePath() {
    return excludePath;
  }

  public void setExcludePath(String[] excludePath) {
    this.excludePath = ArrayUtils.addAll(excludePath, ENDPOINTS);
  }

  public Integer getMaxToken() {
    return maxToken;
  }

  public void setMaxToken(Integer maxToken) {
    this.maxToken = maxToken;
  }

  public String getFindRolesSql() {
    return findRolesSql;
  }

  public void setFindRolesSql(String findRolesSql) {
    this.findRolesSql = findRolesSql;
  }

  public String getFindPermissionsSql() {
    return findPermissionsSql;
  }

  public void setFindPermissionsSql(String findPermissionsSql) {
    this.findPermissionsSql = findPermissionsSql;
  }

  public String getAuthCenterUrl() {
    return authCenterUrl;
  }

  public void setAuthCenterUrl(String authCenterUrl) {
    this.authCenterUrl = authCenterUrl;
  }

  /**
   * @return the viewResolverPrefix
   */
  public String getViewResolverPrefix() {
    return viewResolverPrefix;
  }

  /**
   * @param viewResolverPrefix the viewResolverPrefix to set
   */
  public void setViewResolverPrefix(String viewResolverPrefix) {
    this.viewResolverPrefix = viewResolverPrefix;
  }

  /**
   * @return the viewResolverSuffix
   */
  public String getViewResolverSuffix() {
    return viewResolverSuffix;
  }

  /**
   * @param viewResolverSuffix the viewResolverSuffix to set
   */
  public void setViewResolverSuffix(String viewResolverSuffix) {
    this.viewResolverSuffix = viewResolverSuffix;
  }

}
