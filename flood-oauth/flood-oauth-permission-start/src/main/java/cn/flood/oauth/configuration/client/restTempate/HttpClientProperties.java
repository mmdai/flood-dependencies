package cn.flood.oauth.configuration.client.restTempate;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
    prefix = "feign.httpclient"
)
public class HttpClientProperties {

  public static final boolean DEFAULT_DISABLE_SSL_VALIDATION = false;
  public static final int DEFAULT_MAX_CONNECTIONS = 200;
  public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 50;
  public static final long DEFAULT_TIME_TO_LIVE = 900L;
  public static final TimeUnit DEFAULT_TIME_TO_LIVE_UNIT;
  public static final boolean DEFAULT_FOLLOW_REDIRECTS = true;
  public static final int DEFAULT_CONNECTION_TIMEOUT = 2000;
  public static final int DEFAULT_CONNECTION_TIMER_REPEAT = 3000;

  static {
    DEFAULT_TIME_TO_LIVE_UNIT = TimeUnit.SECONDS;
  }

  private boolean disableSslValidation = false;
  private int maxConnections = 200;
  private int maxConnectionsPerRoute = 50;
  private long timeToLive = 900L;
  private TimeUnit timeToLiveUnit;
  private boolean followRedirects;
  private int connectionTimeout;
  private int connectionTimerRepeat;
  private Hc5Properties hc5;

  public HttpClientProperties() {
    this.timeToLiveUnit = DEFAULT_TIME_TO_LIVE_UNIT;
    this.followRedirects = true;
    this.connectionTimeout = 2000;
    this.connectionTimerRepeat = 3000;
    this.hc5 = new Hc5Properties();
  }

  public int getConnectionTimerRepeat() {
    return this.connectionTimerRepeat;
  }

  public void setConnectionTimerRepeat(int connectionTimerRepeat) {
    this.connectionTimerRepeat = connectionTimerRepeat;
  }

  public boolean isDisableSslValidation() {
    return this.disableSslValidation;
  }

  public void setDisableSslValidation(boolean disableSslValidation) {
    this.disableSslValidation = disableSslValidation;
  }

  public int getMaxConnections() {
    return this.maxConnections;
  }

  public void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

  public int getMaxConnectionsPerRoute() {
    return this.maxConnectionsPerRoute;
  }

  public void setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
    this.maxConnectionsPerRoute = maxConnectionsPerRoute;
  }

  public long getTimeToLive() {
    return this.timeToLive;
  }

  public void setTimeToLive(long timeToLive) {
    this.timeToLive = timeToLive;
  }

  public TimeUnit getTimeToLiveUnit() {
    return this.timeToLiveUnit;
  }

  public void setTimeToLiveUnit(TimeUnit timeToLiveUnit) {
    this.timeToLiveUnit = timeToLiveUnit;
  }

  public boolean isFollowRedirects() {
    return this.followRedirects;
  }

  public void setFollowRedirects(boolean followRedirects) {
    this.followRedirects = followRedirects;
  }

  public int getConnectionTimeout() {
    return this.connectionTimeout;
  }

  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public Hc5Properties getHc5() {
    return this.hc5;
  }

  public void setHc5(Hc5Properties hc5) {
    this.hc5 = hc5;
  }

  public static class Hc5Properties {

    public static final PoolConcurrencyPolicy DEFAULT_POOL_CONCURRENCY_POLICY;
    public static final PoolReusePolicy DEFAULT_POOL_REUSE_POLICY;
    public static final int DEFAULT_SOCKET_TIMEOUT = 5;
    public static final TimeUnit DEFAULT_SOCKET_TIMEOUT_UNIT;

    static {
      DEFAULT_POOL_CONCURRENCY_POLICY = PoolConcurrencyPolicy.STRICT;
      DEFAULT_POOL_REUSE_POLICY = PoolReusePolicy.FIFO;
      DEFAULT_SOCKET_TIMEOUT_UNIT = TimeUnit.SECONDS;
    }

    private PoolConcurrencyPolicy poolConcurrencyPolicy;
    private PoolReusePolicy poolReusePolicy;
    private int socketTimeout;
    private TimeUnit socketTimeoutUnit;

    public Hc5Properties() {
      this.poolConcurrencyPolicy = DEFAULT_POOL_CONCURRENCY_POLICY;
      this.poolReusePolicy = DEFAULT_POOL_REUSE_POLICY;
      this.socketTimeout = 5;
      this.socketTimeoutUnit = DEFAULT_SOCKET_TIMEOUT_UNIT;
    }

    public PoolConcurrencyPolicy getPoolConcurrencyPolicy() {
      return this.poolConcurrencyPolicy;
    }

    public void setPoolConcurrencyPolicy(PoolConcurrencyPolicy poolConcurrencyPolicy) {
      this.poolConcurrencyPolicy = poolConcurrencyPolicy;
    }

    public PoolReusePolicy getPoolReusePolicy() {
      return this.poolReusePolicy;
    }

    public void setPoolReusePolicy(PoolReusePolicy poolReusePolicy) {
      this.poolReusePolicy = poolReusePolicy;
    }

    public TimeUnit getSocketTimeoutUnit() {
      return this.socketTimeoutUnit;
    }

    public void setSocketTimeoutUnit(TimeUnit socketTimeoutUnit) {
      this.socketTimeoutUnit = socketTimeoutUnit;
    }

    public int getSocketTimeout() {
      return this.socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
      this.socketTimeout = socketTimeout;
    }

    public static enum PoolReusePolicy {
      LIFO,
      FIFO;

      private PoolReusePolicy() {
      }
    }

    public static enum PoolConcurrencyPolicy {
      LAX,
      STRICT;

      private PoolConcurrencyPolicy() {
      }
    }
  }
}
