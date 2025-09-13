package com.burgerking.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 전반의 메서드 실행 시간을 로깅하기 위한 AOP Aspect 입니다.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * com.burgerking 패키지 내의 모든 메서드(AOP 관련 로직 제외) 실행 시 시작, 종료, 예외 발생을 로깅합니다.
     *
     * @param joinPoint 프록시된 메서드에 대한 정보
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("within(com.burgerking..*) && !within(com.burgerking.common.aop..*)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        String className = joinPoint.getSignature().getDeclaringTypeName();

        log.info("[START] {}.{}()", className, methodName);

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.info("[END] {}.{}() - 실행 시간: {}ms", className, methodName, (endTime - startTime));
            return result;
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            log.error("[EXCEPTION] {}.{}() - 예외: {} - 실행 시간: {}ms", className, methodName, e.getMessage(), (endTime - startTime));
            throw e;
        }
    }
}