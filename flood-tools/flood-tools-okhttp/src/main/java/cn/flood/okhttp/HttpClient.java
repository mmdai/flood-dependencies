/**  
* <p>Title: HttpClient.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月25日  
* @version 1.0  
*/  
package cn.flood.okhttp;

import cn.flood.lang.Assert;
import cn.flood.okhttp.cookie.CookieStore;
import cn.flood.okhttp.cookie.DefaultCookieJar;
import cn.flood.okhttp.cookie.MemoryCookieStore;
import cn.flood.okhttp.http.DebugLoggingInterceptor;
import cn.flood.okhttp.request.BinaryBodyPostRequest;
import cn.flood.okhttp.request.GetRequest;
import cn.flood.okhttp.request.PostRequest;
import cn.flood.okhttp.request.TextBodyRequest;
import cn.flood.okhttp.utils.SSLContexts;
import okhttp3.ConnectionPool;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**  
* <p>Title: HttpClient</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月25日  
*/
public enum HttpClient {
    
	Instance;

    public static final int DEFAULT_TIMEOUT = 10;//10seconds
    //logger
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    /* OkHttp */
    private OkHttpClient.Builder builder;
    private CookieStore cookieStore;
    private OkHttpClient okHttpClient;

    /* 默认的Header存储 */
    private Map<String, Map<String, String>> defaultHeaders;

    /* default constructor */
    private HttpClient() {
        //default init
        this.cookieStore = new MemoryCookieStore();
        this.builder = new OkHttpClient.Builder()
        		//设置连接池默认空闲20个，闲置时间5分钟
        		.connectionPool(new ConnectionPool(20, 5, TimeUnit.MINUTES))
                //设置cookie自动管理
                .cookieJar(new DefaultCookieJar(this.cookieStore))
                //设置默认主机验证规则
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

        //读取默认配置文件
        Properties ret = null;
		try {
			ret = PropertiesLoaderUtils
			        .loadProperties(new ClassPathResource("flood-okhttp.properties"));
		} catch (IOException e) {
		}
        //连接超时时间
        int timeout = Integer.parseInt(ret.getProperty("connectTimeout"));
        if (timeout <= 0) {
            timeout = DEFAULT_TIMEOUT;
        }
        this.builder.connectTimeout(timeout, TimeUnit.SECONDS);

        //读取超时时间
        timeout = Integer.parseInt(ret.getProperty("readTimeout"));
        if (timeout <= 0) {
            timeout = DEFAULT_TIMEOUT;
        }
        this.builder.readTimeout(timeout, TimeUnit.SECONDS);

        //写入超时时间
        timeout = Integer.parseInt(ret.getProperty("writeTimeout"));
        if (timeout <= 0) {
            timeout = DEFAULT_TIMEOUT;
        }
        this.builder.writeTimeout(timeout, TimeUnit.SECONDS);

        this.defaultHeaders = new ConcurrentHashMap<>(10);

    }


    //region================超时时间设置方法================

    /**
     * 设置连接超时时间
     *
     * @param connectTimeout 超时时间
     * @param timeUnit       超时时间单位
     * @return {@linkplain HttpClient}
     */
    public HttpClient connectTimeout(int connectTimeout, TimeUnit timeUnit) {
        if (connectTimeout <= 0) {
            LOGGER.error(" ===> Connect timeout must not be less than 0.");
            return this;
        }
        Assert.notNull(timeUnit, "TimeUnit may not be null.");
        this.builder.connectTimeout(connectTimeout, timeUnit);
        return this;
    }

    /**
     * 设置连接超时时间
     *
     * @param connectTimeout 超时时间,单位秒
     * @return {@linkplain HttpClient}
     * @see #connectTimeout(int, TimeUnit)
     */
    public HttpClient connectTimeout(int connectTimeout) {
        return connectTimeout(connectTimeout, TimeUnit.SECONDS);
    }

    /**
     * 设置流读取超时时间
     *
     * @param readTimeout 读取超时时间,单位毫秒
     * @param timeUnit    超时时间单位
     * @return {@linkplain HttpClient}
     */
    public HttpClient readTimeout(int readTimeout, TimeUnit timeUnit) {
        if (readTimeout <= 0) {
            LOGGER.error(" ===> Read timeout must not be less than 0.");
            return this;
        }
        Assert.notNull(timeUnit, "TimeUnit may not be null.");
        this.builder.readTimeout(readTimeout, timeUnit);
        return this;
    }

    /**
     * 设置流读取超时时间
     *
     * @param readTimeout 读取超时时间,单位秒
     * @return {@linkplain HttpClient}
     * @see #readTimeout(int, TimeUnit)
     */
    public HttpClient readTimeout(int readTimeout) {
        return readTimeout(readTimeout, TimeUnit.SECONDS);
    }

    /**
     * 设置流的写入超时时间
     *
     * @param writeTimeout 写入超时时间,单位是毫秒
     * @param timeUnit     超时时间单位
     * @return {@linkplain HttpClient}
     */
    public HttpClient writeTimeout(int writeTimeout, TimeUnit timeUnit) {
        if (writeTimeout <= 0) {
            LOGGER.error(" ===> Write timeout must not be less than 0.");
            return this;
        }
        Assert.notNull(timeUnit, "TimeUnit may not be null.");
        this.builder.writeTimeout(writeTimeout, timeUnit);
        return this;
    }

    /**
     * 设置流的写入超时时间
     *
     * @param writeTimeout 写入超时时间,单位是秒
     * @return {@linkplain HttpClient}
     * @see #writeTimeout(int, TimeUnit)
     */
    public HttpClient writeTimeout(int writeTimeout) {
        return writeTimeout(writeTimeout, TimeUnit.SECONDS);
    }
    //endregion


    //region================SSL证书设置方法================

    /**
     * https单向认证
     *
     * @param certificates 含有服务端公钥的证书
     * @return {@link HttpClient}
     */
    public HttpClient customSSL(InputStream... certificates) {
        return customSSL(null, null, certificates);
    }

    /**
     * https单向认证,直接配置证书管理
     *
     * @param trustManager 证书管理器
     * @return {@link HttpClient}
     */
    public HttpClient customSSL(X509TrustManager trustManager) {
        return customSSL(null, null, trustManager);
    }

    /**
     * https双向认证
     *
     * @param pfxStream    客户端证书，支持P12的证书
     * @param pfxPwd       客户端证书密码
     * @param certificates 含有服务端公钥的证书
     * @return {@link HttpClient}
     */
    public HttpClient customSSL(InputStream pfxStream, char[] pfxPwd, InputStream... certificates) {
        SSLContexts.SSLConfig sslConfig = SSLContexts.tryParse(certificates, null, pfxStream, pfxPwd);
        this.builder.sslSocketFactory(sslConfig.getSslSocketFactory(), sslConfig.getX509TrustManager());
        return this;
    }

    /**
     * https双向认证
     *
     * @param pfxStream    客户端证书，支持P12的证书
     * @param pfxPwd       客户端证书密码
     * @param trustManager 证书管理器
     * @return {@link HttpClient}
     */
    public HttpClient customSSL(InputStream pfxStream, char[] pfxPwd, X509TrustManager trustManager) {
        SSLContexts.SSLConfig sslConfig = SSLContexts.tryParse(null, trustManager, pfxStream, pfxPwd);
        this.builder.sslSocketFactory(sslConfig.getSslSocketFactory(), sslConfig.getX509TrustManager());
        return this;
    }
    //endregion


    //region================设置默认请求================

    /**
     * 设置默认的Http Header
     *
     * @param host  主机名
     * @param name  Header名称
     * @param value Header值
     * @return {@linkplain HttpClient}
     */
    public HttpClient setDefaultHeader(String host, String name, String value) {
        Assert.hasLength(host, "Host may not be null or empty.");
        Assert.hasLength(name, "Name may not be null or empty.");
        Assert.notNull(value, "Value may not be null.");
        HttpUrl httpUrl = HttpUrl.parse(host);
        if (httpUrl == null) {
            throw new IllegalArgumentException("Host [" + host + "] is invalid.");
        }
        Map<String, String> headers = defaultHeaders.get(httpUrl.host());
        if (CollectionUtils.isEmpty(headers)) {
            headers = new ConcurrentHashMap<>();
            defaultHeaders.put(httpUrl.host(), headers);
        }
        headers.put(name, value);
        return this;
    }

    /**
     * 获取默认的Http Header列表
     *
     * @param host 主机名
     * @return Header键值对列表
     */
    public Map<String, String> getDefaultHeaders(String host) {
        Assert.hasLength(host, "Host may not be null or empty.");
        HttpUrl httpUrl = HttpUrl.parse(host);
        return getDefaultHeaders(httpUrl);
    }

    /**
     * 获取默认的Http Header列表
     *
     * @param httpUrl 地址信息
     * @return Header键值对列表
     */
    public Map<String, String> getDefaultHeaders(HttpUrl httpUrl) {
        Assert.notNull(httpUrl, "HttpUrl is null or invalid.");
        Map<String, String> headers = defaultHeaders.get(httpUrl.host());
        return headers == null ? Collections.<String, String>emptyMap() : Collections.unmodifiableMap(headers);
    }

    /**
     * 清除默认参数
     *
     * @param host 主机名
     * @return {@linkplain HttpClient}
     */
    public HttpClient clearDefaultHeaders(String host) {
        Assert.hasLength(host, "Host may not be null or empty.");
        HttpUrl httpUrl = HttpUrl.parse(host);
        return clearDefaultHeaders(httpUrl);
    }

    /**
     * 清除默认参数
     *
     * @param httpUrl 地址信息
     * @return {@linkplain HttpClient}
     */
    private HttpClient clearDefaultHeaders(HttpUrl httpUrl) {
        Assert.notNull(httpUrl, "HttpUrl is null or invalid.");
        defaultHeaders.remove(httpUrl.host());
        return this;
    }
    //endregion

//    /**
//     * 获取默认的请求参数列表
//     *
//     * @return 请求参数键值对列表
//     */
//    public Map<String,String> getDefaultParams() {
//        return new ConcurrentHashMap<>(this.defaultParameters);
//    }

    //region================Cookie和拦截器================

    /**
     * 提供自己管理Cookie的能力
     *
     * @param cookieStore 操作Cookie的接口
     * @return {@linkplain HttpClient}
     */
    public HttpClient setCookieStore(CookieStore cookieStore) {
        Assert.notNull(cookieStore, "CookieStore may not be null.");
        this.cookieStore = cookieStore;
        this.builder.cookieJar(new DefaultCookieJar(cookieStore));
        return this;
    }

    /**
     * 返回{@linkplain CookieStore}实现类
     *
     * @return {@link CookieStore}
     */
    public CookieStore getCookieStore() {
        return this.cookieStore;
    }

    /**
     * 添加全局拦截器
     *
     * @param customInterceptor 拦截器接口
     * @return {@linkplain HttpClient}
     */
    public HttpClient addInterceptor(Interceptor customInterceptor) {
        this.builder.addInterceptor(customInterceptor);
        return this;
    }
    //endregion

    //region================Debug调试================

    /**
     * 调试模式，默认打印请求参数和响应结果
     *
     * @return {@linkplain HttpClient}
     */
    public HttpClient debugLog() {
        return debugLog(DebugLoggingInterceptor.Level.BASIC);
    }

    /**
     * 调试模式，自定义设置日志级别
     *
     * @param loggingLevel 日志级别
     * @return {@linkplain HttpClient}
     * @see DebugLoggingInterceptor.Level
     */
    public HttpClient debugLog(DebugLoggingInterceptor.Level loggingLevel) {
        DebugLoggingInterceptor instance = DebugLoggingInterceptor.INSTANCE;
        instance.setLoggingLevel(loggingLevel);
        this.builder.addInterceptor(instance);
        return this;
    }
    //endregion


    //region============构建OkHttpClient===================

    /**
     * 返回{@linkplain OkHttpClient}对象
     *
     * @return {@link OkHttpClient}
     */
    public OkHttpClient getOkHttpClient() {
        if (this.okHttpClient == null) {
            synchronized (Instance) {
                if (this.okHttpClient == null) {
                    this.okHttpClient = builder.build();
                }
            }
        }
        return this.okHttpClient;
    }

    public OkHttpClient.Builder getOkHttpClientBuilder() {
        this.getOkHttpClient();//为了创建默认的OkHttpClient
        return this.builder;
    }
    //endregion


    //region=====================request======================

    /**
     * Get请求
     *
     * @param url 请求地址
     * @return {@link GetRequest}
     */
    public static GetRequest get(String url) {
        return new GetRequest(url);
    }

    /**
     * FORM/POST表单提交
     *
     * @param url 提交地址
     * @return {@link PostRequest}
     */
    public static PostRequest post(String url) {
        return new PostRequest(url);
    }

    /**
     * 向请求体中传入二进制流
     *
     * @param url 请求地址
     * @return {@linkplain BinaryBodyPostRequest}
     */
    public static BinaryBodyPostRequest binaryBody(String url) {
        return new BinaryBodyPostRequest(url);
    }

    /**
     * 文本POST请求体
     *
     * @param url 请求地址
     * @return {@link TextBodyRequest}
     */
    public static TextBodyRequest textBody(String url) {
        return new TextBodyRequest(url);
    }
    //endregion

}