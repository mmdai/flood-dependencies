package cn.flood.redisdelayqueuespringdemo.listener;

import cn.flood.delay.redis.MessageListener;
import cn.flood.delay.redis.enums.ConsumeStatus;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author mmdai
 * @date 2019/11/22
 */
@Slf4j
@Component
public class OrderCancelListener implements MessageListener<Map> {

  @Override
  public String topic() {
    return "order-cancel";
  }

  @Override
  public ConsumeStatus execute(Map data) {
    try {
      Thread.sleep(60000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    log.info("取消订单: {}", data.toString());
    return ConsumeStatus.CONSUMED;
  }

}
