package cn.flood.cloud.rule.service.impl;


import cn.flood.base.core.Func;
import cn.flood.cloud.rule.constant.RuleConstant;
import cn.flood.cloud.rule.entity.BlackList;
import cn.flood.cloud.rule.service.RuleCacheService;
import cn.flood.db.redis.cache.FloodRedis;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 * 规则缓存实现业务类
 *
 * @author pangu
 */
@SuppressWarnings("unchecked")
public class RuleCacheServiceImpl implements RuleCacheService {


  private FloodRedis floodRedis;

  public RuleCacheServiceImpl(FloodRedis floodRedis) {
    this.floodRedis = floodRedis;
  }


  @Override
  public Set<Object> getBlackList(String ip) {
    return floodRedis.getAllZSet(RuleConstant.getBlackListCacheKey(ip));
  }

  @Override
  public Set<Object> getBlackList() {
    return floodRedis.getAllZSet(RuleConstant.getBlackListCacheKey());
  }

  @Override
  public void setBlackList(BlackList blackList) {
    String key = StringUtils.isNotBlank(blackList.getIp()) ? RuleConstant
        .getBlackListCacheKey(blackList.getIp())
        : RuleConstant.getBlackListCacheKey();
    floodRedis.addZSet(key, Func.toJson(blackList));
  }

  @Override
  public void deleteBlackList(BlackList blackList) {
    String key = StringUtils.isNotBlank(blackList.getIp()) ? RuleConstant
        .getBlackListCacheKey(blackList.getIp())
        : RuleConstant.getBlackListCacheKey();
    floodRedis.removeZSet(key, Func.toJson(blackList));
  }

}
