package cn.flood.tools.uid.bean;

import cn.flood.tools.uid.extend.strategy.IUidStrategy;

/**
 *
 */
public class UidSegment {

  /**
   * 空组
   */
  private static final String EMPTY_STR = "";
  /**
   * 基因因子
   */
  public Long factor;
  private IUidStrategy uidStrategy;
  /**
   * 除余基数(建议使用固定值)--控制位移
   */
  private Integer fixed = 25;

  public UidSegment(IUidStrategy uidStrategy) {
    this.uidStrategy = uidStrategy;
  }

  /**
   * @方法名称 getUidStr
   * @功能描述 <pre>获取ID</pre>
   */
  public String getUidStr() {
    return getUidStr(EMPTY_STR);
  }

  /**
   * @param prefix 前缀(有group作用)
   * @方法名称 getUidStr
   * @功能描述 <pre>获取ID</pre>
   */
  public String getUidStr(String prefix) {
    return prefix + getUID(prefix);
  }

  /**
   * @方法名称 getUID
   * @功能描述 <pre>获取ID</pre>
   */
  public long getUID() {
    return getUID(EMPTY_STR);
  }

  /**
   * @param group 分组
   * @方法名称 getUID
   * @功能描述 <pre>获取ID</pre>
   */
  public long getUID(String group) {
    return geneId(uidStrategy.getUID(group));
  }

  /**
   * @param uid
   * @param group 分组
   * @return 输出json字符串：{\"UID\":\"\",\"timestamp\":\"\",\"workerId\":\"\",\"dataCenterId\":\"\",\"sequence\":\"\"}
   * @方法名称 parseUID
   * @功能描述 <pre>解析ID</pre>
   */
  public String parseUID(long uid, String group) {
    return uidStrategy.parseUID(restoreId(uid), group);
  }

  /**
   * @param uid
   * @return 输出json字符串：{\"UID\":\"\",\"timestamp\":\"\",\"workerId\":\"\",\"dataCenterId\":\"\",\"sequence\":\"\"}
   * @方法名称 parseUID
   * @功能描述 <pre>解析ID</pre>
   */
  public String parseUID(long uid) {
    return parseUID(uid, EMPTY_STR);
  }

  /**
   * @param uid
   * @return 输出json字符串：{\"UID\":\"\",\"timestamp\":\"\",\"workerId\":\"\",\"dataCenterId\":\"\",\"sequence\":\"\"}
   * @方法名称 parseUID
   * @功能描述 <pre>解析ID</pre>
   */
  public String parseUID(String uid) {
    return parseUID(uid, EMPTY_STR);
  }

  /**
   * @param uid
   * @param group 分组
   * @return 输出json字符串：{\"UID\":\"\",\"timestamp\":\"\",\"workerId\":\"\",\"dataCenterId\":\"\",\"sequence\":\"\"}
   * @方法名称 parseUID
   * @功能描述 <pre>解析ID</pre>
   */
  public String parseUID(String uid, String group) {
    return parseUID(Long.valueOf(uid.replaceFirst("[^(0-9)]*", "")), group);
  }

  /**
   * 根据基因因子生成基因id
   */
  public Long geneId(Long primitiveId) {
    if (null == factor) {
      return primitiveId;
    }
    int moveBit = Integer.toBinaryString(fixed).length() - 1;
    // 加入factor基因
    return (primitiveId << moveBit) | (factor % fixed);
  }

  /**
   * 还原id
   */
  public long restoreId(long uid) {
    if (null == factor) {
      return uid;
    }
    int leftMoveBit = Integer.toBinaryString(fixed).length() - 1;
    return uid >>> leftMoveBit;
  }

  public IUidStrategy getUidStrategy() {
    return uidStrategy;
  }

  public void setUidStrategy(IUidStrategy uidStrategy) {
    this.uidStrategy = uidStrategy;
  }

  public Integer getFixed() {
    return fixed;
  }

  public void setFixed(Integer fixed) {
    this.fixed = fixed;
  }

  public Long getFactor() {
    return factor;
  }

  public void setFactor(Long factor) {
    this.factor = factor;
  }
}
