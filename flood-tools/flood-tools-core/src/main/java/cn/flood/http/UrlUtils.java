/**  
* <p>Title: UrlUtils.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月16日  
* @version 1.0  
*/  
package cn.flood.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**  
* <p>Title: UrlUtils</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2018年12月16日  
*/
public class UrlUtils {
	
	private static Logger log =  LoggerFactory.getLogger(UrlUtils.class);
	/**
	 * 
	 * <p>Title: getURLDecoderString</p>  
	 * <p>Description: url 解码</p>  
	 * @param str
	 * @param enCode
	 * @return
	 */
	public static String getURLDecoderString(String str, String enCode) {
		String result = "";
		if (null == str) {
			return "";
		}
		try {
			result = java.net.URLDecoder.decode(str, enCode);
		} catch (UnsupportedEncodingException e) {
			log.error("URL解码失败 ex={}", e.getMessage(), e);
		}
		return result;
	}
	/**
	 * 
	 * <p>Title: getURLEncoderString</p>  
	 * <p>Description: url 编码</p>  
	 * @param str
	 * @param enCode
	 * @return
	 */
	public static String getURLEncoderString(String str, String enCode) {
		String result = "";
		if (null == str) {
			return "";
		}
		try {
			result = java.net.URLEncoder.encode(str, enCode);
		} catch (UnsupportedEncodingException e) {
			log.error("URL转码失败 ex={}", e.getMessage(), e);
		}
		return result;
	}

}
