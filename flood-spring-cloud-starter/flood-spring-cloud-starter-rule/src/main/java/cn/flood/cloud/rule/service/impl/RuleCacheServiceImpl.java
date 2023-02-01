package cn.flood.cloud.rule.service.impl;


import cn.flood.base.core.Func;
import cn.flood.cloud.rule.constant.RuleConstant;
import cn.flood.cloud.rule.entity.BlackList;
import cn.flood.cloud.rule.service.RuleCacheService;
import cn.flood.db.redis.service.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * 规则缓存实现业务类
 * @author pangu
 */
@SuppressWarnings("unchecked")
public class RuleCacheServiceImpl implements RuleCacheService {

    @Autowired
    private RedisService redisService;

    public RuleCacheServiceImpl(){
    }


    @Override
    public Set<Object> getBlackList(String ip) {
        return redisService.getAllZSet(RuleConstant.getBlackListCacheKey(ip));
    }

    @Override
    public Set<Object> getBlackList() {
        return redisService.getAllZSet(RuleConstant.getBlackListCacheKey());
    }

    @Override
    public void setBlackList(BlackList blackList) {
        String key = StringUtils.isNotBlank(blackList.getIp()) ? RuleConstant.getBlackListCacheKey(blackList.getIp())
                : RuleConstant.getBlackListCacheKey();
        redisService.addZSet(key, Func.toJson(blackList));
    }

    @Override
    public void deleteBlackList(BlackList blackList) {
        String key = StringUtils.isNotBlank(blackList.getIp()) ? RuleConstant.getBlackListCacheKey(blackList.getIp())
                : RuleConstant.getBlackListCacheKey();
        redisService.removeZSet(key, Func.toJson(blackList));
    }

    public RedisService getRedisService() {
        return redisService;
    }

    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }
}
