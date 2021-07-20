/**  
* <p>Title: BaseBodyHttpRequest.java</p>  
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
* <p>Title: BaseBodyHttpRequest</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月25日  
*/
public abstract class BaseBodyHttpRequest<Req extends HttpRequest<Req>> extends AbsHttpRequest<Req> {

    BaseBodyHttpRequest(String url) {
        super(url);
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
        builder.post(requestBody);
        return builder.build();
    }

}
