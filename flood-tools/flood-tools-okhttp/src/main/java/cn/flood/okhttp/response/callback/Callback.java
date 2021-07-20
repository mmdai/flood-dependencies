/**  
* <p>Title: Callback.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月25日  
* @version 1.0  
*/  
package cn.flood.okhttp.response.callback;

import cn.flood.okhttp.request.AbsHttpRequest;
import cn.flood.okhttp.response.handle.DataHandler;
import okhttp3.Call;
import okhttp3.Response;

/**  
* <p>Title: Callback</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月25日  
*/
public interface Callback<T> {
	
	 /**
     * 在请求前调用,在这里可以设置一些参数
     *
     * @param httpRequest 请求对象
     * @return 返回{@code false}则取消此次请求
     */
    boolean onBefore(AbsHttpRequest httpRequest);

    /**
     * 上传请求调用
     *
     * @param currentSize 当前上传的大小
     * @param totalSize   总的大小
     * @param progress    完成进度
     */
    void postProgress(final long currentSize, final long totalSize, final float progress);

    /**
     * 请求失败调用
     *
     * @param call      The real call
     * @param exception Exception
     */
    void onError(Call call, Exception exception);

    /**
     * 请求完成调用
     *
     * @param response 原始的Response,方便调用者自行处理
     */
    void onComplete(Response response);

    /**
     * 获取数据处理器,用于解析转换响应结果
     *
     * @return {@linkplain DataHandler}
     */
    DataHandler<T> getDataHandler();

    /**
     * 根据数据处理器得到处理结果,调用者直接使用处理后的数据
     * 如果{@linkplain #getDataHandler()}返回{@code null}则{@code data}也为{@code null}
     *
     * @param data 响应经过处理的数据
     * @see #getDataHandler()
     */
    void onSuccess(T data);

    /**
     * 空实现
     */
    Callback EMPTY_CALLBACK = new CallbackAdaptor<>();

}
