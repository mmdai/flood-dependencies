package cn.flood.db.mongodb;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ConnectionPoolSettings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * <p>Title: MongoPoolInit</p>
 * <p>Description: 创建多数据源MongoTemplate</p>
 *
 * @author mmdai
 * @date 2020年8月23日
 */
@SuppressWarnings("unchecked")
public class MongoPoolInit implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

  private List<MongoPoolProperties> pools = new ArrayList<MongoPoolProperties>();

  private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {

  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
      throws BeansException {
    int index = 0;
    for (MongoPoolProperties properties : pools) {
      ConnectionPoolSettings poolSetting = buildMongoOptions(properties);
      List<ServerAddress> seeds = Arrays
          .asList(new ServerAddress(properties.getHost(), properties.getPort()));
      // 连接认证，如果设置了用户名和密码的话
      MongoClientSettings settings = null;
//			ConnectionPoolSettings poolSetting = ConnectionPoolSettings.builder().
//					maxWaitTime(properties.getMaxWaitTime(), TimeUnit.MILLISECONDS).build();
      if (!ObjectUtils.isEmpty(properties.getUsername()) && !"".equals(properties.getUsername())) {
        MongoCredential credential = MongoCredential
            .createScramSha1Credential(properties.getUsername(),
                properties.getDatabase(), properties.getPassword());
        settings = MongoClientSettings.builder()
            .credential(credential)
            .applyToConnectionPoolSettings(builder -> builder.applySettings(poolSetting))
            .applyToClusterSettings(builder -> builder.hosts(seeds)).build();
      } else {
        settings = MongoClientSettings.builder()
            .applyToConnectionPoolSettings(builder -> builder.applySettings(poolSetting))
            .applyToClusterSettings(builder -> builder.hosts(seeds)).build();
      }

      MongoClient mongoClient = MongoClients.create(settings);
      SimpleMongoClientDatabaseFactory mongoDbFactory = null;
      if (StringUtils.hasText(properties.getGridFsDatabase())) {
        mongoDbFactory = new SimpleMongoClientDatabaseFactory(mongoClient,
            properties.getGridFsDatabase());
      } else {
        mongoDbFactory = new SimpleMongoClientDatabaseFactory(mongoClient,
            properties.getDatabase());
      }
      MappingMongoConverter converter = buildConverter(mongoDbFactory, properties.isShowClass());
      boolean primary = false;
      if (index == 0) {
        primary = true;
        index++;
      }
      registryMongoTemplate(registry, primary, properties, mongoDbFactory, converter);
      registryGridFsTemplate(registry, primary, properties, mongoDbFactory, converter);
    }

  }

  private void registryGridFsTemplate(BeanDefinitionRegistry registry, boolean primary,
      MongoPoolProperties properties,
      SimpleMongoClientDatabaseFactory mongoDbFactory, MappingMongoConverter converter) {
    AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(GridFsTemplate.class);
    ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
    abd.setScope(scopeMetadata.getScopeName());
    abd.getConstructorArgumentValues().addGenericArgumentValue(mongoDbFactory);
    abd.getConstructorArgumentValues().addGenericArgumentValue(converter);
    abd.setPrimary(primary);
    AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd,
        properties.getGridFsTemplateName());
    BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
  }

  private void registryMongoTemplate(BeanDefinitionRegistry registry, boolean primary,
      MongoPoolProperties properties,
      SimpleMongoClientDatabaseFactory mongoDbFactory, MappingMongoConverter converter) {
    AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(MongoTemplate.class);
    ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
    abd.setScope(scopeMetadata.getScopeName());
    abd.getConstructorArgumentValues().addGenericArgumentValue(mongoDbFactory);
    abd.getConstructorArgumentValues().addGenericArgumentValue(converter);
    abd.setPrimary(primary);
    AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd,
        properties.getMongoTemplateName());
    BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
  }

  private MappingMongoConverter buildConverter(SimpleMongoClientDatabaseFactory mongoDbFactory,
      boolean showClass) {
    MappingMongoConverter converter = new MappingMongoConverter(
        new DefaultDbRefResolver(mongoDbFactory),
        new MongoMappingContext());
    if (!showClass) {
      converter.setTypeMapper(new DefaultMongoTypeMapper(null));
    }
    return converter;
  }

  private ConnectionPoolSettings buildMongoOptions(MongoPoolProperties properties) {
    ConnectionPoolSettings options = ConnectionPoolSettings.builder()
        .maxSize(properties.getMaxConnectionsPerHost())
        .minSize(properties.getMinConnectionsPerHost())
        .maxWaitTime(properties.getMaxWaitTime(), TimeUnit.MILLISECONDS)
        .maxConnectionIdleTime(properties.getMaxConnectionIdleTime(), TimeUnit.MILLISECONDS)
        .maxConnectionLifeTime(properties.getMaxConnectionLifeTime(), TimeUnit.MILLISECONDS)
        .build();
    return options;
  }

  @Override
  public void setEnvironment(Environment environment) {
    // 初始化配置信息到对象的映射
    Map<String, Object> map = Binder.get(environment).bind("spring.data.mongodb", Map.class)
        .orElse(null);
    Set<String> mongoTemplateNames = new TreeSet<String>();
    Set<String> keys = map.keySet();

    for (String key : keys) {
      String mongoTemplateName = key.split("\\.")[0];
      mongoTemplateNames.add(mongoTemplateName);
    }

    for (String name : mongoTemplateNames) {
      Map<String, Object> values = (Map<String, Object>) map.get(name);
      MongoPoolProperties pro = new MongoPoolProperties();
      buildProperties(values, name, pro);
      pools.add(pro);
    }
  }

  private void buildProperties(Map<String, Object> map, String name, MongoPoolProperties pro) {
    pro.setShowClass(formatBoolValue(map, PoolAttributeTag.SHOW_CLASS, true));
    pro.setMongoTemplateName(name);
    pro.setGridFsTemplateName(formatStringValue(map, PoolAttributeTag.GRID_FS_TEMPLATE_NAME));
    pro.setHost(formatStringValue(map, PoolAttributeTag.HOST));
    pro.setDatabase(formatStringValue(map, PoolAttributeTag.DATABASE));
    pro.setAuthenticationDatabase(formatStringValue(map, PoolAttributeTag.AUTH_DATABASE));
    pro.setGridFsDatabase(formatStringValue(map, PoolAttributeTag.GRIDFS_DATABASE));
    pro.setUsername(formatStringValue(map, PoolAttributeTag.USERNAME));
    pro.setPassword(formatChatValue(map, PoolAttributeTag.PASSWORD));

    pro.setMinConnectionsPerHost(formatIntValue(map, PoolAttributeTag.MIN_CONN_PERHOST, 0));
    pro.setMaxConnectionsPerHost(formatIntValue(map, PoolAttributeTag.MAX_CONN_PERHOST, 100));
    pro.setThreadsAllowedToBlockForConnectionMultiplier(
        formatIntValue(map, PoolAttributeTag.THREADS_ALLOWED_TO_BLOCK_FOR_CONN_MULTIPLIER, 5));
    pro.setServerSelectionTimeout(
        formatIntValue(map, PoolAttributeTag.SERVER_SELECTION_TIMEOUT, 1000 * 30));
    pro.setMaxWaitTime(formatIntValue(map, PoolAttributeTag.MAX_WAIT_TIME, 1000 * 60 * 2));
    pro.setMaxConnectionIdleTime(formatIntValue(map, PoolAttributeTag.MAX_CONN_IDLE_TIME, 0));
    pro.setMaxConnectionLifeTime(formatIntValue(map, PoolAttributeTag.MAX_CONN_LIFE_TIME, 0));
    pro.setConnectTimeout(formatIntValue(map, PoolAttributeTag.CONN_TIMEOUT, 1000 * 10));
    pro.setSocketTimeout(formatIntValue(map, PoolAttributeTag.SOCKET_TIMEOUT, 0));

    pro.setSocketKeepAlive(formatBoolValue(map, PoolAttributeTag.SOCKET_KEEP_ALIVE, false));
    pro.setSslEnabled(formatBoolValue(map, PoolAttributeTag.SSL_ENABLED, false));
    pro.setSslInvalidHostNameAllowed(
        formatBoolValue(map, PoolAttributeTag.SSL_INVALID_HOSTNAME_ALLOWED, false));
    pro.setAlwaysUseMBeans(formatBoolValue(map, PoolAttributeTag.ALWAYS_USE_MBEANS, false));

    pro.setHeartbeatFrequency(formatIntValue(map, PoolAttributeTag.HEARTBEAT_FREQUENCY, 10000));
    pro.setMinHeartbeatFrequency(
        formatIntValue(map, PoolAttributeTag.MIN_HEARTBEAT_FREQUENCY, 500));
    pro.setHeartbeatConnectTimeout(
        formatIntValue(map, PoolAttributeTag.HEARTBEAT_CONN_TIMEOUT, 20000));
    pro.setHeartbeatSocketTimeout(
        formatIntValue(map, PoolAttributeTag.HEARTBEAT_SOCKET_TIMEOUT, 20000));
    pro.setLocalThreshold(formatIntValue(map, PoolAttributeTag.LOCAL_THRESHOLD, 15));
  }

  private String formatStringValue(Map<String, Object> map, String key) {
    if (map.containsKey(key)) {
      return map.get(key).toString();
    }
    return null;
  }

  private int formatIntValue(Map<String, Object> map, String key, int defaultValue) {
    if (map.containsKey(key)) {
      return Integer.valueOf(map.get(key).toString());
    }
    return defaultValue;
  }

  private boolean formatBoolValue(Map<String, Object> map, String key, boolean defaultValue) {
    if (map.containsKey(key)) {
      return Boolean.valueOf(map.get(key).toString());
    }
    return defaultValue;
  }

  private char[] formatChatValue(Map<String, Object> map, String key) {
    if (map.containsKey(key)) {
      return map.get(key).toString().toCharArray();
    }
    return new char[0];
  }

}
