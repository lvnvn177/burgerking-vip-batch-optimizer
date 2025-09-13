package com.burgerking.membership.exception;

import com.burgerking.common.exception.ErrorCode;

/**
 * 멤버십 도메인 관련 에러 코드를 정의하는 열거형입니다.
 */
public enum MembershipErrorCode implements ErrorCode {
    /**
     * 요청한 멤버십 정보를 찾을 수 없을 때 발생합니다.
     */
    MEMBERSHIP_NOT_FOUND("멤버십 정보를 찾을 수 없습니다."),

    /**
     * 주문 금액이 유효하지 않을 때 (e.g., 음수) 발생합니다.
     */
    INVALID_ORDER_AMOUNT("주문 금액이 유효하지 않습니다."),

    /**
     * 특정 월의 주문 집계 정보를 찾을 수 없을 때 발생합니다.
     */
    MONTHLY_ORDER_NOT_FOUND("해당 월의 주문 내역을 찾을 수 없습니다."),

    /**
     * 멤버십 등급 평가 로직 수행 중 예외적인 상황이 발생했을 때 사용됩니다.
     */
    INVALID_GRADE_EVALUATION("등급 평가 과정에서 오류가 발생했습니다."),

    /**
     * 등급 평가에 필요한 최소한의 주문 내역이 존재하지 않을 때 발생합니다.
     */
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