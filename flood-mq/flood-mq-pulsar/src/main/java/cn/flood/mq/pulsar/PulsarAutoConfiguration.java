package cn.flood.mq.pulsar;

import cn.flood.mq.pulsar.client.PulsarConsumeClient;
import cn.flood.mq.pulsar.client.PulsarTemplate;
import cn.flood.mq.pulsar.config.PulsarProperties;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/6/15 12:12
 */
@AutoConfiguration
@EnableConfigurationProperties(PulsarProperties.class)
@ConditionalOnExpression("'${pulsar.serviceUrl}'!=null")
public class PulsarAutoConfiguration {

  protected PulsarProperties pulsarProperties;

  @Autowired
  public void setPulsarProperties(PulsarProperties pulsarProperties) {
    this.pulsarProperties = pulsarProperties;
  }


  /**
   * create Pulsar client Bean
   */
  @Bean(value = "pulsarClient")
  public PulsarClient pulsarClient() throws PulsarClientException {
    return PulsarClient.builder().serviceUrl(pulsarProperties.getServiceUrl()).build();
  }

  /**
   * create Pulsar Template Bean
   */
  @Bean(value = "pulsarTemplate")
  public PulsarTemplate pulsarClient(@Autowired PulsarClient pulsarClient)
      throws PulsarClientException {
    return new PulsarTemplate(pulsarClient, pulsarProperties);
  }

  /**
   * create Pulsar Template Bean
   */
  @Bean(value = "pulsarConsumeClient")
  public PulsarConsumeClient pulsarConsumeClient(@Autowired PulsarClient pulsarClient)
      throws PulsarClientException {
    return new PulsarConsumeClient(pulsarClient, pulsarProperties);
  }

}
