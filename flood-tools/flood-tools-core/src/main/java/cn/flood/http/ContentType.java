/**  
* <p>Title: ContentType.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月19日  
* @version 1.0  
*/  
package cn.flood.http;

import cn.flood.lang.StringUtils;
import org.springframework.util.ObjectUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**  
* <p>Title: ContentType</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月19日  
*/
public class ContentType {
	
	// constants、
    public static final ContentType ALL = create("*/*", (Charset) null);
    public static final ContentType TEXT_XML = create("text/xml", StandardCharsets.UTF_8);
    public static final ContentType TEXT_PLAIN = create("text/plain", StandardCharsets.UTF_8);
    public static final ContentType TEXT_HTML = create("text/html", StandardCharsets.UTF_8);
    public static final ContentType MULTIPART_FORM_DATA = create("multipart/form-data", StandardCharsets.UTF_8);
    public static final ContentType APPLICATION_XML = create("application/xml", StandardCharsets.UTF_8);
    public static final ContentType APPLICATION_OCTET_STREAM = create("application/octet-stream", (Charset) null);
    public static final ContentType APPLICATION_FORM_URLENCODED = create("application/x-www-form-urlencoded", StandardCharsets.UTF_8);
    public static final ContentType APPLICATION_JSON = create("application/json", StandardCharsets.UTF_8);

    //imgage
    public static final ContentType IMAGE_PNG = create("image/png", (Charset) null);
    public static final ContentType IMAGE_JPEG = create("image/jpeg", (Charset) null);
    public static final ContentType IMAGE_JPG = create("image/jpeg", (Charset) null);
    public static final ContentType IMAGE_GIF = create("image/gif", (Charset) null);
    public static final ContentType IMAGE_BMP = create("image/bmp", (Charset) null);

    //zip
    public static final ContentType APPLICATION_ZIP = create("application/zip", (Charset) null);
    public static final ContentType APPLICATION_GZ = create("application/x-gzip", (Charset) null);

    //pdf
    public static final ContentType APPLICATION_PDF = create("application/pdf", (Charset) null);

    //ms
    public static final ContentType APPLICATION_DOC = create("application/msword", (Charset) null);
    public static final ContentType APPLICATION_XLS = create("application/vnd.ms-excel", (Charset) null);
    public static final ContentType APPLICATION_PPT = create("application/vnd.ms-powerpoint", (Charset) null);

    //apk or ios
    public static final ContentType APPLICATION_APK = create("application/vnd.android.package-archive", (Charset) null);
    public static final ContentType APPLICATION_IPA = create("application/iphone", (Charset) null);

    // defaults
    public static final ContentType DEFAULT_TEXT = TEXT_PLAIN;
    public static final ContentType DEFAULT_BINARY = APPLICATION_OCTET_STREAM;

    //support file extension
    private static final Map<String, ContentType> SUPPORT_FILE_EXTS;

    static {
        Map<String, ContentType> _SUPPORT_FILE_EXTS = new ConcurrentHashMap<>();
        _SUPPORT_FILE_EXTS.put("xml", APPLICATION_XML);
        _SUPPORT_FILE_EXTS.put("json", APPLICATION_JSON);
        _SUPPORT_FILE_EXTS.put("doc", APPLICATION_DOC);
        _SUPPORT_FILE_EXTS.put("docx", APPLICATION_DOC);
        _SUPPORT_FILE_EXTS.put("xls", APPLICATION_XLS);
        _SUPPORT_FILE_EXTS.put("xlsx", APPLICATION_XLS);
        _SUPPORT_FILE_EXTS.put("ppt", APPLICATION_PPT);
        _SUPPORT_FILE_EXTS.put("pptx", APPLICATION_PPT);
        _SUPPORT_FILE_EXTS.put("pdf", APPLICATION_PDF);
        _SUPPORT_FILE_EXTS.put("zip", APPLICATION_ZIP);
        _SUPPORT_FILE_EXTS.put("gzip", APPLICATION_GZ);
        _SUPPORT_FILE_EXTS.put("png", IMAGE_PNG);
        _SUPPORT_FILE_EXTS.put("jpeg", IMAGE_JPEG);
        _SUPPORT_FILE_EXTS.put("jpg", IMAGE_JPG);
        _SUPPORT_FILE_EXTS.put("gif", IMAGE_GIF);
        _SUPPORT_FILE_EXTS.put("html", TEXT_HTML);
        _SUPPORT_FILE_EXTS.put("txt", TEXT_PLAIN);
        _SUPPORT_FILE_EXTS.put("apk", APPLICATION_APK);
        _SUPPORT_FILE_EXTS.put("ipa", APPLICATION_IPA);

        SUPPORT_FILE_EXTS = _SUPPORT_FILE_EXTS;
    }

    private String mimeType;

    private Charset charset;


    ContentType(String mimeType, Charset charset) {
        this.mimeType = mimeType;
        this.charset = charset;
    }

    /**
     * 获取MIME类型
     *
     * @return MIME类型
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * 获取字符集编码
     *
     * @return 字符集编码
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * 根据MIME类型创建
     *
     * @param mimeType MIME类型
     * @param charset  字符集编码
     * @return {@link ContentType}
     */
    public static ContentType create(final String mimeType, final Charset charset) {
        return ObjectUtils.isEmpty(mimeType) ? ContentType.DEFAULT_BINARY :
                new ContentType(mimeType.toLowerCase(Locale.CHINESE), charset);
    }

    /**
     * 根据MIME类型创建
     *
     * @param mimeType MIME类型
     * @param charset  字符集编码
     * @return {@link ContentType}
     */
    public static ContentType create(final String mimeType, final String charset) {
        return create(mimeType, ObjectUtils.isEmpty(charset) ? null : Charset.forName(charset));
    }

    /**
     * 根据MIME类型创建
     *
     * @param mimeType MIME类型
     * @return {@link ContentType}
     */
    public static ContentType create(final String mimeType) {
        return create(mimeType, (Charset) null);
    }

    /**
     * 通过文件后缀名转为MIME
     *
     * @param fileExt 文件后缀名
     * @return {@link ContentType}
     */
    public static ContentType parseByFileExt(String fileExt) {
        if (ObjectUtils.isEmpty(fileExt)){
            return DEFAULT_BINARY;
        }
        fileExt = fileExt.toLowerCase();
        ContentType contentType = SUPPORT_FILE_EXTS.get(fileExt);
        return (contentType == null) ? DEFAULT_BINARY : contentType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(64);
        sb.append(mimeType);
        if (this.charset != null) {
            sb.append("; charset=").append(this.charset.name());
        }
        return sb.toString();
    }

}
