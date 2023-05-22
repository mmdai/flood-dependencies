package cn.flood.oauth.configuration.client.httpclient;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;

/**
 * Default implementation of {@link OkHttpClientConnectionPoolFactory}.
 *
 * @author Ryan Baxter
 */
public class DefaultOkHttpClientConnectionPoolFactory implements OkHttpClientConnectionPoolFactory {

    @Override
    public ConnectionPool create(int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit) {
        return new ConnectionPool(maxIdleConnections, keepAliveDuration, timeUnit);
    }

}
