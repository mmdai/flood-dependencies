package cn.flood.base.okhttp.client;

import okhttp3.ConnectionPool;

import java.util.concurrent.TimeUnit;

/**
 * Creates {@link ConnectionPool}s for {@link okhttp3.OkHttpClient}s.
 *
 * @author Ryan Baxter
 */
public interface OkHttpClientConnectionPoolFactory {

    /**
     * Creates a new {@link ConnectionPool}.
     * @param maxIdleConnections Number of max idle connections to allow.
     * @param keepAliveDuration Amount of time to keep connections alive.
     * @param timeUnit The time unit for the keep-alive duration.
     * @return A new {@link ConnectionPool}.
     */
    ConnectionPool create(int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit);
}
