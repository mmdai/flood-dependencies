package cn.flood.websocket.redis;

import cn.flood.websocket.WebSocketManager;
import cn.flood.websocket.redis.action.Action;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;


public class DefaultRedisReceiver implements RedisReceiver {

  private static final Logger logger = LoggerFactory.getLogger(DefaultRedisReceiver.class);
  private ApplicationContext applicationContext;

  public DefaultRedisReceiver(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }


  /**
   * 此方法会被反射调用
   */
  @Override
  public void receiveMessage(String message) {
    logger.info(message);
    JSONObject object = JSONObject.parseObject(JSONObject.parse(message).toString());
    if (!object.containsKey(Action.ACTION)) {
      return;
    }
    String actionName = object.getString(Action.ACTION);
    Action action = getAction(actionName);
    action.doMessage(getWebSocketManager(), object);
  }

  protected Action getAction(String actionName) {
    return getApplicationContext().getBean(actionName, Action.class);
  }

  protected WebSocketManager getWebSocketManager() {
    return getApplicationContext()
        .getBean(WebSocketManager.WEBSOCKET_MANAGER_NAME, WebSocketManager.class);
  }
}