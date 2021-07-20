-- 获取Job详情
local jobs_key_ht = KEYS[1];
local topic_id = ARGV[1];

return redis.call('HGET',jobs_key_ht,topic_id) ;



