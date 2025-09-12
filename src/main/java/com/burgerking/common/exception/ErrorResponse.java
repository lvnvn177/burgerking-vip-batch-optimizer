package com.burgerking.common.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final String error; // ErrorCode의 이름 (String)
    private final String message; // ErrorCode의 메시지 또는 상세 메시지
    private final String path;

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(errorCode.name())
                .message(errorCode.getMessage())
                .path(path)
                .build();
    }

    public static ErrorResponse of(String errorName, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(errorName)
                .message(message)
                .path(path)
                .build();
    }
}