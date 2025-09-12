package com.burgerking.membership.exception;

import com.burgerking.common.exception.BusinessException;

public class MembershipException extends BusinessException {
    
    private MembershipException(MembershipErrorCode errorCode) {
        super(errorCode);
    }
    
    private MembershipException(MembershipErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }

    // 정적 팩토리 메소드들
    public static MembershipException membershipNotFound(Long userId) {
        return new MembershipException(
            MembershipErrorCode.MEMBERSHIP_NOT_FOUND, 
            "사용자 ID: " + userId + "에 해당하는 멤버십을 찾을 수 없습니다."
        );
    }

    public static MembershipException invalidOrderAmount(Integer amount) {
        return new MembershipException(
            MembershipErrorCode.INVALID_ORDER_AMOUNT,
            "유효하지 않은 주문 금액입니다: " + amount
        );
    }

    public static MembershipException monthlyOrderNotFound(Long userId, String yearMonth) {
        return new MembershipException(
            MembershipErrorCode.MONTHLY_ORDER_NOT_FOUND,
            "사용자 ID: " + userId + "의 " + yearMonth + " 월 주문 내역을 찾을 수 없습니다."
        );
    }

    public static MembershipException invalidGradeEvaluation() {
        return new MembershipException(MembershipErrorCode.INVALID_GRADE_EVALUATION);
    }

    public static MembershipException insufficientOrderHistory(Long userId) {
        return new MembershipException(
            MembershipErrorCode.INSUFFICIENT_ORDER_HISTORY,
            "사용자 ID: " + userId + "의 등급 평가를 위한 충분한 주문 내역이 없습니다."
        );
    }
}