package cn.flood.websocket.redis;

import cn.flood.Func;
import cn.flood.websocket.WebSocket;
import cn.flood.websocket.memory.MemWebSocketManager;
import cn.flood.websocket.redis.action.Action;
import cn.flood.websocket.redis.action.BroadCastAction;
import cn.flood.websocket.redis.action.RemoveAction;
import cn.flood.websocket.redis.action.SendMessageAction;
import cn.flood.websocket.utils.ResponseData;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.Map;


public class RedisWebSocketManager extends MemWebSocketManager {
    public static final String CHANNEL = "websocket";

    private static final String COUNT_KEY = "RedisWebSocketManagerCountKey";

    protected RedisTemplate<String, Object> redisTemplate;

    public RedisWebSocketManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void put(String identifier, WebSocket webSocket) {
        super.put(identifier, webSocket);
        //在线数量加1
        countChange(1);
    }

    @Override
    public void remove(String identifier) {
        boolean containsKey = localWebSocketMap().containsKey(identifier);
        if(containsKey){
            super.remove(identifier);
        }else {
            Map<String , Object> map = new HashMap<>(2);
            map.put(Action.ACTION , RemoveAction.class.getName());
            map.put(Action.IDENTIFIER , identifier);
            //在websocket频道上发布发送消息的消息
            redisTemplate.convertAndSend(getChannel() , Func.toJson(map));
        }
        //在线数量减1
        countChange(-1);
    }

    @Override
    public void sendMessage(String userAccount, String message) {
//        super.sendMessage(userAccount, message);
        Map<String, Object> map = new HashMap<>(3);
        map.put(Action.ACTION, SendMessageAction.class.getName());
        map.put(Action.USER_ACCOUNT, userAccount);
        ResponseData responseData = new ResponseData("message", message);
        map.put(Action.MESSAGE, responseData);
        // 发布消息到redis频道上 redis转发到订阅的各个socket实例上 收到信息 根据标识 获取到session 发给自己对应的客户端
        redisTemplate.convertAndSend(getChannel(), Func.toJson(map));
    }

    @Override
    public void broadcast(String message) {
        Map<String , Object> map = new HashMap<>(2);
        map.put(Action.ACTION , BroadCastAction.class.getName());
        map.put(Action.MESSAGE , message);
        //在websocket频道上发布广播的消息
        redisTemplate.convertAndSend(getChannel() , Func.toJson(map));
    }

    @Override
    public int size() {
        return getCount();
    }


    protected String getChannel() {
        return CHANNEL;
    }


    /**
     * 增减在线数量
     */
    private void countChange(int delta){
        ValueOperations<String, Object> value = redisTemplate.opsForValue();

        //获取在线当前数量
        int count = getCount(value);

        count = count + delta;
        count = count > 0 ? count : 0;

        //设置新的数量
        value.set(COUNT_KEY , "" + count);
    }

    /**
     * 获取当前在线数量
     */
    private int getCount(){
        ValueOperations<String, Object> value = redisTemplate.opsForValue();
        return getCount(value);
    }
    private int getCount(ValueOperations<String, Object> value) {
        Object countStr = value.get(COUNT_KEY);
        int count = 0;
        if(null != countStr){
            count = Integer.parseInt(countStr.toString());
        }
        return count;
    }
}
