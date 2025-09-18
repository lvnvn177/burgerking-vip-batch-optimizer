package com.burgerking.common.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Redis를 이용한 분산 락 구현체입니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisDistributedLock implements DistributedLock {

    private final StringRedisTemplate redisTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean acquire(String key, Duration timeout) {
        log.debug("Acquiring lock for key: {}, timeout: {}", key, timeout);
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(generateLockKey(key), "locked", timeout);
        boolean result = Boolean.TRUE.equals(success);
        if (result) {
            log.debug("Lock acquired successfully for key: {}", key);
        } else {
            log.debug("Failed to acquire lock for key: {}", key);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean release(String key) {
        log.debug("Releasing lock for key: {}", key);
        Boolean result = redisTemplate.delete(generateLockKey(key));
        return Boolean.TRUE.equals(result);
    }

    private String generateLockKey(String key) {
        return "LOCK:" + key;
    }
}