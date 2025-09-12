package com.burgerking.common.lock;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Redis를 이용한 분산 락 구현체
 */
@Component
@RequiredArgsConstructor
public class RedisDistributedLock implements DistributedLock{
    
    private static final String LOCK_PREFIX = "coupon:lock:";
    
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean acquire(String key, Duration timeout) {
        String lockKey = LOCK_PREFIX + key;
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(
            lockKey,
            "LOCKED",
            timeout
        );
        return Boolean.TRUE.equals(acquired);
    }

    @Override
    public boolean release(String key) {
        String lockKey = LOCK_PREFIX + key;
        return Boolean.TRUE.equals(redisTemplate.delete(lockKey));
    }
}
