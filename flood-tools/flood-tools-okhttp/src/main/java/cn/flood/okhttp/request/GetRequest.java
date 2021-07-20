/**  
* <p>Title: GetRequest.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月25日  
* @version 1.0  
*/  
package cn.flood.okhttp.request;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
/**  
* <p>Title: GetRequest</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月25日  
*/
public class GetRequest extends AbsHttpRequest<GetRequest> {

    /**
     * 构造GET请求对象
     *
     * @param url 请求的URL地址
     */
    public GetRequest(String url) {
        super(url);
    }

    /**
     * 获取{@linkplain RequestBody}对象
     */
    @Override
    protected RequestBody generateRequestBody() {
        return null;
    }

    /**
     * 根据不同的请求方式，将RequestBody转换成Request对象
     *
     * @param requestBody 请求体
     * @return {@link Request}
     * @see RequestBody
     */
    @Override
    protected Request generateRequest(RequestBody requestBody) {
        Request.Builder builder = new Request.Builder();
        HttpUrl httpUrl = HttpUrl.parse(this.buildUrl());
        builder.url(httpUrl);
        this.collectHeader(builder, httpUrl);
        return builder.build();
    }


}
