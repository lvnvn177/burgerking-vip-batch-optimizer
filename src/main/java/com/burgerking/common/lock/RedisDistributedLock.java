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
    
    private static final String LOCK_PREFIX = "lock:";

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Redis의 setIfAbsent (SETNX) 명령어를 사용하여 락을 획득합니다.
     *
     * @param key     락을 획득할 키
     * @param timeout 락의 유효 시간 (자동 해제 시간)
     * @return 락 획득 성공 여부
     */
    @Override
    public boolean acquire(String key, Duration timeout) {
        String lockKey = LOCK_PREFIX + key;
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", timeout));
    }

    /**
     * 락을 해제합니다.
     *
     * @param key 락을 해제할 키
     * @return 락 해제 성공 여부
     */
    @Override
    public boolean release(String key) {
        String lockKey = LOCK_PREFIX + key;
        return Boolean.TRUE.equals(redisTemplate.delete(lockKey));
    }
}
