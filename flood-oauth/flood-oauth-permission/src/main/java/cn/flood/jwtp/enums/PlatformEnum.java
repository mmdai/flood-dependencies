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
    PLAT_00("plat00", 1000),
    /**
     * 平台01
     */
    PLAT_01("plat01", 1001),
    /**
     * 平台02
     */
    PLAT_02("plat02", 1002),
    /**
     * 平台03
     */
    PLAT_03("plat03", 1003),
    /**
     * 平台04
     */
    PLAT_04("plat04", 1004),
    /**
     * 平台05
     */
    PLAT_05("plat05", 1005),
    /**
     * 平台06
     */
    PLAT_06("plat06", 1006),
    /**
     * 平台07
     */
    PLAT_07("plat07", 1007),
    /**
     * 平台08
     */
    PLAT_08("plat08", 1008),
    /**
     * 平台09
     */
    PLAT_09("plat09", 1009),
    /**
     * 平台10
     */
    PLAT_10("plat10", 1010),
    /**
     * 平台11
     */
    PLAT_11("plat11", 1011),
    /**
     * 平台12
     */
    PLAT_12("plat12", 1012),
    /**
     * 平台13
     */
    PLAT_13("plat13", 1013),
    /**
     * 平台11
     */
    PLAT_14("plat14", 1014),
    /**
     * 平台15
     */
    PLAT_15("plat15", 1015),
    /**
     * 平台16
     */
    PLAT_16("plat16", 1016),
    /**
     * 平台17
     */
    PLAT_17("plat17", 1017),
    /**
     * 平台18
     */
    PLAT_18("plat18", 1018),
    /**
     * 平台19
     */
    PLAT_19("plat19", 1019),
    /**
     * 平台11
     */
    PLAT_20("plat20", 1020);
    /**
     * 名称
     */
    final String code;
    /**
     * 类型
     */
    final int type;

    // 构造方法
    PlatformEnum(String code, int type) {
        this.code = code;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public int getType() {
        return type;
    }

    public static PlatformEnum valueOfEnum(int type) {
        return Stream.of(PlatformEnum.values()).
                filter(enums -> enums.type == type).
                findFirst().orElse(null);
    }

    public static PlatformEnum valueOfEnum(String code) {
        return Stream.of(PlatformEnum.values()).
                filter(enums -> enums.code.equalsIgnoreCase(code)).
                findFirst().orElse(null);
    }
}
