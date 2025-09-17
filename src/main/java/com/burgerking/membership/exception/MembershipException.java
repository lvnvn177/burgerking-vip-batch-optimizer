package com.burgerking.membership.exception;

import com.burgerking.common.exception.BusinessException;

/**
 * 멤버십 도메인에서 발생하는 비즈니스 예외를 나타내는 클래스입니다.
 */
public class MembershipException extends BusinessException {

    private MembershipException(MembershipErrorCode errorCode) {
        super(errorCode);
    }

    private MembershipException(MembershipErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }

    /**
     * '멤버십 없음' 예외를 생성합니다.
     *
     * @param userId 멤버십을 찾지 못한 사용자 ID
     * @return MembershipException
     */
    public static MembershipException membershipNotFound(Long userId) {
        return new MembershipException(
            MembershipErrorCode.MEMBERSHIP_NOT_FOUND,
            "사용자 ID: " + userId + "에 해당하는 멤버십을 찾을 수 없습니다."
        );
    }

    /**
     * '유효하지 않은 주문 금액' 예외를 생성합니다.
     *
     * @param amount 유효하지 않은 주문 금액
     * @return MembershipException
     */
    public static MembershipException invalidOrderAmount(Integer amount) {
        return new MembershipException(
            MembershipErrorCode.INVALID_ORDER_AMOUNT,
            "유효하지 않은 주문 금액입니다: " + amount
        );
    }

    /**
     * '잘못된 등급 평가' 예외를 생성합니다.
     *
     * @return MembershipException
     */
    public static MembershipException invalidGradeEvaluation() {
        return new MembershipException(MembershipErrorCode.INVALID_GRADE_EVALUATION);
    }

    /**
     * '주문 내역 불충분' 예외를 생성합니다.
     *
     * @param userId 주문 내역이 불충분한 사용자 ID
     * @return MembershipException
     */
    public static MembershipException insufficientOrderHistory(Long userId) {
        return new MembershipException(
            MembershipErrorCode.INSUFFICIENT_ORDER_HISTORY,
            "사용자 ID: " + userId + "의 등급 평가를 위한 충분한 주문 내역이 없습니다."
        );
    }
}