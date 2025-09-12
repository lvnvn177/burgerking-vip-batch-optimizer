package com.burgerking.membership.exception;

import com.burgerking.common.exception.ErrorCode;

public enum MembershipErrorCode implements ErrorCode {
    MEMBERSHIP_NOT_FOUND("멤버십 정보를 찾을 수 없습니다."),
    INVALID_ORDER_AMOUNT("주문 금액이 유효하지 않습니다."),
    MONTHLY_ORDER_NOT_FOUND("해당 월의 주문 내역을 찾을 수 없습니다."),
    INVALID_GRADE_EVALUATION("등급 평가 과정에서 오류가 발생했습니다."),
    INSUFFICIENT_ORDER_HISTORY("등급 평가를 위한 충분한 주문 내역이 없습니다.");

    private final String message;

    MembershipErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}