package cn.flood.websocket.redis.action;


import cn.flood.websocket.WebSocket;
import cn.flood.websocket.WebSocketManager;
import cn.flood.websocket.utils.WebSocketUtil;
import com.alibaba.fastjson.JSONObject;

import java.util.List;


public class SendMessageAction implements Action{
    @Override
    public void doMessage(WebSocketManager manager, JSONObject object) {
        if(!object.containsKey(USER_ACCOUNT)){
            return;
        }
        if(!object.containsKey(MESSAGE)){
            return;
        }

        String userAccount = object.getString(USER_ACCOUNT);

        List<WebSocket> list = manager.getList(userAccount);
        if(list==null||list.size()==0){
            return;
        }
        for (WebSocket webSocket:list) {
            WebSocketUtil.sendMessageAsync(webSocket.getSession() , object.getString(MESSAGE));
        }
    }
}
