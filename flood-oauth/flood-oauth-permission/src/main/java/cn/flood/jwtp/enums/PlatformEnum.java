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
    MGR("mgr", 3),

    /**
     * 平台00
     */
    PLAT_00("plat10", 10),
    /**
     * 平台01
     */
    PLAT_01("plat11", 11),
    /**
     * 平台02
     */
    PLAT_02("plat12", 12),
    /**
     * 平台03
     */
    PLAT_03("plat13", 13),
    /**
     * 平台04
     */
    PLAT_04("plat14", 14),
    /**
     * 平台05
     */
    PLAT_05("plat15", 15),
    /**
     * 平台06
     */
    PLAT_06("plat16", 16),
    /**
     * 平台07
     */
    PLAT_07("plat17", 17),
    /**
     * 平台08
     */
    PLAT_08("plat18", 18),
    /**
     * 平台09
     */
    PLAT_09("plat19", 19),
    /**
     * 平台10
     */
    PLAT_10("plat20", 20);
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
