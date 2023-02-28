package cn.flood.cloud.rule.service;


import cn.flood.cloud.rule.entity.BlackList;
import java.util.Set;

/**
 * 规则缓存业务
 */
public interface RuleCacheService {

  /**
   * 根据IP获取黑名单
   * <p>
   * //	 * @param ip 　ip //	 * @return Set
   */
  Set<Object> getBlackList(String ip);

  /**
   * 查询所有黑名单
   * <p>
   * //	 * @return Set
   */
  Set<Object> getBlackList();

  /**
   * 设置黑名单
   * <p>
   * //	 * @param blackList 黑名单对象
   */
  void setBlackList(BlackList blackList);

  /**
   * 删除黑名单
   * <p>
   * //	 * @param blackList 黑名单对象
   */
  void deleteBlackList(BlackList blackList);
}
