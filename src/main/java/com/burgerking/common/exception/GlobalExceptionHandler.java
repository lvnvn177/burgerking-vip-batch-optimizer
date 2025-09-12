package com.burgerking.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 모든 BusinessException을 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                e.getErrorCode(),
                request.getRequestURI()
        );
        // 비즈니스 로직 상의 에러이므로 Bad Request (400)로 응답하는 경우가 많습니다.
        // 특정 에러 코드에 따라 다른 HttpStatus를 반환할 수도 있습니다.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // @Valid 검증 실패 시 발생하는 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder builder = new StringBuilder();
        
        bindingResult.getFieldErrors().forEach(fieldError -> {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("] "); // 공백 추가하여 구분
        });

        ErrorResponse response = ErrorResponse.of(
                "VALIDATION_ERROR", // 고정된 에러 이름
                builder.toString().trim(), // 빌더의 마지막 공백 제거
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 그 외 예상치 못한 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e, HttpServletRequest request) {
        // 서버 로그에는 상세한 스택 트레이스를 남기고, 사용자에게는 일반적인 에러 메시지 제공
        // e.g., log.error("Unexpected error occurred", e); 
        ErrorResponse response = ErrorResponse.of(
                "INTERNAL_SERVER_ERROR",
                "서버에 알 수 없는 오류가 발생했습니다.", // 상세한 메시지는 로그에 남기고, 사용자에게는 일반적인 메시지
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}