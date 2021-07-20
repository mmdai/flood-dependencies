-- 添加重试的延迟任务


local jobs_key_ht = KEYS[1];
local topic_list = KEYS[2];
local topic_id = ARGV[1];
local content = ARGV[2];

redis.call('HSET',jobs_key_ht,topic_id,content) ;

redis.call('RPUSH',topic_list,topic_id) ;


