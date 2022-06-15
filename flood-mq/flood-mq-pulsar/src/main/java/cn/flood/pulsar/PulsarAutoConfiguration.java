package cn.flood.pulsar;

import cn.flood.pulsar.client.PulsarConsumeClient;
import cn.flood.pulsar.client.PulsarTemplate;
import cn.flood.pulsar.config.PulsarProperties;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/6/15 12:12
 */
@AutoConfiguration
@ConditionalOnExpression("'${pulsar.serviceUrl}'!=null")
public class PulsarAutoConfiguration {

    /**
     * create Pulsar client Bean
     */
    @Bean(value = "pulsarClient")
    public PulsarClient pulsarClient(PulsarProperties pulsarProperties) throws PulsarClientException {
        return PulsarClient.builder().serviceUrl(pulsarProperties.getServiceUrl()).build();
    }

    /**
     * create Pulsar Template Bean
     */
    @Bean(value = "pulsarTemplate")
    public PulsarTemplate pulsarClient(@Autowired PulsarClient pulsarClient, PulsarProperties pulsarProperties) throws PulsarClientException {
        return new PulsarTemplate(pulsarClient, pulsarProperties);
    }

    /**
     * create Pulsar Template Bean
     */
    @Bean(value = "pulsarConsumeClient")
    public PulsarConsumeClient pulsarConsumeClient(@Autowired PulsarClient pulsarClient, PulsarProperties pulsarProperties) throws PulsarClientException {
        return new PulsarConsumeClient(pulsarClient, pulsarProperties);
    }

}
