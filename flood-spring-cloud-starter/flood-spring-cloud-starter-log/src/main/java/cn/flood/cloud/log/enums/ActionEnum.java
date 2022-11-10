package cn.flood.cloud.log.enums;

import cn.flood.base.core.enums.annotation.EnumHandler;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 操作
 */
@EnumHandler
@Getter
public enum ActionEnum {

    ADD("0", "添加"),//添加
    UPDATE("1", "更新"),//更新
    DELETE("2", "删除"),//删除
    DOWN("3", "文件下载"),//文件下载
    OTHER("4", "其他");//其他


    // 成员变量
    private String code;

    private String name;

    ActionEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据code获取枚举
     */
    public static ActionEnum codeToEnum(String code) {
        return Stream.of(ActionEnum.values()).filter(eu -> eu.code.equals(code)).findFirst().orElse(null);
    }

    /**
     * 编码转化成中文含义
     */
    public static String codeToName(String code) {
        return Stream.of(ActionEnum.values()).filter(eu -> eu.code.equals(code)).findFirst().get().name;
    }

}
