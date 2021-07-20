-- 尝试将到达执行时间的延迟任务移动到  待消费列表中
-- 1.判断zset中top元素的score是否大于等于当前时间;
-- 2.如果大于当前时间则 将此元素删除; 然后将此元素的member值存放到 TOPIC_LIST中等待消费;
-- 3.如果小于当前时间的话,不做操作;
-- 4.不管2、3; 最后再获取zset的top元素的score返回
-- 5. 入参 maxCount 为最大搬动次数; maxCount==0表示只读取一个
-- 6.如果没有元素了 则返回null

-- 获取当前时间毫秒  在Lua脚本中调用了随机命令之后禁止再调用写命令;
-- TIME是随机命令; 导致后面不能调用写命令;所以当前时间不在redis中获取,让用户传入
local function getNowTime()

    local a=redis.call('TIME') ;
    return a[1]*1000+a[2]
end

-- 从TOPIC:ID 中解析出 TOPIC
local function getTopicInMember(str)
    local idx =  string.find(str,':');
    local v  = string.sub(str,1,idx-1)
    -- 避免有  "  字符  替换一下v
    return string.gsub(v,'\"','');
end



-- {PROJECT}:TOPIC_LIST:   前缀
local topis_list = KEYS[1];
-- {PROJECT}:RD_ZSET_BUCKET:
local bucket_key_zset = KEYS[2];
-- 当前时间
local nowTime = ARGV[1];
-- 查询元素个数
local checkNo = redis.call('ZCARD',bucket_key_zset)

-- 如果没有元素的话 就直接返回了
if (tonumber(checkNo) > 0)
then
    -- 一次取多个
    local member = redis.call('Zrangebyscore',bucket_key_zset,0,tonumber(nowTime));
    -- 如果空表直接return
    if next(member) == nil
    then
        return
    else
        -- 迭代table   member
        for key,value in ipairs(member)
        do
            local zscore = redis.call('ZSCORE',bucket_key_zset,value);
            if tonumber(nowTime) >= tonumber(zscore)
            then
                -- 从member中解析出 TOPIC出来
                local topic = topis_list..getTopicInMember(value);
                -- 删除  和 put
                redis.call('ZREM',bucket_key_zset,value) ;
                redis.call('RPUSH',topic,value);
            else
                -- 返回当前的时间戳
                return zscore;
            end
        end
        -- 如果maxCount全部迭代完,则查询下一个元素的score返回
        local topmember = redis.call('ZRANGE',bucket_key_zset,0,0);
        local nextvalue = next(topmember);
        if nextvalue == nil
        then return
        else
            for k,v in ipairs(topmember)
            do
                return redis.call('ZSCORE',bucket_key_zset,v);
            end

        end

    end
end
return









