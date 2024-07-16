package cn.flood.db.redis.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * @author mmdai
 * @version 1.0.0
 * @ClassName CacheKey
 * @Description
 * @createTime 2024年07月16日 13:33
 */
@Getter
@ToString
@AllArgsConstructor
public class CacheKey {

    /**
     * redis key
     */
    private final String key;
    /**
     * 超时时间 秒
     */
    @Nullable
    private Duration expire;

    public CacheKey(String key) {
        this.key = key;
    }
}
