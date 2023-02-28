package cn.flood.websocket.redis;


import cn.flood.websocket.WebSocketManager;
import cn.flood.websocket.redis.action.ActionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;


@AutoConfiguration
@Import({ActionConfig.class})
public class RedisWebSocketConfig implements ApplicationContextAware {

  private ApplicationContext applicationContext;

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Bean(WebSocketManager.WEBSOCKET_MANAGER_NAME)
  @ConditionalOnMissingBean(name = WebSocketManager.WEBSOCKET_MANAGER_NAME)
  public RedisWebSocketManager webSocketManager(
      @Autowired RedisTemplate<String, Object> redisTemplate) {
    return new RedisWebSocketManager(redisTemplate);
  }

  @Bean(RedisReceiver.REDIS_RECEIVER_NAME)
  public RedisReceiver redisReceiver() {
    return new DefaultRedisReceiver(getApplicationContext());
  }

  @Bean
  public MessageListenerAdapter listenerAdapter(
      @Qualifier(RedisReceiver.REDIS_RECEIVER_NAME) RedisReceiver redisReceiver) {
    return new MessageListenerAdapter(redisReceiver, RedisReceiver.RECEIVER_METHOD_NAME);
  }

  @Bean("redisMessageListenerContainer")
  public RedisMessageListenerContainer redisMessageListenerContainer(
      RedisConnectionFactory connectionFactory,
      MessageListenerAdapter listenerAdapter) {

    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.addMessageListener(listenerAdapter, new PatternTopic(RedisWebSocketManager.CHANNEL));
    return container;
  }
}