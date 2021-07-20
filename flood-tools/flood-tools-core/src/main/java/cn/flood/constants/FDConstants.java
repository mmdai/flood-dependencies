package cn.flood.constants;


/**
 * @author mmdai
 * @date 2019/11/8 21:34
 */
public class FDConstants {

    public final static Long LONG_NEGATIVE_1 = -1L;
    public final static Long LONG_0 = 0L;
    public final static Long LONG_1 = 1L;
    public final static Long LONG_30 = 30L;
    public final static Long LONG_1800 = 1800L;
    public final static String STRING_1 = "1";
    public final static String STRING_0 = "0";
    public final static String HYPHEN = "-";
    public final static String EMPTY_STRING = "";
    public final static String NULL_STRING = "null";
    public final static String UNKNOWN = "unknown";
    public final static Integer INT_NEGATIVE_1 = -1;
    public final static Integer INT_0 = 0;
    public final static Integer INT_1 = 1;
    public final static Integer INT_2 = 2;
    public final static Integer INT_3 = 3;
    public final static Integer INT_4 = 4;
    public final static Integer INT_5 = 5;
    public final static Integer INT_6 = 6;
    public final static Integer INT_7 = 7;
    public final static Integer INT_8 = 8;
    public final static Integer INT_9 = 9;
    public final static Integer INT_24 = 24;
    public final static Integer INT_32 = 32;
    public final static Integer INT_100 = 100;
    public final static Integer INT_1024 = 1024;

    public final static String THREAD_ID = "threadId";

    public final static String REQUEST_ID = "requestId";

    /**
     * 管理员登陆信息 token：Admin
     */
    public static final String ADMIN_LOGIN_INFO = "admin:login:info:";
    /**
     * 用户登陆信息 token: User
     */
    public static final String USER_LOGIN_INFO = "user:login:info:";
    /**
     * 升序
     */
    public final static String ASC = "ASC";
    /**
     * 降序
     **/
    public final static String DESC = "DESC";

    /**
     * 编码
     */
    public interface CHARSET {
        String UTF_8 = "utf-8";
    }

    /**
     * 路径分隔符
     */
    public static final String SLASH = "/";

    /**
     * 系统变量名称
     */
    public static final String SYSTEM_PROPERTY_OS_NAME = "os.name";


    /**
     * session使用
     */
    public interface USER_SESSION {

        String SESSION_NAMESPACE = "user_token";

        /**
         * redis中存储的key前缀-admin
         */
        String SESSION_ADMIN_PREFIX = SESSION_NAMESPACE + ":admin:";

        /**
         * redis中存储的key前缀-app
         */
        String SESSION_APP_PREFIX = SESSION_NAMESPACE + ":app:";

        /**
         * redis中存储的key前缀-api
         */
        String SESSION_API_PREFIX = SESSION_NAMESPACE + ":api:";

        /**
         * 权限初始化加载
         */
        String ROLE_AUTHORITY = SESSION_ADMIN_PREFIX + "authority:";

    }

    /**
     * 图片验证码
     */
    public static final String REGISTER_IMG_CODE = "imgcode:";
    /**
     * 手机验证码
     */
    public static final String REGISTER_PHONE_CODE = "phonecode:";
    /**
     * header使用的token规则
     */
    public static final String HEADER_ACCESS_TOKEN = "access-token";


    public interface FILE {
        String PARAM_FILE = "file";
        /**
         * 单个上传文档的最大大小，默认设置为10M。
         */
        int UPLOAD_FILE_MAX_SIZE = 26214400;

        int FILE_MAX_SIZE = 20971520;

        int FILE_SIGN_MAX_SIZE = 26214400;
        /**
         * 版式类型pdf
         */
        int LAYOUT_TYPE_PDF = 1;
        /**
         * 版式类型pdf
         */
        String LAYOUT_PDF_EXTENSION = FILE.SUFFIX_EXTENSION_PDF;
        /**
         * 版式类型ofd
         */
        int LAYOUT_TYPE_OFD = 2;
        /**
         * 版式类型PNG
         */
        int LAYOUT_TYPE_PNG = 3;
        /**
         * 版式类型JPG
         */
        int LAYOUT_TYPE_JPG = 4;
        /**
         * json类型
         */
        String MIME_TYPE_JSON = "application/json;charset=UTF-8";
        String MIME_TYPE_PDF = "application/pdf";
        String MIME_TYPE_OFD = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        String MIME_TYPE_PNG = "image/png";
        String MIME_TYPE_JPG_JPEG = "image/jpeg";
        /**
         * mime type：doc
         */
        String MIME_TYPE_DOC = "application/msword";
        /**
         * mime type：docx
         */
        String MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        /**
         * mime type：wps
         */
        String MIME_TYPE_WPS = "application/vnd.ms-works";
        /**
         * mime type：xls
         */
        String MIME_TYPE_XLS = "application/vnd.ms-excel";
        /**
         * mime type：xlsx
         */
        String MIME_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        /**
         * mime type：wps-xls(et),wps-xlsx(ett)
         */
        String MIME_TYPE_WPS_XLS = "application/octet-stream";
        /**
         * 普通文本文件
         */
        String MIME_TYPE_TEXT_PLAIN = "text/plain";
        /**
         * PDF文件
         */
        String MIME_TYPE_APPLICATION_PDF = "application/pdf";
        /**
         * OFD文件,也有可能是普通文本文件
         */
        String MIME_TYPE_APPLICATION_OCTET_STREAM = "application/octet-stream";
        /**
         * OFD文件，也有可能类型是这种格式
         */
        String MIME_TYPE_APPLICATION_OFD = "application/ofd";
        /**
         * OFD文件(win7/8)
         */
        String MIME_TYPE_APPLICATION_VND_OFD = "application/vnd.ofd";
        /**
         * OFD文件(ie10)
         */
        String MIME_TYPE_APPLICATION_IE10_OFD = "application/x-zip-compressed";
        String PATH_JOINT = "_";
        int PATH_RANDOM_NUMBER = 10000;
        /**
         * pdf文件类型
         */
        String SUFFIX_EXTENSION_PDF = ".pdf";
        /**
         * ODF文件类型
         */
        String SUFFIX_EXTENSION_OFD = ".ofd";
        /**
         * jpg文件类型
         */
        String SUFFIX_EXTENSION_JPG = ".jpg";
        /**
         * jpeg文件类型
         */
        String SUFFIX_EXTENSION_JPEP = ".jpeg";
        /**
         * svg文件类型
         */
        String SUFFIX_EXTENSION_SVG = ".svg";
        /**
         * png文件类型
         */
        String SUFFIX_EXTENSION_PNG = ".png";
        /**
         * doc文件类型
         */
        String SUFFIX_EXTENSION_DOC = ".doc";
        /**
         * docx文件类型
         */
        String SUFFIX_EXTENSION_DOCX = ".docx";
        /**
         * wps文件类型
         */
        String SUFFIX_EXTENSION_WPS = ".wps";
        /**
         * xls文件类型
         */
        String SUFFIX_EXTENSION_XLS = ".xls";
        /**
         * xlsx文件类型
         */
        String SUFFIX_EXTENSION_XLSX = ".xlsx";
        /**
         * xlsx-wps-et文件类型
         */
        String SUFFIX_EXTENSION_WPS_XLS_ET = ".et";
        /**
         * xlsx-wps-ett文件类型
         */
        String SUFFIX_EXTENSION_WPS_XLS_ETT = ".ett";
        /**
         * TXT文件类型
         */
        String SUFFIX_EXTENSION_TXT = ".txt";
        /**
         * html文件类型
         */
        String SUFFIX_EXTENSION_HTML = ".html";
        /**
         * ppt文件类型
         */
        String SUFFIX_EXTENSION_PPT = ".ppt";
        /**
         * pptx文件类型
         */
        String SUFFIX_EXTENSION_PPTX = ".pptx";
    }

    /**
     * 时区
     */
    public interface TIME_ZONE {
        /**
         * 东8区
         */
        String SHANGHAI = "GMT+8";
    }

    /**
     * 标点符号定义区
     *
     * @author zhaojy
     * @date 2019-03-22
     */
    public interface PUNCTUATION {
        /**
         * 逗号：英文版
         */
        String SYMBOL_COMMA_EN = ",";

        /**
         * 句号：英文
         */
        String SYMBOL_PERIOD_EN = ".";
    }

    /**
     * 应用版本号
     */
    public static final String MATE_APP_VERSION = "3.8.8";

    /**
     * Spring 应用名 prop key
     */
    public static final String SPRING_APP_NAME_KEY = "spring.application.name";


    /**
     * 默认为空消息
     */
    public static final String DEFAULT_NULL_MESSAGE = "承载数据为空";
    /**
     * 默认成功消息
     */
    public static final String DEFAULT_SUCCESS_MESSAGE = "处理成功";
    /**
     * 默认失败消息
     */
    public static final String DEFAULT_FAIL_MESSAGE = "处理失败";
    /**
     * 树的根节点值
     */
    public static final Long TREE_ROOT = -1L;
    /**
     * 允许的文件类型，可根据需求添加
     */
    public static final String[] VALID_FILE_TYPE = {"xlsx", "zip"};

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 微服务之间传递的唯一标识
     */
    public static final String MATE_TRACE_ID = "flood-trace-id";

    /**
     * 日志链路追踪id日志标志
     */
    public static final String LOG_TRACE_ID = "traceId";

    /**
     * Java默认临时目录
     */
    public static final String JAVA_TEMP_DIR = "java.io.tmpdir";

    /**
     * 版本
     */
    public static final String VERSION = "version";

    /**
     * 默认版本号
     */
    public static final String DEFAULT_VERSION = "v1";

    /**
     * 服务资源
     */
    public static final String MATE_SERVICE_RESOURCE = "flood-service-resource";

    /**
     * API资源
     */
    public static final String MATE_API_RESOURCE = "flood-api-resource";


    /**
     * json类型报文，UTF-8字符集
     */
    public static final String JSON_UTF8 = "application/json;charset=UTF-8";


    public static final String CONFIG_DATA_ID_DYNAMIC_ROUTES = "flood-dynamic-routes.yaml";
    public static final String CONFIG_GROUP = "DEFAULT_GROUP";
    public static final long CONFIG_TIMEOUT_MS = 5000;

}
