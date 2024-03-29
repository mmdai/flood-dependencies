package cn.flood.websocket.memory;


import cn.flood.base.core.Func;
import cn.flood.websocket.WebSocket;
import cn.flood.websocket.WebSocketEvent;
import cn.flood.websocket.WebSocketManager;
import cn.flood.websocket.utils.ResponseData;
import cn.flood.websocket.utils.WebSocketUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MemWebSocketManager implements WebSocketManager, ApplicationContextAware {

  protected final ConnectionTimeOutChecker timeOutChecker = startChecker();
  private final Logger log = LoggerFactory.getLogger(this.getClass());
  /**
   * 因为全局只有一个 WebSocketManager ，所以才敢定义为非static
   */
  private final Map<String, WebSocket> connections = new ConcurrentHashMap<>(100);
  private final Map<String, Set<String>> account2identifys = new ConcurrentHashMap<>(100);
  private ApplicationContext applicationContext;
  /**
   * @param timeSpan 检查到心跳更新时间大于这么毫秒就认为断开了（心跳时间）
   */
  @Value("${webSocket.heartCheck.timeSpan:30000}")
  private long timeSpan;
  /**
   * @param errorTolerant 容忍没有心跳次数
   */
  @Value("${webSocket.heartCheck.errorToleration:5}")
  private int errorToleration;

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  /**
   * 同一用户 单点单点登录时使用
   *
   * @param identifier 标识
   * @return
   */
  @Override
  public WebSocket get(String identifier) {
    return connections.get(identifier);
  }

  @Override
  public List<WebSocket> getList(String userAccount) {
    return getConnectionForUser(userAccount);
  }

  @Override
  public void put(String identifier, WebSocket webSocket) {
    connections.put(identifier, webSocket);
    Set<String> set = account2identifys.get(webSocket.getUserAccount());
    if (Func.isEmpty(set)) {
      set = Collections.synchronizedSet(new HashSet<>());
      account2identifys.put(webSocket.getUserAccount(), set);
    }
    set.add(identifier);
    //发送连接事件
    getApplicationContext()
        .publishEvent(new WebSocketEvent(webSocket, WebSocketEvent.EVENT_TYPE_OPEN));
  }

  @Override
  public void remove(String identifier) {
    WebSocket removedWebSocket = connections.remove(identifier);
    //发送关闭事件
    if (Func.isNotEmpty(removedWebSocket)) {
      account2identifys.get(removedWebSocket.getUserAccount()).remove(identifier);
      getApplicationContext()
          .publishEvent(new WebSocketEvent(removedWebSocket, WebSocketEvent.EVENT_TYPE_CLOSE));
      removedWebSocket.closeSession();
    }
  }


  @Override
  public Map<String, WebSocket> localWebSocketMap() {
    return Collections.unmodifiableMap(connections);
  }

  @Override
  public int size() {
    Map<String, Set<String>> account2cons = Collections.unmodifiableMap(account2identifys);
    int count = (int) account2cons.entrySet().stream().filter(e -> e.getValue().size() > 0).count();
    return count;
  }

  @Override
  public void sendMessage(String userAccount, String message) {
    List<WebSocket> list = getList(userAccount);
    //本地能找到就直接发
    if (Func.isNotEmpty(list)) {
      for (WebSocket webSocket : list) {
        WebSocketUtil.sendMessageAsync(webSocket.getSession(),
            new ResponseData("message", message).toString());
      }
      return;
    } else {
//            不能抛异常,如果用户没有登陆,会在登陆的时候再提示
//            throw new RuntimeException("userAccount 不存在");
    }
  }

  @Override
  public void sendMessage(String userAccount, byte[] bytes) {
    List<WebSocket> list = getList(userAccount);
    //本地能找到就直接发
    if (Func.isNotEmpty(list)) {
      for (WebSocket webSocket : list) {
        WebSocketUtil.sendMessageAsync(webSocket.getSession(),
            new ResponseData("message", bytes).toString());
      }
      return;
    } else {
//            不能抛异常,如果用户没有登陆,会在登陆的时候再提示
//            throw new RuntimeException("userAccount 不存在");
    }
  }

  @Override
  public void broadcast(String message) {
    localWebSocketMap().values().forEach(
        webSocket -> WebSocketUtil.sendMessageAsync(
            webSocket.getSession(), message));
  }

  @Override
  public void broadcast(byte[] bytes) {
    localWebSocketMap().values().forEach(
        webSocket -> WebSocketUtil.sendBytesAsync(
            webSocket.getSession(), bytes));
  }

  @Override
  public void onMessage(String identifier, String message) {
    WebSocket webSocket = connections.get(identifier);
    //发布一下消息事件,让关注该事件的人去处理
    if (null != webSocket) {
      getApplicationContext()
          .publishEvent(new WebSocketEvent(webSocket, WebSocketEvent.EVENT_TYPE_MESSAGE, message));
    }
  }

  @Override
  public void onMessage(String identifier, byte[] bytes) {
    WebSocket webSocket = connections.get(identifier);
    //发布一下消息事件,让关注该事件的人去处理
    if (null != webSocket) {
      getApplicationContext()
          .publishEvent(new WebSocketEvent(webSocket, WebSocketEvent.EVENT_TYPE_MESSAGE, bytes));
    }
  }

  private List<WebSocket> getConnectionForUser(String userAccount) {
    Set<String> set = account2identifys.get(userAccount);
    List<WebSocket> list = new ArrayList<>();
    if (Func.isNotEmpty(set)) {
      set.forEach(identifier -> {
        WebSocket ws = connections.get(identifier);
        if (ws != null) {
          list.add(ws);
        }
      });
    }
    return list;
  }

  protected void checkConnection() {
    Map<String, WebSocket> cons = localWebSocketMap();
    final long timeSpans = timeSpan * errorToleration;
    final long newTime = System.currentTimeMillis();
    cons.forEach((identify, con) -> {
      if (newTime - con.getLastHeart().getTime() > timeSpans) {
        //timeout
        remove(identify);
        log.info("remove websocket by time out:{}", identify);
      }
    });
  }

  protected ConnectionTimeOutChecker startChecker() {
    ConnectionTimeOutChecker checker = new ConnectionTimeOutChecker();
    checker.start();
    return checker;
  }

  private class ConnectionTimeOutChecker extends Thread {

    @Override
    public void run() {
      try {
        sleep(20000);//20秒检查一次
        checkConnection();
      } catch (Exception ex) {
      }
    }
  }
}
