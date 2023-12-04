package cn.flood.base.core.constants;

/**
 *
 */
public interface HeaderConstant {

  /**
   * 请求id
   */
  String REQUEST_ID = "requestId";

  /**
   * 平台类型 WEB,APP
   */
  String CLIENT_ID = "client-id";


  String DEFAULT_CLIENT_ID = "all";

  /**
   * header 中租户ID
   */
  String TENANT_ID = "tenant-id";

  /**
   * 租户id参数
   */
  String TENANT_ID_PARAM = "tenantId";


  String DEFAULT_TENANT_ID = "0";


  /**
   * 标准协议指定 请求header头需带上版本号flood-api-version
   */
  String HEADER_VERSION = "version";


  /**
   * 为了兼容旧版本无请求头,默认从1.0版本开始
   */
  String DEFAULT_VERSION = "1.0.0";

  /**
   * 传参token
   */
  String ACCESS_TOKEN = "access-token";
}
