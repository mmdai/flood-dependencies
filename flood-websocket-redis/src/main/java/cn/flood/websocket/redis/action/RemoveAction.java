package cn.flood.websocket.redis.action;


import cn.flood.websocket.WebSocketManager;
import com.alibaba.fastjson.JSONObject;


public class RemoveAction implements Action {

  @Override
  public void doMessage(WebSocketManager manager, JSONObject object) {
    if (!object.containsKey(IDENTIFIER)) {
      return;
    }

    String identifier = object.getString(IDENTIFIER);
    manager.remove(identifier);
  }
}
