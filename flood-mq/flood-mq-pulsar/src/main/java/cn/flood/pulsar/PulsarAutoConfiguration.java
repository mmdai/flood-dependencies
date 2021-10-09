package cn.flood.pulsar;

import cn.flood.pulsar.collector.ConsumerCollector;
import cn.flood.pulsar.collector.ProducerCollector;
import cn.flood.pulsar.properties.PulsarProperties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@EnableConfigurationProperties({PulsarProperties.class})
@Configuration
public class PulsarAutoConfiguration {

    private static final String PILOT_VALUE = "pilot";
    private static final Logger log = LoggerFactory.getLogger(PulsarAutoConfiguration.class);
    @Autowired
    private Environment environment;
    @Autowired
    private PulsarProperties pulsarProperties;

    @ConditionalOnMissingBean
    @Bean
    public PulsarClient pulsarClient() throws PulsarClientException {
        log.info("Yum-Pulsar-Client has been initialized.");
        String authenticationToken = this.pulsarProperties.getAuthentication();
        if (PILOT_VALUE.equals(this.pulsarProperties.getEnv()) && StringUtils.isNotEmpty(this.pulsarProperties.getPilotAuthentication())) {
            log.info("Current is pilot environment.");
            authenticationToken = this.pulsarProperties.getPilotAuthentication();
        }
        return PulsarClient.builder().serviceUrl(this.pulsarProperties.getServiceUrl()).authentication(AuthenticationFactory.token(authenticationToken)).listenerThreads(this.pulsarProperties.getListenerThreads().intValue()).enableTcpNoDelay(this.pulsarProperties.getEnableTcpNoDelay().booleanValue()).build();
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(havingValue = "false", matchIfMissing = true, name = {"disable-consumer"}, prefix = "flood.pulsar")
    @Bean
    public ConsumerCollector consumerCollector() throws PulsarClientException {
        log.info("Yum-Pulsar consumer-collector has been initialized.");
        ConsumerCollector consumerCollector = new ConsumerCollector();
        consumerCollector.setCachedThreadPool(consumerCachedThreadPool());
        consumerCollector.setEnvironment(this.environment);
        consumerCollector.setPulsarProperties(this.pulsarProperties);
        consumerCollector.setPulsarClient(pulsarClient());
        return consumerCollector;
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(havingValue = "false", matchIfMissing = true, name = {"disable-producer"}, prefix = "flood.pulsar")
    @Bean
    public ProducerCollector producerCollector() throws PulsarClientException {
        log.info("Yum-Pulsar producer-collector has been initialized.");
        ProducerCollector producerCollector = new ProducerCollector();
        producerCollector.setEnvironment(this.environment);
        producerCollector.setPulsarClient(pulsarClient());
        producerCollector.setPulsarProperties(this.pulsarProperties);
        return producerCollector;
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(havingValue = "false", matchIfMissing = true, name = {"disable-consumer"}, prefix = "yum.pulsar")
    @Bean(name = {"consumerCachedThreadPool"})
    public ExecutorService consumerCachedThreadPool() {
        log.info("Yum-Pulsar consumer-thread-pool has been initialized.");
        return new ThreadPoolExecutor(PulsarProperties.DEFAULT_CONSUMER_THREAD_POOL_CORE_SIZE, this.pulsarProperties.getConsumerMaximumPoolSize().intValue(), 60, TimeUnit.SECONDS, new SynchronousQueue(), new ConsumerThreadFactory());
    }

    /* access modifiers changed from: package-private */
    public static class ConsumerThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final String namePrefix;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        ConsumerThreadFactory() {
            ThreadGroup threadGroup;
            SecurityManager s = System.getSecurityManager();
            if (s != null) {
                threadGroup = s.getThreadGroup();
            } else {
                threadGroup = Thread.currentThread().getThreadGroup();
            }
            this.group = threadGroup;
            this.namePrefix = "flood-pulsar-consumer-pool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != 5) {
                t.setPriority(5);
            }
            return t;
        }
    }

}
