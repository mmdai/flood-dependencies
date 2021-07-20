/**  
* <p>Title: DefaultCookieJar.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月22日  
* @version 1.0  
*/  
package cn.flood.okhttp.cookie;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**  
* <p>Title: DefaultCookieJar</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月22日  
*/
public class DefaultCookieJar implements CookieJar {

	private CookieStore cookieStore;
	
	public DefaultCookieJar(CookieStore cookieStore) {
        if (cookieStore == null) {
            throw new NullPointerException("CookieStore may not be null.");
        }
        this.cookieStore = cookieStore;
    }

	@Override
	public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
		 cookieStore.add(url, cookies);
	}

	@Override
	public List<Cookie> loadForRequest(HttpUrl url) {
		return cookieStore.get(url);
	}

}
