/**  
* <p>Title: StringDataHandler.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月25日  
* @version 1.0  
*/  
package cn.flood.okhttp.response.handle;

import okhttp3.Response;

import java.io.IOException;
import java.nio.charset.Charset;

/**  
* <p>Title: StringDataHandler</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月25日  
*/
public class StringDataHandler implements DataHandler<String> {

    /**
     * 返回字符串数据处理器
     *
     * @return 字符串数据处理器
     */
    public static StringDataHandler create() {
        return Holder.handler;
    }

    /**
     * 单例Holder
     *
     * @author mzlion
     */
    private static class Holder {
        /**
         * 因为{@linkplain StringDataHandler}使用频率非常高，所以这里直接缓存
         */
        private static StringDataHandler handler = new StringDataHandler();
    }

    /**
     * 字符编码
     */
    private Charset charset;

    /**
     * 获取字符编码
     *
     * @return 字符编码
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * 设置字符编码
     *
     * @param charset 字符编码
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * 得到相应结果后,将相应数据转为需要的数据格式
     *
     * @param response 需要转换的对象
     * @return 转换结果
     */
    @Override
    public String handle(Response response) throws IOException {
        if (this.charset != null) {
            return new String(response.body().bytes());
        }
        return response.body().string();
    }

}
