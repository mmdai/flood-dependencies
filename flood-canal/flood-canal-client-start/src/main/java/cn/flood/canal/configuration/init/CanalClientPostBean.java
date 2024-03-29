package cn.flood.canal.configuration.init;

import cn.flood.canal.client.KafkaCanalClient;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * @author gujiachun
 */
//@Component
public class CanalClientPostBean implements ApplicationRunner, DisposableBean {

  @Autowired
  public List<KafkaCanalClient> kafkaCanalClientList;
  private Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public void destroy() throws Exception {
    log.info("=========destroy begin========");
    if (null != kafkaCanalClientList && kafkaCanalClientList.size() > 0) {
      for (KafkaCanalClient client : kafkaCanalClientList) {
        log.info("=========destroy========");
        client.stop();
      }
    }
    log.info("=========destroy end========");
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (null != kafkaCanalClientList && kafkaCanalClientList.size() > 0) {
      for (KafkaCanalClient client : kafkaCanalClientList) {
        client.start();
      }
    }
  }
}
