package cn.flood.db.redis.lock.autoconfigure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by on 2017/12/29.
 */
@ConfigurationProperties(prefix = RlockConfig.PREFIX)
public class RlockConfig {

  public static final String PREFIX = "spring.lock";
  //redisson
  private String address;
  private String password;
  private int database = 15;
  private ClusterServer clusterServer;
  private SentinelServer sentinelServer;

  private String codec = "org.redisson.codec.JsonJacksonCodec";

  private int connectionMinimumIdleSize = 15;

  private int connectionPoolSize = 20;

  private int threads = 5;
  //lock
  private long waitTime = 60;

  private long leaseTime = 60;

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getCodec() {
    return codec;
  }

  public void setCodec(String codec) {
    this.codec = codec;
  }

  public long getWaitTime() {
    return waitTime;
  }

  public void setWaitTime(long waitTime) {
    this.waitTime = waitTime;
  }

  public long getLeaseTime() {
    return leaseTime;
  }

  public void setLeaseTime(long leaseTime) {
    this.leaseTime = leaseTime;
  }

  public int getDatabase() {
    return database;
  }

  public void setDatabase(int database) {
    this.database = database;
  }

  public SentinelServer getSentinelServer() {
    return sentinelServer;
  }

  public void setSentinelServer(SentinelServer sentinelServer) {
    this.sentinelServer = sentinelServer;
  }

  public ClusterServer getClusterServer() {
    return clusterServer;
  }

  public void setClusterServer(ClusterServer clusterServer) {
    this.clusterServer = clusterServer;
  }

  public int getThreads() {
    return threads;
  }

  public void setThreads(int threads) {
    this.threads = threads;
  }

  public int getConnectionMinimumIdleSize() {
    return connectionMinimumIdleSize;
  }

  public void setConnectionMinimumIdleSize(int connectionMinimumIdleSize) {
    this.connectionMinimumIdleSize = connectionMinimumIdleSize;
  }

  public int getConnectionPoolSize() {
    return connectionPoolSize;
  }

  public void setConnectionPoolSize(int connectionPoolSize) {
    this.connectionPoolSize = connectionPoolSize;
  }

  public static class SentinelServer {

    private String masterName;
    private String[] nodeAddresses;

    public SentinelServer() {
    }

    public String getMasterName() {
      return masterName;
    }

    public void setMasterName(String masterName) {
      this.masterName = masterName;
    }

    public String[] getNodeAddresses() {
      return nodeAddresses;
    }

    public void setNodeAddresses(String[] nodeAddresses) {
      this.nodeAddresses = nodeAddresses;
    }
  }

  public static class ClusterServer {

    private String[] nodeAddresses;

    public String[] getNodeAddresses() {
      return nodeAddresses;
    }

    public void setNodeAddresses(String[] nodeAddresses) {
      this.nodeAddresses = nodeAddresses;
    }
  }
}
