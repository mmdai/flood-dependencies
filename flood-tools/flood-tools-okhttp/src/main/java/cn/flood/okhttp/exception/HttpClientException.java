/**  
* <p>Title: HttpClientException.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月22日  
* @version 1.0  
*/  
package cn.flood.okhttp.exception;

/**  
* <p>Title: HttpClientException</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月22日  
*/
public class HttpClientException extends RuntimeException {

	/** serialVersionUID*/  
	private static final long serialVersionUID = -7765737189890624266L;
	
	public HttpClientException() {
		super();
    }
	
	public HttpClientException(String message) {
        super(message);
    }
	
	public HttpClientException(Throwable cause) {
        super(cause);
    }

}
