package cn.flood.delay.redis.core;

import cn.flood.delay.redis.configuration.Config;
import io.lettuce.core.Limit;
import io.lettuce.core.Range;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * DQRedis
 *
 * @author mmdai
 * @date 2019/11/25
 */
public class DQRedis {

  private RedisClient redisClient;

  private RedisClusterClient clusterClient;

  private StatefulRedisConnection<String, String> connection;

  private StatefulRedisClusterConnection<String, String> clusterConnection;

  private boolean isCluster;


  public DQRedis(String ip, int port, String password, Duration timeout,
      Config.Cluster cluster, Config.Sentinel sentinel) {
    ClientResources resources = DefaultClientResources.builder()
        .ioThreadPoolSize(2) //I/O线程数
        .computationThreadPoolSize(2) //任务线程数
        .build();
    if (null != cluster && !CollectionUtils.isEmpty(cluster.getNodes())) {
      Set<RedisURI> uris = new HashSet<>();
      List<String> nodes = cluster.getNodes();
      for (String node : nodes) {
        String[] parts = StringUtils.split(node, ":");
        Assert.notNull(parts, "Must be defined as 'host:port'");
        Assert.state(parts.length == 2, "Must be defined as 'host:port'");
        RedisURI.Builder uri = RedisURI.builder().withHost(parts[0])
            .withPort(Integer.parseInt(parts[1])).withTimeout(timeout);
        if (!ObjectUtils.isEmpty(password)) {
          uri.withPassword(password.toCharArray());
        }
        uris.add(uri.build());

      }
      ClusterClientOptions options = ClusterClientOptions.builder()
          .autoReconnect(true)//是否自动重连
          .pingBeforeActivateConnection(true)//连接激活之前是否执行PING命令
          .validateClusterNodeMembership(true)//是否校验集群节点的成员关系
          .maxRedirects(cluster.getMaxRedirects())
          .build();
      this.clusterClient = RedisClusterClient.create(resources, uris);
      this.clusterClient.setOptions(options);
      this.isCluster = true;
      this.clusterConnection = clusterClient.connect();
    } else if (null != sentinel && !CollectionUtils.isEmpty(sentinel.getNodes())) {
      List<RedisURI> uris = new ArrayList();
      List<String> nodes = sentinel.getNodes();
      for (String node : nodes) {
        String[] parts = StringUtils.split(node, ":");
        Assert.notNull(parts, "Must be defined as 'host:port'");
        Assert.state(parts.length == 2, "Must be defined as 'host:port'");
        RedisURI.Builder uri = RedisURI.builder().withSentinel(parts[0], Integer.parseInt(parts[1]))
            .
                withSentinelMasterId(sentinel.getMaster()).withTimeout(timeout);
        ;
        if (!ObjectUtils.isEmpty(password)) {
          uri.withPassword(password.toCharArray());
        }
        uris.add(uri.build());
      }
      RedisClient client = RedisClient.create(resources);
      StatefulRedisMasterReplicaConnection<String, String> connection = MasterReplica
          .connect(client, StringCodec.UTF8, uris);
      //从节点读取数据
      connection.setReadFrom(ReadFrom.REPLICA);
      this.redisClient = client;
      this.connection = connection;
    } else {
      RedisURI.Builder redisURI = RedisURI.builder().withHost(ip).withPort(port)
          .withTimeout(timeout);
      if (!ObjectUtils.isEmpty(password)) {
        redisURI.withPassword(password.toCharArray());
      }
      this.redisClient = RedisClient.create(resources, redisURI.build());
      this.connection = redisClient.connect();
    }
  }

  public Long syncEval(String script, ScriptOutputType type, String[] keys, String... values) {
    if (isCluster) {
      return clusterConnection.sync().eval(script, type, keys, values);
    } else {
      return connection.sync().eval(script, type, keys, values);
    }
  }

  public List<String> zrangebyscore(String key, Range<? extends Number> range, Limit limit) {
    if (isCluster) {
      return clusterConnection.sync().zrangebyscore(key, range, limit);
    } else {
      return connection.sync().zrangebyscore(key, range, limit);
    }
  }

  public String hget(String key, String feild) {
    if (isCluster) {
      return clusterConnection.sync().hget(key, feild);
    } else {
      return connection.sync().hget(key, feild);
    }
  }

  public boolean hset(String key, String feild, String value) {
    if (isCluster) {
      return clusterConnection.sync().hset(key, feild, value);
    } else {
      return connection.sync().hset(key, feild, value);
    }
  }

  public Long zrem(String key, String... members) {
    if (isCluster) {
      return clusterConnection.sync().zrem(key, members);
    } else {
      return connection.sync().zrem(key, members);
    }
  }

  public Long hdel(String key, String feild) {
    if (isCluster) {
      return clusterConnection.sync().hdel(key, feild);
    } else {
      return connection.sync().hdel(key, feild);
    }
  }

  public <T> RedisFuture<T> asyncEval(String script, ScriptOutputType type, String[] keys,
      String... values) {
    if (isCluster) {
      return clusterConnection.async().eval(script, type, keys, values);
    } else {
      return connection.async().eval(script, type, keys, values);
    }
  }

  void shutdown() {
    if (isCluster) {
      clusterConnection.close();
      clusterClient.shutdown();
    } else {
      connection.close();
      redisClient.shutdown();
    }
  }

}
