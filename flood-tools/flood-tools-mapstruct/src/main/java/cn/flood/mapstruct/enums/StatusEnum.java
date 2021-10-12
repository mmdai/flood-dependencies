package cn.flood.mapstruct.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Administrator
 */

@AllArgsConstructor
@Getter
public enum StatusEnum {


    /**
     * 状态枚举
     */
    ENABLED(1, "启用"),
    DISABLED(0, "停用");

    private Integer value;

    private String name;


    public static StatusEnum getByValue(Integer value){
        return Arrays.stream(StatusEnum.values()).filter(statusEnum -> statusEnum.getValue().equals(value)).findFirst().orElse(null);
    }
}
