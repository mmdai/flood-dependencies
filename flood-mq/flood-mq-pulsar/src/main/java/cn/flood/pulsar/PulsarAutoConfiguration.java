package cn.flood.pulsar;

import cn.flood.pulsar.properties.PulsarProperties;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan(basePackages = {"cn.flood.pulsar"})
@EnableConfigurationProperties({PulsarProperties.class})
public class PulsarAutoConfiguration {

    @Autowired
    private PulsarProperties pulsarProperties;

    @Bean
    @ConditionalOnMissingBean
    public PulsarClient pulsarClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl(this.pulsarProperties.getServiceUrl())
                .authentication(AuthenticationFactory.token(this.pulsarProperties.getAuthentication()))
                .listenerThreads(this.pulsarProperties.getListenerThreads().intValue())
                .enableTcpNoDelay(this.pulsarProperties.getEnableTcpNoDelay().booleanValue())
                .build();
    }

    @Bean(name = {"consumerCachedThreadPool"})
    public ExecutorService cachedThreadPool() {
        return new ThreadPoolExecutor(this.pulsarProperties
                .getConsumerCorePoolSize().intValue(), this.pulsarProperties
                .getConsumerMaximumPoolSize().intValue(), 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }
}
