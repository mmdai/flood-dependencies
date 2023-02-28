package cn.flood.websocket.redis.action;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;


@AutoConfiguration
@Import({SendMessageAction.class, BroadCastAction.class, RemoveAction.class, NoActionAction.class})
public class ActionConfig {

}