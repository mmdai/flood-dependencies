package cn.flood.rocketmq;

import cn.flood.exception.CoreException;

/**
 * Created by yipin on 2017/6/28.
 * RocketMQ的自定义异常
 */
public class MQException extends CoreException {
    public MQException(String msg) {
        super(msg);
    }
}
