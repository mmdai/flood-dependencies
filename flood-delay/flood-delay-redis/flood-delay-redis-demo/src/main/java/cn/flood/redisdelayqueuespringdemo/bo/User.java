package cn.flood.redisdelayqueuespringdemo.bo;

import lombok.Data;

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
    private String id;

    private Date date;

    private String name;
}
