package cn.flood.base.core.constants;


/**
 * @author mmdai
 * @date 2019/11/8 21:34
 */
public interface FDConstant {

    Long LONG_NEGATIVE_1 = -1L;
    Long LONG_0 = 0L;
    Long LONG_1 = 1L;
    Long LONG_30 = 30L;
    Long LONG_1800 = 1800L;
    String STRING_1 = "1";
    String STRING_0 = "0";
    String HYPHEN = "-";
    String EMPTY_STRING = "";
    String NULL_STRING = "null";
    String UNKNOWN = "unknown";
    Integer INT_NEGATIVE_1 = -1;
    Integer INT_0 = 0;
    Integer INT_1 = 1;
    Integer INT_2 = 2;
    Integer INT_3 = 3;
    Integer INT_4 = 4;
    Integer INT_5 = 5;
    Integer INT_6 = 6;
    Integer INT_7 = 7;
    Integer INT_8 = 8;
    Integer INT_9 = 9;
    Integer INT_24 = 24;
    Integer INT_32 = 32;
    Integer INT_100 = 100;
    Integer INT_1024 = 1024;

    String THREAD_ID = "threadId";

    String REQUEST_ID = "requestId";

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
    String ASC = "ASC";
    /**
     * 降序
     **/
    String DESC = "DESC";

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

}
