package cn.flood.log.enums;

import java.util.stream.Stream;

/**
 * 操作
 */
public enum ActionEnum {

    ADD(0),//添加
    UPDATE(1),//更新
    DELETE(2),//删除
    DOWN(3),//文件下载
    OTHER(4);//其他


    // 成员变量
    private int index;

    // 构造方法
    ActionEnum(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }



    public static ActionEnum valueOfEnum(int index) {
        return Stream.of(ActionEnum.values()).filter(eu -> eu.index == index).findFirst().orElse(null);
    }
}
