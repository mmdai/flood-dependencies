/**  
* <p>Title: HttpStatusCodeException.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月22日  
* @version 1.0  
*/  
package cn.flood.okhttp.exception;

/**  
* <p>Title: HttpStatusCodeException</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月22日  
*/
public class HttpStatusCodeException extends HttpClientException {

	/** serialVersionUID*/  
	private static final long serialVersionUID = 4089613731154564667L;
	
	public HttpStatusCodeException(String url, int statusCode, String statusMessage) {
		super("Request url[=" + url + "] failed, status code is " + statusCode + ",status message is " + statusMessage);
        this.url = url;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
	
	private final String url;
	
    private final int statusCode;
    
    private final String statusMessage;

	/**
	 * @return the url
	 * 返回请求地址
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the statusCode 
	 * 请求失败时的HTTP Status Code
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @return the statusMessage
	 * 请求失败时错误消息
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
    
    
	

}
