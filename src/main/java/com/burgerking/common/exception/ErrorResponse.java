package com.burgerking.common.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * API 예외 발생 시 클라이언트에게 반환될 응답 형식을 정의하는 클래스입니다.
 */
@Getter
@Builder
public class ErrorResponse {
    /**
     * 예외 발생 시간
     */
    private final LocalDateTime timestamp;
    /**
     * ErrorCode의 이름 (e.g., "MEMBERSHIP_NOT_FOUND")
     */
    private final String error;
    /**
     * 예외 메시지 (사용자에게 보여줄 내용)
     */
    private final String message;
    /**
     * 예외가 발생한 요청 URI
     */
    private final String path;

    /**
     * ErrorCode와 요청 경로를 사용하여 ErrorResponse 객체를 생성합니다.
     *
     * @param errorCode 발생한 에러의 코드
     * @param path      요청 경로
     * @return ErrorResponse
     */
    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(errorCode.name())
                .message(errorCode.getMessage())
                .path(path)
                .build();
    }

    /**
     * 에러 이름, 메시지, 요청 경로를 직접 받아 ErrorResponse 객체를 생성합니다.
     * (주로 @Valid 검증 실패 시 사용)
     *
     * @param errorName 에러 이름
     * @param message   에러 메시지
     * @param path      요청 경로
     * @return ErrorResponse
     */
    public static ErrorResponse of(String errorName, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(errorName)
                .message(message)
                .path(path)
                .build();
    }
}