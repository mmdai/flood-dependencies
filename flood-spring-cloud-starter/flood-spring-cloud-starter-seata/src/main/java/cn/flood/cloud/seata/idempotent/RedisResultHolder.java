package cn.flood.cloud.seata.idempotent;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/5/6 15:43
 */
public class RedisResultHolder implements ResultHolder {

  //seata-tcc 存储key
  private static final String KEY_SEATA_KEY = "seata_tcc:";
  private final RedisTemplate redisTemplate;

  public RedisResultHolder(RedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /**
   * @param actionClass
   * @param xid
   * @param context
   */
  @SuppressWarnings("unchecked")
  @Override
  public void setResult(String actionClass, String xid, String context) {
    HashOperations<String, String, Object> hash = this.redisTemplate.opsForHash();
    hash.put(KEY_SEATA_KEY + actionClass, xid, context);
  }

  /**
   * @param actionClass
   * @param xid
   * @return
   */
  @SuppressWarnings("unchecked")
  @Override
  public boolean getResult(String actionClass, String xid) {
    HashOperations<String, String, Object> hash = this.redisTemplate.opsForHash();
    return hash.hasKey(KEY_SEATA_KEY + actionClass, xid);
  }

  /**
   * @param actionClass
   * @param xid
   */
  @SuppressWarnings("unchecked")
  @Override
  public void removeResult(String actionClass, String xid) {
    HashOperations<String, String, Object> hash = this.redisTemplate.opsForHash();
    hash.delete(KEY_SEATA_KEY + actionClass, xid);
  }
}
