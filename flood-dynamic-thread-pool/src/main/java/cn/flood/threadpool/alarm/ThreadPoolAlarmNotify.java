package cn.flood.threadpool.alarm;

import cn.flood.threadpool.alarm.AlarmMessage;

/**
 * 线程池告警通知，使用者可实现改接口进行告警方式的扩展
 *
 */
public interface ThreadPoolAlarmNotify {

    /**
     * 告警通知
     * @param alarmMessage
     */
    void alarmNotify(AlarmMessage alarmMessage);

}
