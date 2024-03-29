package cn.flood.db.elasticsearch.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
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
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

/**
 * @program: esclientrhl
 * @description: http客户端工具
 * @author: X-Pacific zhang
 * @create: 2019-10-10 12:53
 **/
public class HttpClientTool {

  private static final String UTF_8 = StandardCharsets.UTF_8.name();
  private static HttpClient mHttpClient = null;

  private static CloseableHttpClient getHttpClient(HttpClientBuilder httpClientBuilder) {
    RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
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

  private synchronized static HttpClient getESHttpClient() {
    if (mHttpClient == null) {
//            HttpParams params = new BasicHttpParams();
//            //设置基本参数
//            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//            HttpProtocolParams.setContentCharset(params, Constants.CHARSET);
//            HttpProtocolParams.setUseExpectContinue(params, true);
//            //超时设置
//            /*从连接池中取连接的超时时间*/
//            ConnManagerParams.setTimeout(params, Constants.CONMANTIMEOUT);
//            /*连接超时*/
//            HttpConnectionParams.setConnectionTimeout(params, Constants.CONTIMEOUT);
//            /*请求超时*/
//            HttpConnectionParams.setSoTimeout(params, Constants.SOTIMEOUT);
//            //设置HttpClient支持HTTp和HTTPS两种模式
//            SchemeRegistry schReg = new SchemeRegistry();
//            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//            schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
//            PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schReg);
//            cm.setMaxTotal(Constants.MAXTOTAL);
//            cm.setDefaultMaxPerRoute(Constants.DEFAULTMAXPERROUTE);
//            mHttpClient = new DefaultHttpClient(cm,params);
      mHttpClient = getHttpClient(HttpClientBuilder.create());
    }
    return mHttpClient;
  }

  private synchronized static HttpClient getESHttpClient(String username, String password) {
    if (mHttpClient == null) {
      mHttpClient = getHttpClientWithBasicAuth(username, password);
    }
    return mHttpClient;
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

  //设置头信息，e.g. content-type 等
  private static void setHeaders(HttpRequestBase req, Map<String, String> headers) {
    if (headers == null) {
      return;
    }
    for (Map.Entry<String, String> header : headers.entrySet()) {
      req.setHeader(header.getKey(), header.getValue());
    }
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * 执行http请求
   *
   * @param url
   * @param obj
   * @return
   * @throws Exception
   */
  public static String execute(String url, String obj) throws Exception {
    HttpClient httpClient = null;
    HttpResponse response = null;
    httpClient = HttpClientTool.getESHttpClient();
    HttpUriRequest request = postMethod(url, obj);
    response = httpClient.execute(request);
    HttpEntity entity1 = response.getEntity();
    String respContent = EntityUtils.toString(entity1, UTF_8).trim();
    return respContent;
  }

  /**
   * 执行http请求
   *
   * @param url
   * @param obj
   * @return
   * @throws Exception
   */
  public static String execute(String url, String obj, String username, String password)
      throws Exception {
    HttpClient httpClient = null;
    HttpResponse response = null;
    httpClient = HttpClientTool.getESHttpClient(username, password);
    HttpUriRequest request = postMethod(url, obj);
    response = httpClient.execute(request);
    HttpEntity entity1 = response.getEntity();
    String respContent = EntityUtils.toString(entity1, UTF_8).trim();
    return respContent;
  }

  private static HttpUriRequest postMethod(String url, String data)
      throws UnsupportedEncodingException {
    HttpPost httpPost = new HttpPost(url);
    if (data != null) {
      httpPost.setEntity(new StringEntity(data, StandardCharsets.UTF_8));
    }
    httpPost.addHeader("Content-Type", "application/json");
    return httpPost;
  }

  static class Constants {

    /**
     * 编码
     */
    public static final String CHARSET = UTF_8;
    /*从连接池中取连接的超时时间*/
    public static final int CONMANTIMEOUT = 5000;
    /*连接超时*/
    public static final int CONTIMEOUT = 5000;
    /*请求超时*/
    public static final int SOTIMEOUT = 20000;
    /*设置整个连接池最大连接数*/
    public static final int MAXTOTAL = 64;
    /*根据连接到的主机对MaxTotal的一个细分*/
    public static final int DEFAULTMAXPERROUTE = 16;
  }
}
