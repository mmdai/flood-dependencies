package cn.flood.constants;

/**
 *
 */
public interface HeaderConstant {
    // 请求id
    String REQUEST_ID = "requestId";


    /**
     * 标准协议指定 请求header头需带上版本号flood-api-version
     */
    String HEADER_VERSION = "version";


    /**
     * 为了兼容旧版本无请求头,默认从1.0版本开始
     */
    String DEFAULT_VERSION = "v1.0.0";
}
