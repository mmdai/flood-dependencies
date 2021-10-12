package cn.flood.mapstruct.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class StaffEntity {


    private Integer id;

    private String name;

    private String password;

    private Integer sex;

    private String birthday;

    private String salary;

    private Integer status;

    private String staffDescription;

    private Integer departmentId;

    private String createTime;
}
