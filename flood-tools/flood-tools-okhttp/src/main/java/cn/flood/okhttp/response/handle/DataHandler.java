/**  
* <p>Title: DataHandler.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月25日  
* @version 1.0  
*/  
package cn.flood.okhttp.response.handle;

import java.io.IOException;

/**  
* <p>Title: DataHandler</p>  
* <p>Description: 数据处理定义接口,将得到的响应结果转为所需的数据</p>  
* @author mmdai  
* @date 2019年7月25日  
*/
public interface DataHandler<T> {
	
	/**
     * 得到相应结果后,将相应数据转为需要的数据格式
     *
     * @param response 需要转换的对象
     * @return 转换结果
     * @throws IOException 出现异常
     */
    T handle(final okhttp3.Response response) throws IOException;

}
