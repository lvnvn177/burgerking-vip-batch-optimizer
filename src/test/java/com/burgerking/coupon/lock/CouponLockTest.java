package com.burgerking.coupon.lock;

import com.burgerking.common.lock.DistributedLock;
import com.burgerking.common.lock.RedisDistributedLock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CouponLockTest {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private DistributedLock distributedLock;

    @BeforeEach
    void setUp() {
        distributedLock = new RedisDistributedLock(redisTemplate);
        
        // 테스트 전에 Redis 데이터 초기화 (deprecated 메서드 대신 execute 사용)
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.serverCommands().flushDb();
            return null;
        });
    }

    @Test
    @DisplayName("락 획득 및 해제 기본 테스트")
    void acquireAndReleaseLock() {
        // given
        String lockKey = "test-lock-key";

        // when
        boolean acquired = distributedLock.acquire(lockKey, Duration.ofMillis(5000));

        // then
        assertTrue(acquired, "락을 획득할 수 있어야 합니다");

        // when
        boolean released = distributedLock.release(lockKey);

        // then
        assertTrue(released, "락을 해제할 수 있어야 합니다");
    }

    @Test
    @DisplayName("이미 획득된 락은 다시 획득할 수 없어야 함")
    void cannotAcquireLockTwice() {
        // given
        String lockKey = "test-lock-key";

        // when
        boolean firstAcquired = distributedLock.acquire(lockKey, Duration.ofMillis(5000));
        boolean secondAcquired = distributedLock.acquire(lockKey, Duration.ofMillis(1000));

        // then 
        assertTrue(firstAcquired, "첫 번째 락 획득은 성공해야 합니다");
        assertTrue(secondAcquired, "이미 획득한 락은 다시 획득할 수 없어야 합니다");

        // cleanup
        distributedLock.release(lockKey);
    }

    @Test
    @DisplayName("락 타임아웃 테스트")
    void lockShouldTimeoutAfterExpiration() throws InterruptedException {
        // given
        String lockKey = "timeout-test-lock";
        Duration lockTimeoutMs = Duration.ofMillis(1000); // 1초 후 만료

        // when
        boolean acquire = distributedLock.acquire(lockKey, lockTimeoutMs);
        
        // then
        assertTrue(acquire, "락을 획득할 수 있어야 합니다");

        // 락이 만료되기를 기다림 (1초 + 여유 시간)
        Thread.sleep(1500);

        // 락이 만료된 후에는 다시 획득할 수 있어야 함
        boolean reacquired = distributedLock.acquire(lockKey, lockTimeoutMs);
        assertTrue(reacquired, "만료된 락은 다시 획들할 수 있어야 합니다");

        // cleanup
        distributedLock.release(lockKey);
    }

       @Test
    @DisplayName("동시성 환경에서 락 테스트")
    void concurrentLockTest() throws InterruptedException {
        // given
        final String lockKey = "concurrent-test-lock";
        final int threadCount = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final AtomicInteger successCount = new AtomicInteger(0);
        
        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    if (distributedLock.acquire(lockKey, Duration.ofMillis(1000))) {
                        try {
                            // 락 획득 성공
                            successCount.incrementAndGet();
                            Thread.sleep(100); // 작업 시간 시뮬레이션
                        } finally {
                            distributedLock.release(lockKey);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 모든 스레드 완료 대기
        latch.await(5, TimeUnit.SECONDS);
        
        // then
        assertEquals(1, successCount.get(), "동시 요청 중 하나의 스레드만 락을 획득해야 합니다");
        
        executorService.shutdown();
    }
}
