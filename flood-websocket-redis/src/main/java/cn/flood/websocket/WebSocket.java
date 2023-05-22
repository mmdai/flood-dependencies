package cn.flood.websocket;

import java.io.IOException;
import java.util.Date;
import jakarta.websocket.Session;
import lombok.Data;

@Data
public class WebSocket {

  /**
   * 代表一个连接
   */
  private Session session;
  /**
   * 用户账号,一个用户可以在多个地方登陆
   */
  private String userAccount;

  /**
   * 唯一标识
   */
  private String identifier;
  /**
   * 最后心跳时间
   */
  private Date lastHeart;

  public void closeSession() {
    if (session != null) {
      try {
        session.close();
      } catch (IOException e) {
      }
    }
  }
}
