package cn.flood.rocketmq.base;

import com.google.gson.Gson;
import cn.flood.rocketmq.annotation.MQKey;
import cn.flood.rocketmq.enums.DelayTimeLevel;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;


@Data
public class MessageBuilder {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static Gson gson = new Gson();

    private static final String[] DELAY_ARRAY = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h".split(" ");

    private String topic;
    private String tag;
    private String key;
    private Object message;
    private Integer delayTimeLevel;

    public static MessageBuilder of(String topic, String tag) {
        MessageBuilder builder = new MessageBuilder();
        builder.setTopic(topic);
        builder.setTag(tag);
        return builder;
    }

    public static MessageBuilder of(Object message) {
        MessageBuilder builder = new MessageBuilder();
        builder.setMessage(message);
        return builder;
    }

    public MessageBuilder topic(String topic) {
        this.topic = topic;
        return this;
    }

    public MessageBuilder tag(String tag) {
        this.tag = tag;
        return this;
    }

    public MessageBuilder key(String key) {
        this.key = key;
        return this;
    }

    public MessageBuilder delayTimeLevel(DelayTimeLevel delayTimeLevel) {
        this.delayTimeLevel = delayTimeLevel.getLevel();
        return this;
    }

    public Message build() {
        StringBuilder messageKey = new StringBuilder(ObjectUtils.isEmpty(key) ? "" : key);
        try {
            Annotation[] annotations = message.getClass().getAnnotations();
//            for (Field field : fields) {
//                Annotation[] allFAnnos= field.getAnnotations();
//                if(allFAnnos.length > 0) {
//                    for (int i = 0; i < allFAnnos.length; i++) {
//                        if(allFAnnos[i].annotationType().equals(MQKey.class)) {
//                            field.setAccessible(true);
//                            MQKey mqKey = MQKey.class.cast(allFAnnos[i]);
//                            messageKey.append(StringUtils.SPACE).append(ObjectUtils.isEmpty(mqKey.prefix()) ? field.get(message).toString() : (mqKey.prefix() + ":" + field.get(message).toString()));
//                        }
//                    }
//                }
//            }
            if (annotations.length > 0) {
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().equals(MQKey.class)) {
                        MQKey mqKey = (MQKey) annotation;
                        Field[] fields = message.getClass().getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            if (field.getName().equals(mqKey.field())) {
                                messageKey.append(ObjectUtils.isEmpty(mqKey.prefix()) ? field.get(message).toString() : (mqKey.prefix() + ":" + field.get(message).toString()));
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("parse key error : {}", e.getMessage());
        }
        String str = gson.toJson(message);
        if (ObjectUtils.isEmpty(topic)) {
            if (ObjectUtils.isEmpty(getTopic())) {
                throw new RuntimeException("no topic defined to send this message");
            }
        }
        Message message = new Message(topic, str.getBytes(StandardCharsets.UTF_8));
        if (!ObjectUtils.isEmpty(tag)) {
            message.setTags(tag);
        }
        if (StringUtils.isNotEmpty(messageKey.toString())) {
            message.setKeys(messageKey.toString());
        }
        if (delayTimeLevel != null && delayTimeLevel > 0 && delayTimeLevel <= DELAY_ARRAY.length) {
            message.setDelayTimeLevel(delayTimeLevel);
        }
        return message;
    }
}
