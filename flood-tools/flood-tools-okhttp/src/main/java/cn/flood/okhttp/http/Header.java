/**  
* <p>Title: Header.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月23日  
* @version 1.0  
*/  
package cn.flood.okhttp.http;

/**  
* <p>Title: Header</p>  
* <p>Description: HTTP header对象</p>  
* @author mmdai  
* @date 2019年7月23日  
*/
public interface Header {
	
	/**
     * Get the name of the Header.
     *
     * @return the name of the Header,  never {@code null}
     */
    String getName();

    /**
     * Get the value of the Header.
     *
     * @return the value of the Header,  may be {@code null}
     */
    String getValue();

    //region==============定义常用的标准header name==============
    /**
     * The header Accept
     */
    String ACCEPT = "Accept";
    /**
     * The header Accept-Charset
     */
    String ACCEPT_CHARSET = "Accept-Charset";
    /**
     * The header Accept-Encoding
     */
    String ACCEPT_ENCODING = "Accept-Encoding";
    /**
     * The header Accept-Language
     */
    String ACCEPT_LANGUAGE = "Accept-Language";
    /**
     * The header Accept-Ranges
     */
    String ACCEPT_RANGES = "Accept-Ranges";
    /**
     * The header Age
     */
    String AGE = "Age";
    /**
     * The header Allow
     */
    String ALLOW = "Allow";
    /**
     * The header Cache-Control
     */
    String CACHE_CONTROL = "Cache-Control";
    /**
     * The header Connection
     */
    String CONNECTION = "Connection";
    /**
     * The header Content-Encoding
     */
    String CONTENT_ENCODING = "Content-Encoding";
    /**
     * The header Content-Language
     */
    String CONTENT_LANGUAGE = "Content-Language";
    /**
     * The header Content-Length
     */
    String CONTENT_LENGTH = "Content-Length";
    /**
     * The header Content-Location
     */
    String CONTENT_LOCATION = "Content-Location";
    /**
     * The header Content-MD5
     */
    String CONTENT_MD5 = "Content-MD5";
    /**
     * The header Content-Range
     */
    String CONTENT_RANGE = "Content-Range";
    /**
     * The header Content-Type
     */
    String CONTENT_TYPE = "Content-Type";
    /**
     * The header Content-Disposition
     */
    String CONTENT_DISPOSITION = "Content-Disposition";
    /**
     * The header User-Agent
     */
    String USER_AGENT = "User-Agent";
    /**
     * The header Transfer-Encoding
     */
    String TRANSFER_ENCODING = "Transfer-Encoding";
    //endregion==============定义常用的标准header name==============

}
