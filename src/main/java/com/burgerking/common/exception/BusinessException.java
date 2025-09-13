package com.burgerking.common.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 상의 예외를 나타내는 최상위 클래스입니다.
 * 모든 커스텀 비즈니스 예외는 이 클래스를 상속받습니다.
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    /**
     * ErrorCode를 받아 예외를 생성합니다.
     *
     * @param errorCode 에러 코드
     */
    protected BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * ErrorCode와 상세 메시지를 받아 예외를 생성합니다.
     *
     * @param errorCode 에러 코드
     * @param message   상세 예외 메시지
     */
    protected BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}