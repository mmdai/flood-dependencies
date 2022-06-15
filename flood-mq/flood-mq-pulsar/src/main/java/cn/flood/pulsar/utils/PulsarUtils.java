package cn.flood.pulsar.utils;

import cn.flood.pulsar.beans.TopicNameComponent;
import cn.flood.pulsar.constants.PulsarConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Pig
 * @date 2020/8/7 11:35 上午
 */
public class PulsarUtils {
    
    public static String getActualTopic(TopicNameComponent topicNameComponent, String value) {
        if(StringUtils.isAnyBlank(topicNameComponent.getTenancy(), topicNameComponent.getNamespace())) {
            throw new RuntimeException("[Pulsar] tenancy and namespace must be not blank");
        }
        return String.format("%s://%s/%s/%s",
                topicNameComponent.isPersistent()? PulsarConstants.PERSISTENT:PulsarConstants.NON_PERSISTENT,
                topicNameComponent.getTenancy(), topicNameComponent.getNamespace(), value);
    }
}
