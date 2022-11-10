package cn.flood.mq.rocketmq.dedup.persist;

/**
 * Created by mmdai
 */
public interface IPersist {

     String CONSUME_STATUS_CONSUMING = "CONSUMING";
     String CONSUME_STATUS_CONSUMED = "CONSUMED";


    boolean setConsumingIfNX(DedupElement dedupElement, long dedupProcessingExpireMilliSeconds);

    void delete(DedupElement dedupElement);

    void markConsumed(DedupElement dedupElement, long dedupRecordReserveMinutes);

    String get(DedupElement dedupElement);

    default String toPrintInfo(DedupElement dedupElement) {
        return dedupElement.toString();
    }
}
