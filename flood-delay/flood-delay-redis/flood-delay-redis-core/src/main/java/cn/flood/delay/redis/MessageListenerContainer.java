package cn.flood.delay.redis;

import cn.flood.delay.redis.configuration.Config;
import cn.flood.delay.redis.core.Callback;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * MessageListenerContainer
 *
 * @author mmdai
 * @date 2019/11/22
 */
public class MessageListenerContainer implements BeanPostProcessor {

  private Map<String, Callback> callbacks;

  public MessageListenerContainer(Config config) {
    this.callbacks = config.getCallbacks();
  }

  @Override
  public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
    return o;
  }

  @Override
  public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
    if (o instanceof MessageListener) {
      MessageListener messageListener = (MessageListener) o;
      callbacks.put(messageListener.topic(), messageListener::execute);
    }
    return o;
  }

}
