package cn.flood.websocket.redis.action;


import cn.flood.websocket.WebSocketManager;
import cn.flood.websocket.utils.WebSocketUtil;
import com.alibaba.fastjson.JSONObject;

/**
 * { "action":"broadcast", "message":"xxxxxxxxxxxxx" } 广播给所有的websocket发送消息 action
 *
 * @author xiongshiyan at 2018/10/12 , contact me with email yanshixiong@126.com or phone
 * 15208384257
 */
public class BroadCastAction implements Action {

  @Override
  public void doMessage(WebSocketManager manager, JSONObject object) {
    if (!object.containsKey(MESSAGE)) {
      return;
    }
    String message = object.getString(MESSAGE);
    //从本地取出所有的websocket发送消息
    manager.localWebSocketMap().values().forEach(
        webSocket -> WebSocketUtil.sendMessage(
            webSocket.getSession(), message));
  }
}
