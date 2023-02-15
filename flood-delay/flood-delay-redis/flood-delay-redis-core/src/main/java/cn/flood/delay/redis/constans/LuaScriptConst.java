package cn.flood.delay.redis.constans;

/**
 * LuaScriptConst
 *
 * @author mmdai
 * @date 2019/11/25
 */
public interface LuaScriptConst {
	/**
	 * 添加一个延时任务
	 * 1.姜延时任务存放到key为delay-queue-hash 的hash_table中
	 * 2.将topic：id作为member；执行时间戳作为score放入delay-queue-keys的zset队列中
	 */
	String PUSH_MESSAGE = "" +
			"local hash_key = KEYS[1]\n" +
			"local zset_key = KEYS[2]\n" +
			"local id_ = KEYS[3]\n" +
			"local hash_value = ARGV[1]\n" +
			"local zset_score = ARGV[2]\n" +
			"if redis.call('ZADD', zset_key, zset_score, id_) == 1 then\n" +
			"   redis.call('HSET', hash_key, id_, hash_value)\n" +
			"   return 1;\n" +
			"else\n" +
			"   return 0;\n" +
			"end";

	String TRANSFER_MESSAGE = "" +
			"local from_key = KEYS[1]\n" +
			"local to_key = KEYS[2]\n" +
			"local id_ = KEYS[3]\n" +
			"local zset_score = ARGV[1]\n" +
			"if redis.call('ZREM', from_key, id_) == 1 then\n" +
			"   redis.call('ZADD', to_key, zset_score, id_)\n" +
			"	return 1;\n" +
			"else\n" +
			"	return 0;\n" +
			"end";
	/**
	 * 删除一个延时任务
	 */
	String DEL_MESSAGE = "" +
			"local hash_key = KEYS[1]\n" +
			"local zset_key = KEYS[2]\n" +
			"local topic_id = ARGV[1]\n" +
			"if redis.call('HDEL', hash_key, topic_id) == 1 then\n" +
			"   redis.call('ZREM', zset_key, topic_id)\n" +
			"	return 1;\n" +
			"else\n" +
			"	return 0;\n" +
			"end";
}