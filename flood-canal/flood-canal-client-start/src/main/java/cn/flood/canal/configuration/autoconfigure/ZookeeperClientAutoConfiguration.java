package cn.flood.canal.configuration.autoconfigure;


import cn.flood.canal.client.ZookeeperClusterCanalClient;
import cn.flood.canal.configuration.properties.CanalProperties;
import cn.flood.canal.configuration.properties.CanalSimpleProperties;
import cn.flood.canal.factory.EntryColumnModelFactory;
import cn.flood.canal.handler.EntryHandler;
import cn.flood.canal.handler.MessageHandler;
import cn.flood.canal.handler.RowDataHandler;
import cn.flood.canal.handler.impl.AsyncMessageHandlerImpl;
import cn.flood.canal.handler.impl.RowDataHandlerImpl;
import cn.flood.canal.handler.impl.SyncMessageHandlerImpl;
import com.alibaba.otter.canal.protocol.CanalEntry;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties(CanalSimpleProperties.class)
@ConditionalOnBean(value = {EntryHandler.class})
@ConditionalOnProperty(value = CanalProperties.CANAL_MODE, havingValue = "zk")
@Import(ThreadPoolAutoConfiguration.class)
public class ZookeeperClientAutoConfiguration {


  private CanalSimpleProperties canalSimpleProperties;


  public ZookeeperClientAutoConfiguration(CanalSimpleProperties canalSimpleProperties) {
    this.canalSimpleProperties = canalSimpleProperties;
  }

  @Bean
  public RowDataHandler<CanalEntry.RowData> rowDataHandler() {
    return new RowDataHandlerImpl(new EntryColumnModelFactory());
  }

  @Bean
  @ConditionalOnProperty(value = CanalProperties.CANAL_ASYNC, havingValue = "true", matchIfMissing = true)
  public MessageHandler asyncMessageHandler(RowDataHandler<CanalEntry.RowData> rowDataHandler,
      List<EntryHandler> entryHandlers,
      ExecutorService executorService) {
    return new AsyncMessageHandlerImpl(entryHandlers, rowDataHandler, executorService);
  }


  @Bean
  @ConditionalOnProperty(value = CanalProperties.CANAL_ASYNC, havingValue = "false")
  public MessageHandler syncMessageHandler(RowDataHandler<CanalEntry.RowData> rowDataHandler,
      List<EntryHandler> entryHandlers) {
    return new SyncMessageHandlerImpl(entryHandlers, rowDataHandler);
  }


  @Bean(initMethod = "start", destroyMethod = "stop")
  public ZookeeperClusterCanalClient zookeeperClusterCanalClient(MessageHandler messageHandler) {
    return ZookeeperClusterCanalClient.builder()
        .zkServers(canalSimpleProperties.getServer())
        .destination(canalSimpleProperties.getDestination())
        .userName(canalSimpleProperties.getUserName())
        .password(canalSimpleProperties.getPassword())
        .batchSize(canalSimpleProperties.getBatchSize())
        .filter(canalSimpleProperties.getFilter())
        .timeout(canalSimpleProperties.getTimeout())
        .unit(canalSimpleProperties.getUnit())
        .messageHandler(messageHandler)
        .build();
  }

}
