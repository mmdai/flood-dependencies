package cn.flood.threadpool.alarm;

/**
 * 告警类型
 *
 */
public enum AlarmTypeEnum {
    /**
     * 钉钉
     */
    DING_TALK("DingTalk"),
    /**
     * 外部系统
     */
    EXTERNAL_SYSTEM("ExternalSystem");

    AlarmTypeEnum(String type) {
        this.type = type;
    };

    private String type;

    public String getType() {
        return type;
    }
}
