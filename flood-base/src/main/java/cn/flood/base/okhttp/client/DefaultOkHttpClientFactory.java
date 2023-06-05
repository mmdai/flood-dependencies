package cn.flood.base.okhttp.client;

import okhttp3.OkHttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Default implementation of {@link OkHttpClientFactory}.
 *
 * @author Ryan Baxter
 */
public class DefaultOkHttpClientFactory implements OkHttpClientFactory {

    private static final Log LOG = LogFactory.getLog(DefaultOkHttpClientFactory.class);

    private OkHttpClient.Builder builder;

    public DefaultOkHttpClientFactory(OkHttpClient.Builder builder) {
        this.builder = builder;
    }

    @Override
    public OkHttpClient.Builder createBuilder(boolean disableSslValidation) {
        if (disableSslValidation) {
            try {
                X509TrustManager disabledTrustManager = new DisableValidationTrustManager();
                TrustManager[] trustManagers = new TrustManager[1];
                trustManagers[0] = disabledTrustManager;
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustManagers, new java.security.SecureRandom());
                SSLSocketFactory disabledSSLSocketFactory = sslContext.getSocketFactory();
                this.builder.sslSocketFactory(disabledSSLSocketFactory, disabledTrustManager);
                this.builder.hostnameVerifier(new TrustAllHostnames());
            }
            catch (NoSuchAlgorithmException e) {
                LOG.warn("Error setting SSLSocketFactory in OKHttpClient", e);
            }
            catch (KeyManagementException e) {
                LOG.warn("Error setting SSLSocketFactory in OKHttpClient", e);
            }
        }
        return this.builder;
    }
}
