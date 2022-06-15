package cn.flood.websocket.redis.action;


import cn.flood.websocket.WebSocketManager;
import com.alibaba.fastjson2.JSONObject;

/**
 * do nothing action
 * @author xiongshiyan at 2018/10/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class NoActionAction implements Action{

    @Override
    public void doMessage(WebSocketManager manager, JSONObject object) {
        // do no thing
    }
}
