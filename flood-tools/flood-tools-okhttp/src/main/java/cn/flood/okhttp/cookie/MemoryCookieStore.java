/**  
* <p>Title: MemoryCookieStore.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月22日  
* @version 1.0  
*/  
package cn.flood.okhttp.cookie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.CollectionUtils;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**  
* <p>Title: MemoryCookieStore</p>  
* <p>Description: 内存缓存Cookie</p>  
* @author mmdai  
* @date 2019年7月22日  
*/
public class MemoryCookieStore implements CookieStore {
	
	private final Map<String, List<Cookie>> allCookies = new ConcurrentHashMap<>();

	@Override
	public void add(HttpUrl uri, List<Cookie> cookies) {
		if (uri == null) {
			throw new NullPointerException("Uri must not be null.");
        }
        if (CollectionUtils.isEmpty(cookies)) {
            throw new NullPointerException("Cookies must not be null.");
        }
        List<Cookie> oldCookies = allCookies.get(uri.host());
        List<Cookie> deleteCookies = new ArrayList<>(oldCookies.size());
        for (Cookie cookie : cookies) {
            for (Cookie oldCookie : oldCookies) {
                if (oldCookie.name().equals(cookie.name())) {
                    deleteCookies.add(oldCookie);
                }
            }
        }
        oldCookies.removeAll(deleteCookies);
        oldCookies.addAll(cookies);
	}

	@Override
	public List<Cookie> get(HttpUrl uri) {
		if (uri == null) {
            throw new NullPointerException("Uri must not be null.");
        }
        List<Cookie> cookies = allCookies.get(uri.host());
        if (cookies == null) {
            cookies = new ArrayList<>();
            allCookies.put(uri.host(), cookies);
        }
        return cookies;
	}

	@Override
	public List<Cookie> getCookies() {
		List<Cookie> cookies = new ArrayList<>(20);
        for (String host : allCookies.keySet()) {
            cookies.addAll(allCookies.get(host));
        }
        return cookies;
	}

	@Override
	public boolean remove(HttpUrl uri, Cookie cookie) {
		if (uri == null) {
            throw new NullPointerException("Uri must not be null.");
        }
        if (cookie == null) {
            throw new NullPointerException("Cookie must not be null.");
        }
        return allCookies.remove(uri.host()) != null;
	}

	@Override
	public boolean removeAll() {
		allCookies.clear();
        return true;
	}

}
