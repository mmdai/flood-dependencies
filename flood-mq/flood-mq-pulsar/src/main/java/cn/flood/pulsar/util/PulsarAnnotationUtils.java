package cn.flood.pulsar.util;

import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.shade.org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class PulsarAnnotationUtils {

    private static final Logger log = LoggerFactory.getLogger(PulsarAnnotationUtils.class);

    public static SubscriptionType getSubscriptionType(Environment environment, String subscriptionTypeName, SubscriptionType subscriptionType) {
        if (StringUtils.isNotEmpty(subscriptionTypeName)) {
            String lastString = null;
            try {
                lastString = environment.resolvePlaceholders(subscriptionTypeName);
            } catch (Exception e) {
                log.error("subscriptionTypeName resolve error, please check value : {}", subscriptionTypeName, e);
            }
            SubscriptionType[] values = SubscriptionType.values();
            for (SubscriptionType type : values) {
                if (type.name().equalsIgnoreCase(lastString)) {
                    return type;
                }
            }
        }
        return subscriptionType;
    }

    public static Class getMsgClass(Environment environment, String msgClassName, Class msgClass) {
        if (StringUtils.isNotEmpty(msgClassName)) {
            String lastString = null;
            try {
                lastString = environment.resolvePlaceholders(msgClassName);
            } catch (Exception e) {
                log.error("subscriptionTypeName resolve error, please check value {}", msgClassName, e);
            }
            try {
                return Class.forName(lastString);
            } catch (ClassNotFoundException e2) {
                log.error("Class {} not found", msgClassName, e2);
            }
        }
        return msgClass;
    }

}
