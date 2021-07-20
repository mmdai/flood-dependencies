-- 添加一个延迟任务
-- 1.将延迟任务存放到key为 {PROJECT}:REDIS_DELAY_TABLE 的 HASH_TABLE中
-- 2.将TOPIC:ID 作为member ;执行时间戳作为 score  放入 {PROJECT}:RD_ZSET_BUCKET: 的ZSET队列中;


local jobs_key_ht = KEYS[1];
local bucket_key_zset = KEYS[2];
local topic_id = ARGV[1];
local content = ARGV[2];
local score = ARGV[3];

redis.call('HSET',jobs_key_ht,topic_id,content) ;

redis.call('ZADD',bucket_key_zset,score,topic_id) ;


