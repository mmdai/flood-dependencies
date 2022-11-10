package cn.flood.mq.rocketmq.dedup.persist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by mmdai
 */
@AllArgsConstructor
@Getter
@ToString
public class DedupElement {
    private String application = "dedup";
    private String topic;
    private String tag;
    private String msgUniqKey;

}
