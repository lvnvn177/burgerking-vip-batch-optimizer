package com.burgerking.common.exception;

public interface ErrorCode {
    String name(); // 에러 코드의 이름 (e.g., MEMBERSHIP_NOT_FOUND)
    String getMessage(); // 사용자에게 보여줄 메시지
}