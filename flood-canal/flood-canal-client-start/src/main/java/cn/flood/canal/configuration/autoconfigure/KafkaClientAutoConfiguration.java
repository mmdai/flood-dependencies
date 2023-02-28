package cn.flood.canal.configuration.autoconfigure;


import cn.flood.canal.client.KafkaCanalClient;
import cn.flood.canal.configuration.init.CanalClientPostBean;
import cn.flood.canal.configuration.properties.CanalKafkaProperties;
import cn.flood.canal.configuration.properties.CanalProperties;
import cn.flood.canal.factory.MapColumnModelFactory;
import cn.flood.canal.handler.EntryHandler;
import cn.flood.canal.handler.MessageHandler;
import cn.flood.canal.handler.RowDataHandler;
import cn.flood.canal.handler.impl.AsyncFlatMessageHandlerImpl;
import cn.flood.canal.handler.impl.MapRowDataHandlerImpl;
import cn.flood.canal.handler.impl.SyncFlatMessageHandlerImpl;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties(CanalKafkaProperties.class)
@ConditionalOnBean(value = {EntryHandler.class})
@ConditionalOnProperty(value = CanalProperties.CANAL_MODE, havingValue = "kafka")
@Import({ThreadPoolAutoConfiguration.class, CanalClientPostBean.class})
public class KafkaClientAutoConfiguration {


  private CanalKafkaProperties canalKafkaProperties;


  public KafkaClientAutoConfiguration(CanalKafkaProperties canalKafkaProperties) {
    this.canalKafkaProperties = canalKafkaProperties;
  }


  @Bean
  public RowDataHandler<List<Map<String, String>>> rowDataHandler() {
    return new MapRowDataHandlerImpl(new MapColumnModelFactory());
  }

  @Bean
  @ConditionalOnProperty(value = CanalProperties.CANAL_ASYNC, havingValue = "true", matchIfMissing = true)
  public MessageHandler asyncFlatMessageHandler(
      RowDataHandler<List<Map<String, String>>> rowDataHandler,
      List<EntryHandler> entryHandlers,
      ExecutorService executorService) {
    return new AsyncFlatMessageHandlerImpl(entryHandlers, rowDataHandler, executorService);
  }


  @Bean
  @ConditionalOnProperty(value = CanalProperties.CANAL_ASYNC, havingValue = "false")
  public MessageHandler syncFlatMessageHandler(
      RowDataHandler<List<Map<String, String>>> rowDataHandler, List<EntryHandler> entryHandlers) {
    return new SyncFlatMessageHandlerImpl(entryHandlers, rowDataHandler);
  }

//    @Bean(initMethod = "start", destroyMethod = "stop")
//    public KafkaCanalClient kafkaCanalClient(MessageHandler messageHandler) {
//        return KafkaCanalClient.builder().servers(canalKafkaProperties.getServer())
//                .groupId(canalKafkaProperties.getGroupId())
//                .topic(canalKafkaProperties.getDestination())
//                .messageHandler(messageHandler)
//                .batchSize(canalKafkaProperties.getBatchSize())
//                .filter(canalKafkaProperties.getFilter())
//                .timeout(canalKafkaProperties.getTimeout())
//                .unit(canalKafkaProperties.getUnit())
//                .build();
//    }

  @Bean
  public List<KafkaCanalClient> kafkaCanalClientList(MessageHandler messageHandler) {
    List<KafkaCanalClient> clientList = Lists.newArrayList();
    String[] topics = canalKafkaProperties.getDestination().split(",");
    String[] groups = canalKafkaProperties.getGroupId().split(",");
    if (ArrayUtils.isNotEmpty(topics) && ArrayUtils.isNotEmpty(groups)
        && topics.length == groups.length) {
      int i = 0;
      for (String topic : topics) {
        KafkaCanalClient client = KafkaCanalClient.builder()
            .servers(canalKafkaProperties.getServer())
            .groupId(groups[i])
            .topic(topic)
            .messageHandler(messageHandler)
            .batchSize(canalKafkaProperties.getBatchSize())
            .filter(canalKafkaProperties.getFilter())
            .timeout(canalKafkaProperties.getTimeout())
            .unit(canalKafkaProperties.getUnit())
            .build();
        clientList.add(client);
        i++;
      }
    }
    return clientList;
  }

}
