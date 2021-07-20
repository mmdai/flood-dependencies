-- 删除一个延迟任务


local jobs_key_ht = KEYS[1];
local bucket_key_zset = KEYS[2];
local topic_id = ARGV[1];

redis.call('HDEL',jobs_key_ht,topic_id) ;

redis.call('ZREM',bucket_key_zset,topic_id) ;


