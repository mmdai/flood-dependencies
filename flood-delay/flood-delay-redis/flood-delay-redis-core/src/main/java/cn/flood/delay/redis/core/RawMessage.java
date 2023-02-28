package cn.flood.delay.redis.core;

import java.io.Serializable;
import lombok.Data;

/**
 * RawMessage
 *
 * @author mmdai
 * @date 2019/11/25
 */
@Data
public class RawMessage implements Serializable {

  private String key;
  private String topic;
  private String msg;

  private int maxRetries;
  private int hasRetries;

  private long executeTime;
  private long createTime;

  public void addHasRetries() {
    this.hasRetries++;
  }

}
