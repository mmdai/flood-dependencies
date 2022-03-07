package cn.flood.jwtp.enums;

import cn.flood.enums.annotation.EnumHandler;

import java.util.stream.Stream;

/**
 * 平台类型
 * @author mmdai
 * @version 1.0
 * @date 2022/2/21 18:27
 */
@EnumHandler
public enum PlatformEnum {

    /**
     * 全平台
     */
    ALL("all", 0),

    /**
     * web
     */
    WEB("web", 1),

    /**
     * app
     */
    APP("app", 2),

    /**
     * 管理平台
     */
    MGR("", 3),

    /**
     * 其他平台
     */
    OTHER("", 4);
    /**
     * 名称
     */
    final String name;
    /**
     * 类型
     */
    final int type;

    // 构造方法
    PlatformEnum(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public static PlatformEnum valueOfEnum(int type) {
        return Stream.of(PlatformEnum.values()).
                filter(enums -> enums.type == type).
                findFirst().orElse(null);
    }

    public static PlatformEnum valueOfEnum(String name) {
        return Stream.of(PlatformEnum.values()).
                filter(enums -> enums.name.equalsIgnoreCase(name)).
                findFirst().orElse(null);
    }
}
