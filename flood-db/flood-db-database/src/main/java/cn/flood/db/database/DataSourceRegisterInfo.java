package cn.flood.db.database;

/**
 * <p>Title: DataSourceRegisterInfo</p>
 * <p>Description: 多数据源配置文件</p>
 *
 * @author mmdai
 * @date 2020年8月12日
 */
public class DataSourceRegisterInfo {

  private boolean primary = false;

  private String url;

  private String username;

  private String password;

  private String driverClassName;
  /**
   * 是否需要复用druid线程池
   */
  private boolean threadPool = true;


  /**
   * 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
   */
  private Integer initialSize;

  /**
   * 最小连接池数量
   */
  private Integer minIdle;

  /**
   * 最大连接池数量
   */
  private Integer maxActive;

  /**
   * 获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。
   */
  private Integer maxWait;

  /**
   * 单位：秒，执行查询的超时时间，单位是秒
   */
  private Integer queryTimeout = 15;

  /**
   * 有两个含义： 1) Destroy线程会检测连接的间隔时间，如果连接空闲时间大于等于minEvictableIdleTimeMillis则关闭物理连接。 2)
   * testWhileIdle的判断依据，详细看testWhileIdle属性的说明
   */
  private Integer timeBetweenEvictionRunsMillis;

  /**
   * 连接保持空闲而不被驱逐的最长时间
   */
  private Integer minEvictableIdleTimeMillis;

  /**
   * 用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会其作用。
   */
  private String validationQuery;
  /**
   * 单位：秒，检测连接是否有效的超时时间。底层调用jdbc Statement对象的void setQueryTimeout(int seconds)方法
   */
  private Integer validationQueryTimeout;

  /**
   * 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
   */
  private Boolean testWhileIdle;

  /**
   * 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
   */
  private Boolean testOnBorrow;

  /**
   * 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
   */
  private Boolean testOnReturn;

  /**
   * 当程序存在缺陷时，申请的连接忘记关闭，这时候，就存在连接泄漏了。Druid提供了RemoveAbandanded相关配置，用来关闭长时间不使用的连接。
   * 注：配置removeAbandoned对性能会有一些影响，建议怀疑存在泄漏之后再打开。在上面的配置中，如果连接超过30分钟未关闭，就会被强行回收，并且日志记录连接申请时的调用堆栈
   */
  private Boolean removeAbandoned;

  /**
   * 连接超时多长时间关闭，单位秒
   */
  private Integer removeAbandonedTimeout;

  /**
   * 默认值为false。是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭
   */
  private Boolean poolPreparedStatements;

  /**
   * 默认值-1。要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。
   * 在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100
   */
  private Integer maxPoolPreparedStatementPerConnectionSize;

  /**
   * 属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有： 监控统计用的filter:stat 日志用的filter:log4j 防御sql注入的filter:wall
   */
  private String filters;
  /**
   * 通过connectProperties属性来打开mergeSql功能；慢SQL记录
   */
  private String connectionProperties;

  /**
   * 合并多个DruidDataSource的监控数据
   */
  private Boolean useGlobalDataSourceStat;
  /**
   * 白名单
   */
  private String allow;

  /**
   * 黑名单
   */
  private String deny;

  public boolean isPrimary() {
    return primary;
  }

  public void setPrimary(boolean primary) {
    this.primary = primary;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDriverClassName() {
    return driverClassName;
  }

  public void setDriverClassName(String driverClassName) {
    this.driverClassName = driverClassName;
  }

  public boolean isThreadPool() {
    return threadPool;
  }

  public void setThreadPool(boolean threadPool) {
    this.threadPool = threadPool;
  }

  public Integer getInitialSize() {
    return initialSize;
  }

  public void setInitialSize(Integer initialSize) {
    this.initialSize = initialSize;
  }

  public Integer getMinIdle() {
    return minIdle;
  }

  public void setMinIdle(Integer minIdle) {
    this.minIdle = minIdle;
  }

  public Integer getMaxActive() {
    return maxActive;
  }

  public void setMaxActive(Integer maxActive) {
    this.maxActive = maxActive;
  }

  public Integer getMaxWait() {
    return maxWait;
  }

  public void setMaxWait(Integer maxWait) {
    this.maxWait = maxWait;
  }

  public Integer getQueryTimeout() {
    return queryTimeout;
  }

  public void setQueryTimeout(Integer queryTimeout) {
    this.queryTimeout = queryTimeout;
  }

  public Integer getTimeBetweenEvictionRunsMillis() {
    return timeBetweenEvictionRunsMillis;
  }

  public void setTimeBetweenEvictionRunsMillis(Integer timeBetweenEvictionRunsMillis) {
    this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
  }

  public Integer getMinEvictableIdleTimeMillis() {
    return minEvictableIdleTimeMillis;
  }

  public void setMinEvictableIdleTimeMillis(Integer minEvictableIdleTimeMillis) {
    this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
  }

  public String getValidationQuery() {
    return validationQuery;
  }

  public void setValidationQuery(String validationQuery) {
    this.validationQuery = validationQuery;
  }

  public Integer getValidationQueryTimeout() {
    return validationQueryTimeout;
  }

  public void setValidationQueryTimeout(Integer validationQueryTimeout) {
    this.validationQueryTimeout = validationQueryTimeout;
  }

  public Boolean getTestWhileIdle() {
    return testWhileIdle;
  }

  public void setTestWhileIdle(Boolean testWhileIdle) {
    this.testWhileIdle = testWhileIdle;
  }

  public Boolean getTestOnBorrow() {
    return testOnBorrow;
  }

  public void setTestOnBorrow(Boolean testOnBorrow) {
    this.testOnBorrow = testOnBorrow;
  }

  public Boolean getTestOnReturn() {
    return testOnReturn;
  }

  public void setTestOnReturn(Boolean testOnReturn) {
    this.testOnReturn = testOnReturn;
  }

  public Boolean getRemoveAbandoned() {
    return removeAbandoned;
  }

  public void setRemoveAbandoned(Boolean removeAbandoned) {
    this.removeAbandoned = removeAbandoned;
  }

  public Integer getRemoveAbandonedTimeout() {
    return removeAbandonedTimeout;
  }

  public void setRemoveAbandonedTimeout(Integer removeAbandonedTimeout) {
    this.removeAbandonedTimeout = removeAbandonedTimeout;
  }

  public Boolean getPoolPreparedStatements() {
    return poolPreparedStatements;
  }

  public void setPoolPreparedStatements(Boolean poolPreparedStatements) {
    this.poolPreparedStatements = poolPreparedStatements;
  }

  public Integer getMaxPoolPreparedStatementPerConnectionSize() {
    return maxPoolPreparedStatementPerConnectionSize;
  }

  public void setMaxPoolPreparedStatementPerConnectionSize(
      Integer maxPoolPreparedStatementPerConnectionSize) {
    this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
  }

  public String getFilters() {
    return filters;
  }

  public void setFilters(String filters) {
    this.filters = filters;
  }

  public String getConnectionProperties() {
    return connectionProperties;
  }

  public void setConnectionProperties(String connectionProperties) {
    this.connectionProperties = connectionProperties;
  }

  public Boolean getUseGlobalDataSourceStat() {
    return useGlobalDataSourceStat;
  }

  public void setUseGlobalDataSourceStat(Boolean useGlobalDataSourceStat) {
    this.useGlobalDataSourceStat = useGlobalDataSourceStat;
  }

  public String getAllow() {
    return allow;
  }

  public void setAllow(String allow) {
    this.allow = allow;
  }

  public String getDeny() {
    return deny;
  }

  public void setDeny(String deny) {
    this.deny = deny;
  }
}
