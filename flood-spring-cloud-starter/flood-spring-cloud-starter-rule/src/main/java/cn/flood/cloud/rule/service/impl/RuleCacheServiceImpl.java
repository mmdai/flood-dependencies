package cn.flood.cloud.rule.service.impl;


import cn.flood.Func;
import cn.flood.redis.util.RedisUtil;
import cn.flood.cloud.rule.constant.RuleConstant;
import cn.flood.cloud.rule.entity.BlackList;
import cn.flood.cloud.rule.service.RuleCacheService;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * 规则缓存实现业务类
 * @author pangu
 */
public class RuleCacheServiceImpl implements RuleCacheService {


    @Override
    public Set<Object> getBlackList(String ip) {
        return RedisUtil.getZsetHandler().getAllAsObj(RuleConstant.getBlackListCacheKey(ip));
    }

    @Override
    public Set<Object> getBlackList() {
        return RedisUtil.getZsetHandler().getAllAsObj(RuleConstant.getBlackListCacheKey());
    }

    @Override
    public void setBlackList(BlackList blackList) {
        String key = StringUtils.isNotBlank(blackList.getIp()) ? RuleConstant.getBlackListCacheKey(blackList.getIp())
                : RuleConstant.getBlackListCacheKey();
        RedisUtil.getZsetHandler().add(key, Func.toJson(blackList));
    }

    @Override
    public void deleteBlackList(BlackList blackList) {
        String key = StringUtils.isNotBlank(blackList.getIp()) ? RuleConstant.getBlackListCacheKey(blackList.getIp())
                : RuleConstant.getBlackListCacheKey();
        RedisUtil.getZsetHandler().remove(key, Func.toJson(blackList));
    }
}
