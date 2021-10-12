package cn.flood.mapstruct.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 * @date 2021-06-20 9:36
 * @description
 */
@Data
@Accessors(chain = true)
public class Department {

    private Integer id;

    private String name;
}
