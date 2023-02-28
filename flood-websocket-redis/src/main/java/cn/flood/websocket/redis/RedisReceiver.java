package cn.flood.websocket.redis;


public interface RedisReceiver {

  String RECEIVER_METHOD_NAME = "receiveMessage";
  String REDIS_RECEIVER_NAME = "redisReceiver";

  /**
   * 回调方法
   *
   * @param message 接收到的消息
   */
  void receiveMessage(String message);
}
