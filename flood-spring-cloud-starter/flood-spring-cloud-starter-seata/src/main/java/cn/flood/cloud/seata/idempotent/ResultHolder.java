package cn.flood.cloud.seata.idempotent;

/**
 * 用于保存事务状态标识，防止防止幂等性问题导致的重复执行commit或者rollback
 *
 * @author mmdai
 * @version 1.0
 * @date 2022/5/6 15:28
 */
public interface ResultHolder {

    /**
     *
     * @param actionClass
     * @param xid
     * @param context
     */
    void setResult(String actionClass, String xid, String context);

    /**
     *
     * @param actionClass
     * @param xid
     * @return
     */
    boolean getResult(String actionClass, String xid);

    /**
     *
     * @param actionClass
     * @param xid
     */
    void removeResult(String actionClass, String xid);

}
