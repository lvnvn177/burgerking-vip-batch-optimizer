package com.burgerking.coupon.web.exception;

import com.burgerking.coupon.web.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리 - 도메인 로직에서 발생하는 예외
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("비즈니스 예외 발생: {}", e.getMessage(), e);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                e.getStatusCode().value(),
                e.getErrorCode(),
                e.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
    }
    
    /**
     * 쿠폰 관련 예외 처리
     */
    @ExceptionHandler(CouponException.class)
    public ResponseEntity<ErrorResponse> handleCouponException(CouponException e, HttpServletRequest request) {
        log.error("쿠폰 예외 발생: {}", e.getMessage(), e);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                e.getStatusCode().value(),
                e.getErrorCode(),
                e.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
    }
    
    /**
     * 입력값 검증 예외 처리 (@Valid 어노테이션 관련)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.error("유효성 검증 실패: {}", errors);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_INPUT",
                "입력값 검증에 실패했습니다: " + errors,
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * 타입 변환 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        
        log.error("타입 변환 실패: {}", e.getMessage(), e);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "TYPE_MISMATCH",
                "요청 파라미터 타입이 올바르지 않습니다: " + e.getName() + "=" + e.getValue(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * 낙관적 락 예외 처리
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(
            OptimisticLockingFailureException e, HttpServletRequest request) {
        
        log.error("낙관적 락 충돌 발생: {}", e.getMessage(), e);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                "OPTIMISTIC_LOCK_FAILURE",
                "데이터가 다른 요청에 의해 변경되었습니다. 다시 시도해주세요.",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    /**
     * 데이터 무결성 위반 예외 처리 (중복 키 등)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException e, HttpServletRequest request) {
        
        log.error("데이터 무결성 위반: {}", e.getMessage(), e);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                "DATA_INTEGRITY_VIOLATION",
                "데이터 제약 조건 위반이 발생했습니다. 중복된 데이터가 있는지 확인해주세요.",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    /**
     * ResponseStatusException 처리
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException e, HttpServletRequest request) {
        
        log.error("HTTP 상태 예외: {}", e.getMessage(), e);
        
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
        
        ErrorResponse errorResponse = ErrorResponse.of(
                status.value(),
                status.name(),
                e.getReason(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }
    
    /**
     * 일반적인 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception e, HttpServletRequest request) {
        log.error("서버 오류 발생: {}", e.getMessage(), e);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "서버 내부 오류가 발생했습니다. 관리자에게 문의해주세요.",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}