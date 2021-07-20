package cn.flood.threadpool.alarm;

import lombok.Builder;
import lombok.Data;

/**
 * 告警参数
 *
 */
@Data
@Builder
public class AlarmMessage {

    /**
     * 告警名称，区分唯一性，方便控制告警时间间隔
     */
    private String alarmName;

    /**
     * 告警类型
     */
    private AlarmTypeEnum alarmType;

    /**
     * 告警消息
     */
    private String message;

    /**
     * 钉钉机器人access_token
     */
    private String accessToken;

    /**
     * 钉钉机器人secret
     */
    private String secret;

    /**
     * 对接外部API地址
     */
    private String apiUrl;

    /**
     * 告警时间间隔，单位分钟
     */
    private int alarmTimeInterval = 1;
}
