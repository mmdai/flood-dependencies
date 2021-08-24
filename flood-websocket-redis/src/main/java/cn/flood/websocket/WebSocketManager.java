package cn.flood.websocket;

import java.util.List;
import java.util.Map;


public interface WebSocketManager {
    /**
     * 在容器中的名字
     */
    String WEBSOCKET_MANAGER_NAME  = "webSocketManager";
    /**
     * 根据标识获取websocket session
     * @param identifier 标识
     * @return WebSocket
     */
    WebSocket get(String identifier);

    /**
     * 同一用户多点登录时使用
     * @param userAccount 用户账户
     * @return WebSocket
     */
    List<WebSocket> getList(String userAccount);

    /**
     * 放入一个 websocket session
     * @param identifier 标识
     * @param webSocket websocket
     */
    void put(String identifier, WebSocket webSocket);

    /**
     * 删除
     * @param identifier 标识
     */
    void remove(String identifier);

    /**
     * 获取当前机器上的保存的WebSocket
     * @return WebSocket Map
     */
    Map<String , WebSocket> localWebSocketMap();

    /**
     * 统计当前实例在线人数,如果不允许一个账号多次登录,默认实现就可以,如果一个人多次登录,需要重新该方法
     * @return 统计当前实例在线人数
     */
    default int size(){
        return localWebSocketMap().size();
    }

    /**
     * 给某人发送消息
     * @param userAccount 用户账户
     * @param message 消息
     */
    void sendMessage(String userAccount, String message);

    /**
     * 广播
     * @param message 消息
     */
    void broadcast(String message);

    /**
     * WebSocket接收到消息的函数调用
     * @param identifier 标识
     * @param message 消息内容
     */
    void onMessage(String identifier, String message);

    /**
     * 在OnMessage中判断是否是心跳,
     * 从客户端的消息判断是否是ping消息
     * @param identifier 标识
     * @param message 消息
     * @return 是否是ping消息
     */
    default boolean isPing(String identifier, String message){
        return "ping".equalsIgnoreCase(message);
    }

    /**
     * 返回心跳信息
     * @param identifier 标识
     * @param message 消息
     * @return 返回的pong消息
     */
    default String pong(String identifier, String message){
        return "pong";
    }
}
