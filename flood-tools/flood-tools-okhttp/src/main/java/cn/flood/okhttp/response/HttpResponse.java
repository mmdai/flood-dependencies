/**  
* <p>Title: HttpResponse.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月25日  
* @version 1.0  
*/  
package cn.flood.okhttp.response;

import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import cn.flood.io.IOUtils;
import cn.flood.lang.Assert;
import cn.flood.okhttp.exception.HttpClientException;
import cn.flood.okhttp.exception.HttpStatusCodeException;
import cn.flood.okhttp.response.handle.DataHandler;
import cn.flood.okhttp.response.handle.FileDataHandler;
import cn.flood.okhttp.response.handle.StringDataHandler;
/**  
* <p>Title: HttpResponse</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月25日  
*/
public class HttpResponse implements Serializable {
	
	private transient final Response rawResponse;

    /**
     * 请求是否成功
     */
    private boolean isSuccess;

    /**
     * 请求失败时错误消息
     */
    private String errorMessage;

    /**
     * HTTP Status Code
     */
    private int httpCode;

    private byte[] byteData;//cache data

    public HttpResponse(Response rawResponse) {
        this.rawResponse = rawResponse;
        try {
            this.byteData = this.rawResponse.body().bytes();
        } catch (IOException e) {
            throw new HttpClientException(e);
        } finally {
            IOUtils.closeQuietly(this.rawResponse);
        }
    }

    /**
     * 判断请求是否成功
     *
     * @return 成功则[@code true}否则为{@code false}
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    /**
     * 请求失败时错误消息
     *
     * @return 失败消息
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * 请求失败时的HTTP Status Code
     *
     * @return Http错误码
     */
    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public Response getRawResponse() {
        if (rawResponse == null) {
            return rawResponse;
        }
        return rawResponse.newBuilder().body(ResponseBody.create(rawResponse.body().contentType(), byteData)).build();
    }

    /**
     * 将响应结果转为字符串
     *
     * @return 响应结果字符串
     * @throws HttpClientException 如果服务器返回非200则抛出此异常
     */
    public String asString() {
        return this.custom(StringDataHandler.create());
    }


    /**
     * 将响应结果转为字节数组
     *
     * @return 字节数组
     * @throws HttpClientException 如果服务器返回非200则抛出此异常
     */
    public byte[] asByteData() {
        this.assertSuccess();
        return byteData;
    }

    /**
     * 将响应结果输出到文件中
     *
     * @param saveFile 目标保存文件,非空
     */
    public void asFile(File saveFile) {
        Assert.notNull(saveFile, "SaveFile may noy be null.");
        this.custom(new FileDataHandler(saveFile.getParent(), saveFile.getName()));
    }

    /**
     * 将响应结果输出到输出流,并不会主动关闭输出流{@code out}
     *
     * @param out 输出流,非空
     * @throws IOException 
     */
    public void asStream(OutputStream out) throws IOException {
        Assert.notNull(out, "OutputStream is null.");
        this.assertSuccess();
        try {
            IOUtils.copy(this.rawResponse.body().byteStream(), out);
        } finally {
            IOUtils.closeQuietly(this.rawResponse);
        }
    }

    /**
     * 响应结果转换
     *
     * @param dataHandler 数据转换接口
     * @param <T>         期望转换的类型
     * @return 抓好结果
     */
    public <T> T custom(DataHandler<T> dataHandler) {
        return custom(dataHandler, true);
    }

    /**
     * 响应结果转换
     *
     * @param dataHandler   数据转换接口
     * @param <T>           期望转换的类型
     * @param checkHttpCode 是否检查状态码
     * @return 抓好结果
     */
    public <T> T custom(DataHandler<T> dataHandler, boolean checkHttpCode) {
        if (checkHttpCode) {
            this.assertSuccess();
        }
        try {
            return dataHandler.handle(getRawResponse());
        } catch (IOException e) {
            throw new HttpClientException(e);
        } finally {
            IOUtils.closeQuietly(this.rawResponse);
        }
    }

    private void assertSuccess() {
        if (!this.isSuccess) {
            throw new HttpStatusCodeException(this.rawResponse.request().url().toString(),
                    this.httpCode, this.errorMessage);
        }
    }

}
