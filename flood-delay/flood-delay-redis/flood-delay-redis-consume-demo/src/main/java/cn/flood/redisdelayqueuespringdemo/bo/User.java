package cn.flood.redisdelayqueuespringdemo.bo;

import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

/**
 * <p>Title: User</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2020</p>
 *
 * @author mmdai
 * @version 1.0
 * @date 2020/12/13
 */
@Data
@ToString
public class User implements Serializable {

  private int id;
  private String name;

  private String content;

//    private LocalDateTime dateTime;
}
