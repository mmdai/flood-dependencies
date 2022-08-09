package cn.flood.mongodb;

import lombok.Data;

/**
 * 
* <p>Title: MongoPoolProperties</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2020年8月23日
 */
@Data
public class MongoPoolProperties {
	
	private String mongoTemplateName = "mongoTemplate";
	
	private String gridFsTemplateName = "gridFsTemplate";
	
	/**
	 * 存储时是否保存_class
	 */
	private boolean showClass = true;
	private String host;
	private Integer port = 27017;
	private String database;
	private String authenticationDatabase;
	private String gridFsDatabase;
	private String username;
	private char[] password;

    private int minConnectionsPerHost;
    private int maxConnectionsPerHost = 100;
    private int threadsAllowedToBlockForConnectionMultiplier = 5;
    private int serverSelectionTimeout = 1000 * 30;
    private long maxWaitTime = 1000 * 60 * 2;
    private long maxConnectionIdleTime;
    private long maxConnectionLifeTime;
    private int connectTimeout = 1000 * 10;
    private int socketTimeout = 0;
    private boolean socketKeepAlive = false;
    private boolean sslEnabled = false;
    private boolean sslInvalidHostNameAllowed = false;
    private boolean alwaysUseMBeans = false;

    private int heartbeatFrequency = 10000;
    private int minHeartbeatFrequency = 500;
    private int heartbeatConnectTimeout = 20000;
    private int heartbeatSocketTimeout = 20000;
    private int localThreshold = 15;

}
