package cn.flood.canal.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import cn.flood.canal.client.exception.CanalClientException;
import cn.flood.canal.client.transfer.TransponderFactory;
import cn.flood.canal.config.CanalConfig;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract implements of the CanalClient interface
 * It help to initialize the canal connector and so on..
 *
 * @author chen.qian
 */
public abstract class AbstractCanalClient implements CanalClient {

    /**
     * running flag
     */
    private volatile boolean running;

    /**
     * customer config
     */
    private CanalConfig canalConfig;


    /**
     * TransponderFactory
     */
    protected final TransponderFactory factory;

    AbstractCanalClient(CanalConfig canalConfig, TransponderFactory factory) {
        Objects.requireNonNull(canalConfig, "canalConfig can not be null!");
        Objects.requireNonNull(canalConfig, "transponderFactory can not be null!");
        this.canalConfig = canalConfig;
        this.factory = factory;
    }

    @Override
    public void start() {
        Map<String, CanalConfig.Instance> instanceMap = getConfig();
        for (Map.Entry<String, CanalConfig.Instance> instanceEntry : instanceMap.entrySet()) {
            process(processInstanceEntry(instanceEntry), instanceEntry);
        }

    }

    /**
     * To initialize the canal connector
     * @param connector CanalConnector
     * @param config config
     */
    protected abstract void process(CanalConnector connector, Map.Entry<String, CanalConfig.Instance> config);

    private CanalConnector processInstanceEntry(Map.Entry<String, CanalConfig.Instance> instanceEntry) {
        CanalConfig.Instance instance = instanceEntry.getValue();
        CanalConnector connector;
        if (instance.isClusterEnabled()) {
            String zkServers = instance.getZookeeperAddress();
            for (String s : instance.getZookeeperAddress().split(",")) {
                String[] entry = s.split(":");
                if (entry.length != 2)
                    throw new CanalClientException("error parsing zookeeper address:" + s);
//                addresses.add(new InetSocketAddress(entry[0], Integer.parseInt(entry[1])));
            }
            connector = CanalConnectors.newClusterConnector(zkServers, instanceEntry.getKey(),
                    instance.getUserName(),
                    instance.getPassword());
        } else {
            connector = CanalConnectors.newSingleConnector(new InetSocketAddress(instance.getHost(), instance.getPort()),
                    instanceEntry.getKey(),
                    instance.getUserName(),
                    instance.getPassword());
        }
        connector.connect();
        if (!StringUtils.isEmpty(instance.getFilter())) {
            connector.subscribe(instance.getFilter());
        } else {
            connector.subscribe();
        }

        connector.rollback();
        return connector;
    }

    /**
     * get the config
     *
     * @return config
     */
    protected Map<String, CanalConfig.Instance> getConfig() {
        CanalConfig config = canalConfig;
        Map<String, CanalConfig.Instance> instanceMap;
        if (config != null && (instanceMap = config.getInstances()) != null && !instanceMap.isEmpty()) {
            return config.getInstances();
        } else {
            throw new CanalClientException("can not get the configuration of canal client!");
        }
    }

    @Override
    public void stop() {
        setRunning(false);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }
}
