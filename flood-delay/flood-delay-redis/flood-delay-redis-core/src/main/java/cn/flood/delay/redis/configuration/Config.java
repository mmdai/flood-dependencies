package cn.flood.delay.redis.configuration;

import cn.flood.delay.redis.core.Callback;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Config
 *
 * @author mmdai
 * @date 2019/11/21
 */

@ConfigurationProperties("spring.redis")
public class Config {

	private static final String DEFAULT_DQUEUE_PREFIX_NAME = "delay-queue-";

	private String keyPrefix = DEFAULT_DQUEUE_PREFIX_NAME;

	private String host;

	private int port = 6379;

	private String password;

	private Sentinel sentinel;

	private Cluster cluster;


	private Duration timeout;

	private int retryInterval = 10;

	private int taskTtl = 24 * 3600;

	private int callbackTtl = 3;

	private int maxJobCoreSize = Runtime.getRuntime().availableProcessors() * 2;

	private int maxCallbackCoreSize = Runtime.getRuntime().availableProcessors() * 2;

	/**
	 * Delay of message consumer callback processing mapping.
	 * <p>
	 * ::key   -> TOPIC
	 * ::VALUE -> CALLBACK INSTANCE
	 */
	private Map<String, Callback> callbacks = new ConcurrentHashMap<>();

	/**
	 * The key in the processing in a single JVM is managed in the collection,
	 * to ensure that only a task in the implementation
	 */
	private Set<String> processedKeys = new CopyOnWriteArraySet<>();

	public Config() {
	}

	public Config(String keyPrefix, String host, int port, String password, Sentinel sentinel,
                  Cluster cluster, Duration timeout, int retryInterval, int taskTtl, int callbackTtl,
                  int maxJobCoreSize, int maxCallbackCoreSize, Map<String, Callback> callbacks,
                  Set<String> processedKeys) {
		this.keyPrefix = keyPrefix;
		this.host = host;
		this.port = port;
		this.password = password;
		this.sentinel = sentinel;
		this.cluster = cluster;
		this.timeout = timeout;
		this.retryInterval = retryInterval;
		this.taskTtl = taskTtl;
		this.callbackTtl = callbackTtl;
		this.maxJobCoreSize = maxJobCoreSize;
		this.maxCallbackCoreSize = maxCallbackCoreSize;
		this.callbacks = callbacks;
		this.processedKeys = processedKeys;
	}

	public static class Sentinel {
		private String master;
		private List<String> nodes;
		private String username;
		private String password;

		public Sentinel() {
		}

		public String getMaster() {
			return this.master;
		}

		public void setMaster(String master) {
			this.master = master;
		}

		public List<String> getNodes() {
			return this.nodes;
		}

		public void setNodes(List<String> nodes) {
			this.nodes = nodes;
		}

		public String getUsername() {
			return this.username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return this.password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	public static class Cluster {
		private List<String> nodes;
		private Integer maxRedirects;

		public Cluster() {
		}

		public List<String> getNodes() {
			return this.nodes;
		}

		public void setNodes(List<String> nodes) {
			this.nodes = nodes;
		}

		public Integer getMaxRedirects() {
			return this.maxRedirects;
		}

		public void setMaxRedirects(Integer maxRedirects) {
			this.maxRedirects = maxRedirects;
		}
	}

	public String getDelayKey() {
		return this.keyPrefix + "keys";
	}

	public String getAckKey() {
		return this.keyPrefix + "acks";
	}

	public String getErrorKey() {
		return this.keyPrefix + "errors";
	}

	public String getHashKey() {
		return this.keyPrefix + "hash";
	}

	public boolean isProcessing(String key) {
		return processedKeys.contains(key);
	}

	public boolean waitProcessing(String key) {
		return !isProcessing(key);
	}

	public void addProcessed(String key) {
		processedKeys.add(key);
	}

	public void processed(String key) {
		processedKeys.remove(key);
	}

	public static String getDefaultDqueuePrefixName() {
		return DEFAULT_DQUEUE_PREFIX_NAME;
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}


	public int getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}

	public int getTaskTtl() {
		return taskTtl;
	}

	public void setTaskTtl(int taskTtl) {
		this.taskTtl = taskTtl;
	}

	public int getCallbackTtl() {
		return callbackTtl;
	}

	public void setCallbackTtl(int callbackTtl) {
		this.callbackTtl = callbackTtl;
	}

	public int getMaxJobCoreSize() {
		return maxJobCoreSize;
	}

	public void setMaxJobCoreSize(int maxJobCoreSize) {
		this.maxJobCoreSize = maxJobCoreSize;
	}

	public int getMaxCallbackCoreSize() {
		return maxCallbackCoreSize;
	}

	public void setMaxCallbackCoreSize(int maxCallbackCoreSize) {
		this.maxCallbackCoreSize = maxCallbackCoreSize;
	}

	public Map<String, Callback> getCallbacks() {
		return callbacks;
	}

	public void setCallbacks(Map<String, Callback> callbacks) {
		this.callbacks = callbacks;
	}

	public Set<String> getProcessedKeys() {
		return processedKeys;
	}

	public void setProcessedKeys(Set<String> processedKeys) {
		this.processedKeys = processedKeys;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Sentinel getSentinel() {
		return sentinel;
	}

	public void setSentinel(Sentinel sentinel) {
		this.sentinel = sentinel;
	}

	public Cluster getCluster() {
		return cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	public Duration getTimeout() {
		return timeout;
	}

	public void setTimeout(Duration timeout) {
		this.timeout = timeout;
	}
}
