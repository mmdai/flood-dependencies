package cn.flood.delay.redis.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Delay Queue Message
 *
 * @author mmdai
 * @date 2019/11/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message<T extends Serializable> implements Serializable {
	/**
	 * topic
	 */
	private String topic;
	/**
	 * 消息对象
	 */
	private T msg;

	private int retries = 3;

	private transient String msgId;

	private transient int delayTime;

	private transient TimeUnit timeUnit = TimeUnit.SECONDS;

	public Message(String topic, T msg, int delayTime) {
		this.topic = topic;
		this.msg = msg;
		this.delayTime = delayTime;
	}

	@Override
	public String toString() {
		return "(msg=" + msg +
				", topic=" + topic +
				", retries=" + retries +
				", delayTime=" + timeUnit.toSeconds(delayTime) +
				"seconds)";
	}

}
