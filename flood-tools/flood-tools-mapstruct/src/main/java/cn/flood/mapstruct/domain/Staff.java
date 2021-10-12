package cn.flood.mapstruct.domain;

import cn.flood.mapstruct.enums.StatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class Staff {

    private Integer id;

    private String name;

    private String password;

    private Integer sex;

    private Date birthday;

    private Double salary;

    private StatusEnum status;

    private String description;

    private Department department;

    private Date createTime;
}
