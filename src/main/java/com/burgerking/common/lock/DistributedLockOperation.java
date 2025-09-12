package com.burgerking.common.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 분산 락을 적용할 메서드에 사용하는 어노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLockOperation {
    
    /**
     * 락 키 (표현식 사용 가능: #p0, #p1 등으로 메서드 파라미터 참조)
     */
    String key();
    
    /**
     * 락 획득 타임아웃 (밀리초)
     */
    long timeoutMs() default 3000;
}