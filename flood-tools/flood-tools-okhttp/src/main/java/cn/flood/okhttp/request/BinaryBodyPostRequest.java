/**  
* <p>Title: BinaryBodyPostRequest.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月25日  
* @version 1.0  
*/  
package cn.flood.okhttp.request;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.*;

import cn.flood.http.ContentType;
import cn.flood.io.IOUtils;
import cn.flood.lang.Assert;
import cn.flood.okhttp.utils.Utils;
/**  
* <p>Title: BinaryBodyPostRequest</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月25日  
*/
public class BinaryBodyPostRequest extends BaseBodyHttpRequest<BinaryBodyPostRequest> {

    /**
     * 二进制流内容
     */
    private byte[] content;
    private MediaType mediaType;

    /**
     * 默认构造器
     *
     * @param url 请求地址
     */
    public BinaryBodyPostRequest(String url) {
        super(url);
    }


    /**
     * 设置二进制流
     *
     * @param inputStream 二进制流
     * @return {@link BinaryBodyPostRequest}
     * @see MediaType
     */
    public BinaryBodyPostRequest stream(InputStream inputStream) {
        Assert.notNull(inputStream, "In must not be null.");
        Assert.notNull(mediaType, "MediaType must not be null.");
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (IOUtils.copy(inputStream, outputStream) == -1) {
                throw new IOException("Copy failed");
            }
            this.content = outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Reading stream failed->", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return this;
    }

    /**
     * 设置文件，转为文件流
     *
     * @param file 文件对象
     * @return {@link BinaryBodyPostRequest}
     */
    public BinaryBodyPostRequest file(File file) {
        Assert.notNull(file, "File must not be null.");
        String filename = file.getName();
        MediaType mediaType = Utils.guessMediaType(filename);
        try {
            this.stream(new FileInputStream(file));
            this.mediaType = mediaType;
            return this;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置请求内容类型
     *
     * @param contentType 请求内容类型
     * @return {@link BinaryBodyPostRequest}
     */
    public BinaryBodyPostRequest contentType(String contentType) {
        Assert.hasLength(contentType, "ContentType must not be null.");
        this.mediaType = MediaType.parse(contentType);
        return this;
    }

    /**
     * 设置请求内容类型
     *
     * @param contentType 请求内容类型
     * @return {@link BinaryBodyPostRequest}
     */
    public BinaryBodyPostRequest contentType(ContentType contentType) {
        Assert.notNull(contentType, "ContentType must not be null.");
        this.mediaType = MediaType.parse(contentType.toString());
        return this;
    }

    /**
     * 获取{@linkplain RequestBody}对象
     */
    @Override
    protected RequestBody generateRequestBody() {
        return RequestBody.create(this.mediaType, this.content);
    }


}
