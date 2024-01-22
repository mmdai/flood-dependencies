package cn.flood.base.core.http;

import cn.flood.base.core.Func;
import cn.flood.base.core.exception.CoreException;
import cn.flood.base.core.exception.enums.GlobalErrorCodeEnum;
import com.google.common.base.Stopwatch;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * http客户端工具
 **/
public class HttpClientTool {

  private static final Logger log = LoggerFactory.getLogger(HttpClientTool.class);

  private static HttpClient mHttpClient = null;

  private static HttpClient httpClientLogin = null;

  private static CloseableHttpClient getHttpClient(HttpClientBuilder httpClientBuilder) {
    RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
    ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
    registryBuilder.register("http", plainSF);
    //指定信任密钥存储对象和连接套接字工厂
    try {
      KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
      //信任任何链接
      TrustStrategy anyTrustStrategy = new TrustStrategy() {
        @Override
        public boolean isTrusted(X509Certificate[] x509Certificates, String s)
            throws CertificateException {
          return true;
        }
      };
      SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(trustStore, anyTrustStrategy)
          .build();
      LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext,
          NoopHostnameVerifier.INSTANCE);
      ;
      registryBuilder.register("https", sslSF);
    } catch (KeyStoreException e) {
      throw new RuntimeException(e);
    } catch (KeyManagementException e) {
      throw new RuntimeException(e);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }

    Registry<ConnectionSocketFactory> registry = registryBuilder.build();
    //设置连接管理器
    PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
        registry);
    connManager.setMaxTotal(Constants.MAXTOTAL);//维护的httpclientConnection总数
    //每一个route的最大连接数，route可以理解为一个主机，如http://www.roadjava.com/2.html
    //和http://www.roadjava.com/1.html是一个主机
    connManager.setDefaultMaxPerRoute(Constants.DEFAULTMAXPERROUTE);
    /*三、为HttpClientBuilder设置从连接池获取连接的超时时间、连接超时时间、获取数据响应超时时间
     */
    RequestConfig requestConfig = RequestConfig.custom().
        setConnectionRequestTimeout(Constants.CONMANTIMEOUT).
        setConnectTimeout(Constants.CONTIMEOUT).
        setSocketTimeout(Constants.SOTIMEOUT).build();
    //构建客户端
    return httpClientBuilder.setConnectionManager(connManager)
        .setDefaultRequestConfig(requestConfig).build();
  }

  private synchronized static HttpClient getHttpClient() {
    if (mHttpClient == null) {
      mHttpClient = getHttpClient(HttpClientBuilder.create());
    }
    return mHttpClient;
  }

  private synchronized static HttpClient getHttpClient(String username, String password) {
    if (httpClientLogin == null) {
      httpClientLogin = getHttpClientWithBasicAuth(username, password);
    }
    return httpClientLogin;
  }

  private static HttpClientBuilder credential(String username, String password) {
    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
    CredentialsProvider provider = new BasicCredentialsProvider();
    AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
    provider.setCredentials(scope, credentials);
    httpClientBuilder.setDefaultCredentialsProvider(provider);
    return httpClientBuilder;
  }

  /**
   * 获取支持basic Auth认证的HttpClient
   *
   * @param username
   * @param password
   * @return
   */
  private static CloseableHttpClient getHttpClientWithBasicAuth(String username, String password) {
    return getHttpClient(credential(username, password));
  }

  /**
   * 设置头信息，e.g. content-type 等
   * @param req
   * @param headers
   */
  private static void setHeaders(HttpRequestBase req, Map<String, String> headers) {
    if (CollectionUtils.isEmpty(headers)) {
      return;
    }
    for (Map.Entry<String, String> header : headers.entrySet()) {
      req.setHeader(header.getKey(), header.getValue());
    }
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * 执行http get Unicode请求
   *
   * @param url
   * @param map
   * @return
   * @throws CoreException
   */
  public static String getMethod(String url, Map<String, Object> map) throws CoreException {
    return getMethod(url, map, null, null, null);
  }

  /**
   * 执行http get Unicode请求
   *
   * @param url
   * @param map
   * @param headers
   * @return
   * @throws CoreException
   */
  public static String getMethod(String url, Map<String, Object> map, Map<String, String> headers) throws CoreException {
    return getMethod(url, map, headers, null, null);
  }
  /**
   * 执行http get Unicode请求
   *
   * @param url      请求地址
   * @param map      请求参数
   * @param username 用户名
   * @param password 密码
   * @return
   * @throws CoreException
   */
  public static String getMethod(String url, Map<String, Object> map, String username,
      String password) throws CoreException {
    return getMethod(url, map, null, username, password);
  }

  /**
   * 执行http get Unicode请求
   *
   * @param url      请求地址
   * @param map      请求参数
   * @param headers  headers
   * @param username 用户名
   * @param password 密码
   * @return
   * @throws CoreException
   */
  public static String getMethod(String url, Map<String, Object> map, Map<String, String> headers, String username,
      String password) throws CoreException {
    HttpClient httpClient = getHttpClientMethod(username, password);
    HttpRequestBase request = httpRequestGet(url, map, true);
    setHeaders(request, headers);
    return excute(request, httpClient);
  }

  /**
   * 执行http get 不Unicode请求
   *
   * @param url
   * @param map
   * @return
   * @throws CoreException
   */
  public static String getMethodUnUnicode(String url, Map<String, Object> map)
      throws CoreException {
    return getMethodUnUnicode(url, map, null, null, null);
  }

  /**
   * 执行http get 不Unicode请求
   *
   * @param url
   * @param map
   * @param headers
   * @return
   * @throws CoreException
   */
  public static String getMethodUnUnicode(String url, Map<String, Object> map, Map<String, String> headers)
      throws CoreException {
    return getMethodUnUnicode(url, map,  headers, null, null);
  }

  /**
   * 执行http get 不Unicode请求
   *
   * @param url      请求地址
   * @param map      请求参数
   * @param username 用户名
   * @param password 密码
   * @return
   * @throws CoreException
   */
  public static String getMethodUnUnicode(String url, Map<String, Object> map, String username,
      String password) throws CoreException {
    return getMethodUnUnicode(url, map, null, username, password);
  }

  /**
   * 执行http get 不Unicode请求
   *
   * @param url      请求地址
   * @param map      请求参数
   * @param headers  headers
   * @param username 用户名
   * @param password 密码
   * @return
   * @throws CoreException
   */
  public static String getMethodUnUnicode(String url, Map<String, Object> map, Map<String, String> headers, String username,
      String password) throws CoreException {
    HttpClient httpClient = getHttpClientMethod(username, password);
    HttpRequestBase request = httpRequestGet(url, map, false);
    setHeaders(request, headers);
    return excute(request, httpClient);
  }

  /**
   * 执行http post 请求
   *
   * @param url 请求地址
   * @param map 请求参数
   * @return
   * @throws CoreException
   */
  public static String postMethod(String url, Map<String, Object> map) throws CoreException {
    return postMethod(url, map, null,null, null);
  }

  /**
   * 执行http post 请求
   *
   * @param url 请求地址
   * @param map 请求参数
   * @param headers headers
   * @return
   * @throws CoreException
   */
  public static String postMethod(String url, Map<String, Object> map, Map<String, String> headers) throws CoreException {
    return postMethod(url, map, headers, null, null);
  }

  /**
   * 执行http post 请求
   *
   * @param url      请求地址
   * @param map      请求参数
   * @param username 用户名
   * @param password 密码
   * @return
   * @throws CoreException
   */
  public static String postMethod(String url, Map<String, Object> map, String username,
      String password) throws CoreException {
    return postMethod(url, map, null, username, password);
  }

  /**
   * 执行http post 请求
   *
   * @param url      请求地址
   * @param map      请求参数
   * @param headers  headers
   * @param username 用户名
   * @param password 密码
   * @return
   * @throws CoreException
   */
  public static String postMethod(String url, Map<String, Object> map, Map<String, String> headers, String username,
      String password) throws CoreException {
    HttpClient httpClient = getHttpClientMethod(username, password);
    HttpRequestBase request = httpRequestPost(url, map, true);
    setHeaders(request, headers);
    return excute(request, httpClient);
  }

  /**
   * 执行http post 不Unicode请求
   *
   * @param url 请求地址
   * @param map 请求参数
   * @return
   * @throws CoreException
   */
  public static String postMethodUnUnicode(String url, Map<String, Object> map)
      throws CoreException {
    return postMethodUnUnicode(url, map, null, null);
  }

  /**
   * 执行http post 不Unicode请求
   *
   * @param url 请求地址
   * @param map 请求参数
   * @param headers headers
   * @return
   * @throws CoreException
   */
  public static String postMethodUnUnicode(String url, Map<String, Object> map, Map<String, String> headers)
      throws CoreException {
    return postMethodUnUnicode(url, map, headers, null, null);
  }

  /**
   * 执行http post 不Unicode请求
   *
   * @param url      请求地址
   * @param map      请求参数
   * @param username 用户名
   * @param password 密码
   * @return
   * @throws CoreException
   */
  public static String postMethodUnUnicode(String url, Map<String, Object> map, String username,
      String password) throws CoreException {
    return postMethodUnUnicode(url, map, null, username, password);
  }

  /**
   * 执行http post 不Unicode请求
   *
   * @param url      请求地址
   * @param map      请求参数
   * @param headers  headers
   * @param password 密码
   * @return
   * @throws CoreException
   */
  public static String postMethodUnUnicode(String url, Map<String, Object> map, Map<String, String> headers, String username,
      String password) throws CoreException {
    HttpClient httpClient = getHttpClientMethod(username, password);
    HttpRequestBase request = httpRequestPost(url, map, false);
    setHeaders(request, headers);
    return excute(request, httpClient);
  }

  /**
   * 执行http post json请求
   *
   * @param url 请求地址
   * @param map 请求参数
   * @return
   * @throws CoreException
   */
  public static String postMethodJson(String url, Map<String, Object> map) throws CoreException {
    return postMethodJson(url, map, null, null, null);
  }

  /**
   * 执行http post json请求
   *
   * @param url 请求地址
   * @param map 请求参数
   * @param headers headers
   * @return
   * @throws CoreException
   */
  public static String postMethodJson(String url, Map<String, Object> map, Map<String, String> headers) throws CoreException {
    return postMethodJson(url, map, headers, null, null);
  }

  /**
   * 执行http post json请求
   *
   * @param url      请求地址
   * @param map      请求参数
   * @param username 用户名
   * @param password 密码
   * @return
   * @throws CoreException
   */
  public static String postMethodJson(String url, Map<String, Object> map, String username,
      String password) throws CoreException {
    return postMethodJson(url, map, null, username, password);
  }

  /**
   * 执行http post json请求
   *
   * @param url      请求地址
   * @param map      请求参数
   * @param headers  headers
   * @param username 用户名
   * @param password 密码
   * @return
   * @throws CoreException
   */
  public static String postMethodJson(String url, Map<String, Object> map, Map<String, String> headers, String username,
      String password) throws CoreException {
    HttpClient httpClient = getHttpClientMethod(username, password);
    String obj = Func.toJson(map);
    HttpRequestBase request = httpRequestJson(url, obj);
    setHeaders(request, headers);
    return excute(request, httpClient);
  }
  


  public static HttpClient getHttpClientMethod(String username, String password) {
    HttpClient httpClient = getHttpClient();
    if (Func.isNotEmpty(username) && Func.isNotEmpty(password)) {
      httpClient = getHttpClient(username, password);
    }
    return httpClient;
  }

  public static String excute(HttpRequestBase request, HttpClient httpClient) throws CoreException {
    try {
      Stopwatch stopwatch = Stopwatch.createStarted();
      log.info("【http】【{}】 start", request.getURI());
      HttpResponse response = httpClient.execute(request);
      int statusCode = response.getStatusLine().getStatusCode();
      log.info("【http】【{}】,statuscode: {} end, cost【{}ms】", request.getURI(), statusCode,
          stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
      String res = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8).trim();
      log.info("http return: {}", res);
      if (statusCode != HttpStatus.SC_OK) {
        throw new CoreException(GlobalErrorCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
            "ERROR【" + statusCode + "】");
      }
      return res;
    } catch (Exception e) {
      log.error("http error:{}", e);
      String message = e != null ? e.getMessage() : "";
      if (e instanceof CoreException) {
        throw (CoreException) e;
      } else if (e instanceof SocketTimeoutException || e instanceof ConnectTimeoutException) {
        if (message.indexOf("connect timed out") > -1) {
          throw new CoreException(GlobalErrorCodeEnum.CONNECTION_TIME_OUT.getCode(),
              GlobalErrorCodeEnum.CONNECTION_TIME_OUT.getEnName());
        } else if (message.indexOf("Read timed out") > -1) {
          throw new CoreException(GlobalErrorCodeEnum.READ_TIME_OUT.getCode(),
              GlobalErrorCodeEnum.READ_TIME_OUT.getEnName());
        } else {
          throw new CoreException(GlobalErrorCodeEnum.CONNECTION_TIME_OUT.getCode(),
              "ERROR【" + message + "】");
        }
      } else {
        throw new CoreException(GlobalErrorCodeEnum.CONNECTION_TIME_OUT.getCode(),
            "ERROR【" + message + "】");
      }
    } finally {
      request.releaseConnection();
    }
  }

  public static HttpRequestBase httpRequestGet(String url, Map<String, Object> map,
      boolean isUnicode) {
    StringBuilder getParam = new StringBuilder();
    getParam.append(url).append("?");
    if (Func.isNotEmpty(map)) {
      for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); ) {
        String key = it.next();
        String value = map.get(key).toString();
        if (!Func.isEmpty(value)) {
          if (isUnicode) {
            try {
              value = URLEncoder.encode(value, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
              log.error("{}", e);
              value = "";
            }
          }
        } else {
          value = "";
        }
        getParam.append(key).append("=").append(value).append("&");
      }
    }
    getParam.deleteCharAt(getParam.length() - 1);
    HttpGet httpGet = new HttpGet(getParam.toString());
    httpGet.addHeader("Connection", "close");
    httpGet.addHeader("Content-Type", "text/xml; charset=utf-8");
    return httpGet;
  }

  public static HttpRequestBase httpRequestPost(String url, Map<String, Object> map,
      boolean isUnicode) {
    HttpPost httpPost = new HttpPost(url);
    httpPost.addHeader("Connection", "close");
    httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
    if (map != null) {
      if (isUnicode) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();
        for (Map.Entry<String, Object> e : entrySet) {
          String name = e.getKey();
          String value = e.getValue() == null ? "" : e.getValue().toString();
          NameValuePair pair = new BasicNameValuePair(name, value);
          params.add(pair);
        }
        try {
          httpPost.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      } else {
        StringBuilder getParam = new StringBuilder();
        for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); ) {
          String key = it.next();
          String value = map.get(key).toString();
          getParam.append(key).append("=").append(value).append("&");
        }
        getParam.deleteCharAt(getParam.length() - 1);
        httpPost.setEntity(new StringEntity(getParam.toString(), StandardCharsets.UTF_8));
      }
    }
    return httpPost;
  }

  public static HttpRequestBase httpRequestJson(String url, String data) {
    HttpPost httpPost = new HttpPost(url);
    if (data != null) {
      httpPost.setEntity(new StringEntity(data, StandardCharsets.UTF_8));
    }
    httpPost.addHeader("Connection", "close");
    httpPost.addHeader("Content-Type", "application/json");
    return httpPost;
  }

  static class Constants {

    /*从连接池中取连接的超时时间*/
    public static final int CONMANTIMEOUT = 2000;
    /*连接超时*/
    public static final int CONTIMEOUT = 2000;
    /*请求超时*/
    public static final int SOTIMEOUT = 15000;
    /*设置整个连接池最大连接数*/
    public static final int MAXTOTAL = 64;
    /*根据连接到的主机对MaxTotal的一个细分*/
    public static final int DEFAULTMAXPERROUTE = 32;
  }
}
