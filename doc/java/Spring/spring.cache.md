# 官方文档

https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.caching

https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache

# 开启功能

@EnableCaching

# 声明

```java
package org.springframework.cache.annotation;

public @interface Cacheable {
    /**
     * 缓存组名
     */
    String[] cacheNames() default {};
    /**
     * key生成，keyGenerator的生成方法 or key指定的SpEL模版
     */
    String key() default "";
    String keyGenerator() default "";

    /**
     * 缓存处理器
     */
    String cacheManager() default "";
    String cacheResolver() default "";

    String condition() default "";
    String unless() default "";
    boolean sync() default false;
}
```

# Key Generation

```java
package org.springframework.cache.interceptor;

/**
 * 选择
 */
public abstract class CacheAspectSupport {
    protected Object generateKey(@Nullable Object result) {
        // 配置了key，用SpEL
        if (StringUtils.hasText(this.metadata.operation.getKey())) {
            EvaluationContext evaluationContext = createEvaluationContext(result);
            return evaluator.key(this.metadata.operation.getKey(), this.metadata.methodKey, evaluationContext);
        }
        // 没有配置key，用keyGenerator
        return this.metadata.keyGenerator.generate(this.target, this.metadata.method, this.args);
    }
}

/**
 * 默认的keyGenerator
 */

public interface KeyGenerator {
}

public class SimpleKeyGenerator implements KeyGenerator {
    public static Object generateKey(Object... params) {
        // 无参数
		if (params.length == 0) {
			return SimpleKey.EMPTY;
		}
        // 1个参数
		if (params.length == 1) {
			Object param = params[0];
			if (param != null && !param.getClass().isArray()) {
                // 参数本身，没有类名
				return param;
			}
		}
        // 2个以上参数
		return new SimpleKey(params);
	}
}

public class SimpleKey {
    @Override
	public String toString() {
        // 无参数 or 2个以上参数，类名+所有参数 串联
		return getClass().getSimpleName() + " [" + StringUtils.arrayToCommaDelimitedString(this.params) + "]";
	}
}
```

# Cache Resolution

```java
package org.springframework.cache.interceptor;

public interface CacheResolver {
}

// 默认，使用cacheManager
public class SimpleCacheResolver {
}

public interface CacheManager {
}

// 默认，使用JDK的ConcurrentHashMap
public class ConcurrentMapCacheManager {
}
```

# 是否同步

sync

# 条件缓存

condition

unless

# 支持SpEL

# 源码

org.springframework.cache.interceptor.CacheInterceptor 入口

```java
package org.springframework.cache.interceptor;

public abstract class CacheAspectSupport {
    private Object execute(final CacheOperationInvoker invoker, Method method, CacheOperationContexts contexts) {
        // Special handling of synchronized invocation
		if (contexts.isSynchronized()) {
			CacheOperationContext context = contexts.get(CacheableOperation.class).iterator().next();
			if (isConditionPassing(context, CacheOperationExpressionEvaluator.NO_RESULT)) {
				Object key = generateKey(context, CacheOperationExpressionEvaluator.NO_RESULT);
				Cache cache = context.getCaches().iterator().next();
				try {
					return wrapCacheValue(method, cache.get(key, () -> unwrapReturnValue(invokeOperation(invoker))));
				}
				catch (Cache.ValueRetrievalException ex) {
					// The invoker wraps any Throwable in a ThrowableWrapper instance so we
					// can just make sure that one bubbles up the stack.
					throw (CacheOperationInvoker.ThrowableWrapper) ex.getCause();
				}
			}
			else {
				// No caching required, only call the underlying method
				return invokeOperation(invoker);
			}
		}


		// Process any early evictions
		processCacheEvicts(contexts.get(CacheEvictOperation.class), true,
				CacheOperationExpressionEvaluator.NO_RESULT);

		// 获取之前缓存的结果
		Cache.ValueWrapper cacheHit = findCachedItem(contexts.get(CacheableOperation.class));

		// Collect puts from any @Cacheable miss, if no cached item is found
		List<CachePutRequest> cachePutRequests = new LinkedList<>();
		if (cacheHit == null) {
			collectPutRequests(contexts.get(CacheableOperation.class),
					CacheOperationExpressionEvaluator.NO_RESULT, cachePutRequests);
		}

		Object cacheValue;
		Object returnValue;

		if (cacheHit != null && !hasCachePut(contexts)) {
			// If there are no put requests, just use the cache hit
			cacheValue = cacheHit.get();
			returnValue = wrapCacheValue(method, cacheValue);
		}
		else {
			// Invoke the method if we don't have a cache hit
			returnValue = invokeOperation(invoker);
			cacheValue = unwrapReturnValue(returnValue);
		}

		// Collect any explicit @CachePuts
		collectPutRequests(contexts.get(CachePutOperation.class), cacheValue, cachePutRequests);

		// Process any collected put requests, either from @CachePut or a @Cacheable miss
		for (CachePutRequest cachePutRequest : cachePutRequests) {
			cachePutRequest.apply(cacheValue);
		}

		// Process any late evictions
		processCacheEvicts(contexts.get(CacheEvictOperation.class), false, cacheValue);

		return returnValue;
    }
}
```


# redis

```xml
<!-- 配置redis服务器地址后，会自动初始化一个RedisCacheManager -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

通过key-value方式，cacheNames和key前缀都拼装在key种，而不是hash结构

@CacheEvict的allEntries是通过*查询实现的
