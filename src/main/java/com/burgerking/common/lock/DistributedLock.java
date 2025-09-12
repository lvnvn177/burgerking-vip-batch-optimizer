package com.burgerking.common.lock;

import java.time.Duration;

/**
 * 분산 락 인터페이스
 * 여러 서버 환경에서 동시성 제어를 위한 락 메커니즘을 정의합니다.
 */

public interface DistributedLock {
    
     /**
     * 락을 획득합니다.
     * @param key 락을 획득할 키 값
     * @param timeout 락 획득 대기 시간
     * @return 락 획득 성공 여부
     */
    boolean acquire(String key, Duration timeout);

     /**
     * 락을 해제합니다.
     * @param key 락을 해제할 키 값
     * @return 락 해제 성공 여부
     */
    boolean release(String key);

    /**
     * 락을 획득한 후 작업을 수행하고 자동으로 락을 해제합니다.
     * @param key 락을 획득할 키 값
     * @param timeout 락 획득 대기 시간
     * @param runnable 락 획득 후 실행할 작업
     * @return 락 획득 및 작업 실행 성공 여부
     */
    default boolean executeWithLock(String key, Duration timeout, Runnable runnable) {
        try {
            if (!acquire(key, timeout)) {
                return false;
            }
            runnable.run();
            return true;
        } finally {
            release(key);
        }
    }
}
