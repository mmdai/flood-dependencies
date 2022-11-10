package cn.flood.db.redis.idempotent.autoconfigure;

import cn.flood.db.redis.idempotent.autoconfigure.aspect.IdempotentAspect;
import cn.flood.db.redis.idempotent.autoconfigure.expression.ExpressionResolver;
import cn.flood.db.redis.idempotent.autoconfigure.expression.KeyResolver;
import cn.flood.db.redis.lock.autoconfigure.RlockAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author daimm
 * @date 2020/9/25
 * <p>
 * 幂等插件初始化
 */
@SuppressWarnings("unchecked")
@AutoConfiguration
@AutoConfigureAfter(RlockAutoConfiguration.class)
public class IdempotentAutoConfiguration {

	/**
	 * 切面 拦截处理所有 @Idempotent
	 * @return Aspect
	 */
	@Bean
	public IdempotentAspect idempotentAspect() {
		return new IdempotentAspect();
	}

	/**
	 * key 解析器
	 * @return KeyResolver
	 */
	@Bean
	@ConditionalOnMissingBean(KeyResolver.class)
	public KeyResolver keyResolver() {
		return new ExpressionResolver();
	}

}
