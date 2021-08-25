package cn.flood.websocket;

import org.springframework.context.ApplicationEvent;

public class WebSocketEvent extends ApplicationEvent {
    public static final String EVENT_TYPE_OPEN = "open";
    public static final String EVENT_TYPE_CLOSE = "close";
    public static final String EVENT_TYPE_MESSAGE = "message";

    private WebSocket webSocket;
    private String eventType;
    private Object message;

    public WebSocketEvent(WebSocket webSocket, String eventType) {
        super(webSocket);
        this.webSocket = webSocket;
        this.eventType = eventType;
    }

    public WebSocketEvent(WebSocket webSocket, String eventType, Object message) {
        super(webSocket);
        this.webSocket = webSocket;
        this.eventType = eventType;
        this.message = message;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public String getEventType() {
        return eventType;
    }

    public Object getMessage() {
        return message;
    }
}
