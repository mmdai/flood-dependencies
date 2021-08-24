package cn.flood.websocket.redis;


import cn.flood.Func;
import cn.flood.websocket.WebSocket;
import cn.flood.websocket.memory.MemWebSocketManager;
import cn.flood.websocket.redis.action.Action;
import cn.flood.websocket.redis.action.SendMessageAction;
import cn.flood.websocket.utils.ResponseData;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class RedisWebSocketManager extends MemWebSocketManager {
    public static final String CHANNEL = "websocket";
    protected RedisTemplate<String, Object> redisTemplate;

    public RedisWebSocketManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void put(String identifier, WebSocket webSocket) {
        super.put(identifier, webSocket);
    }

    @Override
    public void remove(String identifier) {
        super.remove(identifier);
    }

    @Override
    public void sendMessage(String userAccount, String message) {
        Map<String, Object> map = new HashMap<>(3);
        map.put(Action.ACTION, SendMessageAction.class.getName());
        map.put(Action.USER_ACCOUNT, userAccount);
        ResponseData responseData = new ResponseData("message", message);
        map.put(Action.MESSAGE, responseData);
        // 发布消息到redis频道上 redis转发到订阅的各个socket实例上 收到信息 根据标识 获取到session 发给自己对应的客户端
        redisTemplate.convertAndSend(getChannel(), Func.toJson(map));
    }


    protected String getChannel() {
        return CHANNEL;
    }
}
