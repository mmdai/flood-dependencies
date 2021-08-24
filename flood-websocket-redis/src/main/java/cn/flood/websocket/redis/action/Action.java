package cn.flood.websocket.redis.action;


import cn.flood.websocket.WebSocketManager;
import com.alibaba.fastjson.JSONObject;


public interface Action {
    String IDENTIFIER = "identifier";
    String USER_ACCOUNT = "userAccount";
    String MESSAGE    = "message";
    String ACTION     = "action";

    void doMessage(WebSocketManager manager, JSONObject object);
}
