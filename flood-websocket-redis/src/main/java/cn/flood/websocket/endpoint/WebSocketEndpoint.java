package cn.flood.websocket.endpoint;


import cn.flood.websocket.BaseWebSocketEndpoint;
import cn.flood.websocket.User;
import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

@Component
@ServerEndpoint(value = "/ws/{identifier}", configurator = GetHttpSessionConfigurator.class)
public class WebSocketEndpoint extends BaseWebSocketEndpoint {

  @OnOpen
  public void onOpen(Session session, @PathParam(IDENTIFIER) String identifier,
      EndpointConfig config) {
    try {
      HttpSession session1 = (HttpSession) config.getUserProperties()
          .get(HttpSession.class.getName());
      String userAccount = null;
      if (session1 != null) {
        userAccount = ((User) session1.getAttribute("loginUser")).getAccount();
      }
      logger.info("*** WebSocket opened from sessionId " + session.getId() + " , identifier = "
          + identifier);
      connect(userAccount, identifier, session);
    } catch (Exception ex) {
      logger.error("建立webSocket出错:", ex);
    }

  }

  @OnMessage
  public void onMessage(String message, Session session, @PathParam(IDENTIFIER) String identifier) {
    logger.info("接收到的数据为：" + message + " from sessionId " + session.getId() + " , identifier = "
        + identifier);
    receiveMessage(identifier, message, session);
  }

  @OnClose
  public void onClose(Session session, @PathParam(IDENTIFIER) String identifier) {
    logger.info(
        "*** WebSocket closed from sessionId " + session.getId() + " , identifier = " + identifier);
    disconnect(identifier);
  }

  @OnError
  public void onError(Throwable t, @PathParam(IDENTIFIER) String identifier) {
    logger.info("发生异常：, identifier = " + identifier);
    logger.error(t.getMessage(), t);
    disconnect(identifier);
  }
}
