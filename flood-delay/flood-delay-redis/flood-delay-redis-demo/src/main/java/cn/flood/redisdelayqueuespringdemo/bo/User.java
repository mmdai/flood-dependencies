package cn.flood.redisdelayqueuespringdemo.bo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

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
public class User {
    private int id;
    private String name;

    private String content;

    private LocalDateTime dateTime;
}
