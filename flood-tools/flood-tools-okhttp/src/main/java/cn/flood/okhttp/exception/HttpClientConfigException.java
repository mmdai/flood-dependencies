/**  
* <p>Title: HttpClientConfigException.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月22日  
* @version 1.0  
*/  
package cn.flood.okhttp.exception;

/**  
* <p>Title: HttpClientConfigException</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月22日  
*/
public class HttpClientConfigException extends RuntimeException {

	/** serialVersionUID*/  
	private static final long serialVersionUID = -1841476371294845609L;
	
	public HttpClientConfigException(Throwable cause) {
        super(cause);
    }

    public HttpClientConfigException(String message) {
        super(message);
    }

}
