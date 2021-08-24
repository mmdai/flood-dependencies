package cn.flood.websocket.redis.action;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({SendMessageAction.class , RemoveAction.class})
public class ActionConfig {
}