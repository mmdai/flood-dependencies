package cn.flood.constants;

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
    String USER_TYPE = "user_type";


    String DEFAULT_USER_TYPE = "web";


    /**
     * 标准协议指定 请求header头需带上版本号flood-api-version
     */
    String HEADER_VERSION = "version";


    /**
     * 为了兼容旧版本无请求头,默认从1.0版本开始
     */
    String DEFAULT_VERSION = "1.0.0";
}
