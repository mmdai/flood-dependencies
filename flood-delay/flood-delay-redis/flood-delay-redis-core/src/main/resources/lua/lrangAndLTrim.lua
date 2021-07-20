-- 获取 最大maxGet个元素返回 并且同时将这些元素删除

local topic_list = KEYS[1];
local maxGet = ARGV[1];
local LLEN = redis.call('LLEN',topic_list) ;

if (tonumber(LLEN) > 0) then

    local topicTable = redis.call('LRANGE',topic_list,0,maxGet) ;
    -- 表长度
    local tableLen = #topicTable;
    -- 删除被取出来的数据
    redis.call('LTRIM',topic_list,tableLen,-1) ;

    return topicTable
end





