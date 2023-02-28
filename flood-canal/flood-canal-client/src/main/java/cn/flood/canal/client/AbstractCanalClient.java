package cn.flood.canal.client;

import cn.flood.canal.handler.MessageHandler;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yang peng
 * @date 2019/3/2619:11
 */

public abstract class AbstractCanalClient implements CanalClient {


  protected volatile boolean flag;
  protected String filter = StringUtils.EMPTY;
  protected Integer batchSize = 1;
  protected Long timeout = 1L;
  protected TimeUnit unit = TimeUnit.SECONDS;
  private Logger log = LoggerFactory.getLogger(AbstractCanalClient.class);
  private Thread workThread;
  private CanalConnector connector;
  private MessageHandler messageHandler;


  @Override
  public void start() {
    log.info("start canal client");
    workThread = new Thread(this::process);
    workThread.setName("canal-client-thread");
    flag = true;
    workThread.start();
  }

  @Override
  public void stop() {
    log.info("stop canal client");
    flag = false;
    if (null != workThread) {
      workThread.interrupt();
    }

  }

  @Override
  public void process() {
    while (flag) {
      try {
        connector.connect();
        connector.subscribe(filter);
        while (flag) {
          Message message = connector.getWithoutAck(batchSize, timeout, unit);
          long batchId = message.getId();
          if (message.getId() != -1 && message.getEntries().size() != 0) {
            log.info("获取消息 {}", message);
            messageHandler.handleMessage(message);
          }
          connector.ack(batchId);
        }
      } catch (Exception e) {
        log.error("canal client 异常", e);
      } finally {
        connector.disconnect();
      }
    }
  }

  public CanalConnector getConnector() {
    return connector;
  }

  public void setConnector(CanalConnector connector) {
    this.connector = connector;
  }

  public MessageHandler getMessageHandler() {
    return messageHandler;
  }

  public void setMessageHandler(MessageHandler messageHandler) {
    this.messageHandler = messageHandler;
  }
}
