package cn.flood.pulsar.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * Pulsar properties configuration
 * @author pig
 **/

@ConfigurationProperties(prefix = "pulsar")
@Data
public class PulsarProperties {
    private String serviceUrl;
    private String tenancy;
    private String namespace;
    private boolean persistent = true;
    private ProducerProperties producer;
    private ConsumerProperties consumer;

}
