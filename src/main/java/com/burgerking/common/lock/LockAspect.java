package com.burgerking.common.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.burgerking.common.exception.LockAcquisitionException;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * @DistributedLock 어노테이션이 적용된 메서드에 분산 락을 적용하는 AOP
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LockAspect {
    
    private final DistributedLock distributedLock;

   @Around("@annotation(com.burgerking.common.lock.DistributedLockOperation)")
   public Object applyLock(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    DistributedLockOperation lockOperation = method.getAnnotation(DistributedLockOperation.class);
    String key = generateKey(lockOperation.key(), joinPoint.getArgs(), method);
    Duration timeout = Duration.ofMillis(lockOperation.timeoutMs());

    log.debug("Trying to acquire lock for key: {}", key);

     if (distributedLock.acquire(key, timeout)) {
            try {
                log.debug("Lock acquired for key: {}", key);
                return joinPoint.proceed();
            } finally {
                distributedLock.release(key);
                log.debug("Lock released for key: {}", key);
            }
        } else {
            log.warn("Failed to acquire lock for key: {}", key);
            throw new LockAcquisitionException("Failed to acquire lock for: " + key);
     }
   }

   private String generateKey(String keyTemplate, Object[] args, Method method) {
        // 키 템플릿에 인자 값을 치환하는 로직
        // 간단한 구현으로 #p0, #p1과 같은 패턴을 파라미터 값으로 치환
        String key = keyTemplate;
        for (int i = 0; i < args.length; i++) {
            key = key.replace("#p" + i, args[i].toString());
        }
        return key;
   }
}
