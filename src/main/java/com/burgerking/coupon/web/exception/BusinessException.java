package com.burgerking.coupon.web.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException{
    
    private final String errorCode;
    private final HttpStatus statusCode;

    public BusinessException(String message, String errorCode, HttpStatus statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
