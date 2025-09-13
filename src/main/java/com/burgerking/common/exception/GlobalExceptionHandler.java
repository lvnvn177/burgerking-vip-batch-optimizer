package com.burgerking.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 애플리케이션 전역에서 발생하는 예외를 처리하는 클래스입니다.
 * (@RestControllerAdvice를 통해 모든 @RestController에서 발생하는 예외를 가로챕니다.)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 예외(BusinessException)를 처리합니다.
     *
     * @param e       발생한 BusinessException
     * @param request HTTP 요청 정보
     * @return ErrorResponse를 포함한 ResponseEntity
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(e.getErrorCode(), request.getRequestURI());
        // 대부분의 비즈니스 예외는 Bad Request (400)로 처리합니다.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * @Valid 어노테이션을 사용한 DTO의 유효성 검증 실패 시 발생하는 예외를 처리합니다.
     *
     * @param e       발생한 MethodArgumentNotValidException
     * @param request HTTP 요청 정보
     * @return ErrorResponse를 포함한 ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder builder = new StringBuilder();

        bindingResult.getFieldErrors().forEach(fieldError -> {
            builder.append("[")
                   .append(fieldError.getField())
                   .append("](은)는 ")
                   .append(fieldError.getDefaultMessage())
                   .append(". 입력된 값: [")
                   .append(fieldError.getRejectedValue())
                   .append("] ");
        });

        ErrorResponse response = ErrorResponse.of("VALIDATION_ERROR", builder.toString().trim(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 위에서 처리되지 않은 모든 예상치 못한 예외(Exception)를 처리합니다.
     *
     * @param e       발생한 Exception
     * @param request HTTP 요청 정보
     * @return ErrorResponse를 포함한 ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e, HttpServletRequest request) {
        // 실제 운영 환경에서는 e.printStackTrace() 대신 log.error() 등을 사용하여 로그를 남겨야 합니다.
        ErrorResponse response = ErrorResponse.of("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

