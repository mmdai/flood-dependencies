package cn.flood.websocket;



import cn.flood.Func;
import cn.flood.context.SpringContextManager;
import cn.flood.websocket.utils.WebSocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.Date;


public abstract class BaseWebSocketEndpoint {
    /**
     * 路径标识：目前使用token来代表
     */
    public static final String IDENTIFIER = "identifier";
    protected static final Logger logger = LoggerFactory.getLogger(BaseWebSocketEndpoint.class);

    public void connect(String userAccount, String identifier, Session session) {
        try {
            if (Func.isBlank(identifier)) {
                return;
            }

            WebSocketManager websocketManager = getWebSocketManager();

            WebSocket webSocket = new WebSocket();
            webSocket.setIdentifier(identifier);
            webSocket.setSession(session);
            webSocket.setUserAccount(userAccount);
            webSocket.setLastHeart(new Date());
            websocketManager.put(identifier, webSocket);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void disconnect(String identifier) {
        getWebSocketManager().remove(identifier);
    }

    public void receiveMessage(String identifier, String message, Session session) {
        WebSocketManager webSocketManager = getWebSocketManager();
        //心跳监测
        if (webSocketManager.isPing(identifier, message)) {
            String pong = webSocketManager.pong(identifier, message);
            WebSocketUtil.sendMessageAsync(session, pong);
            WebSocket webSocket = webSocketManager.get(identifier);
            //更新心跳时间
            if (null != webSocket) {
                webSocket.setLastHeart(new Date());
            }
            return;
        }
        //收到其他消息的时候
        webSocketManager.onMessage(identifier, message);
    }

    protected WebSocketManager getWebSocketManager() {
        return (WebSocketManager) SpringContextManager.getBean(WebSocketManager.WEBSOCKET_MANAGER_NAME);
    }
}
