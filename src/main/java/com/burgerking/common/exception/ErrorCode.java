package com.burgerking.common.exception;

/**
 * 애플리케이션에서 발생하는 모든 에러 코드를 위한 공통 인터페이스입니다.
 * 각 도메인의 에러 코드는 이 인터페이스를 구현해야 합니다.
 */
public interface ErrorCode {
    /**
     * @return 에러 코드의 이름 (e.g., "MEMBERSHIP_NOT_FOUND")
     */
    String name();

    /**
     * @return 사용자에게 보여줄 기본 에러 메시지
     */
    String getMessage();
}