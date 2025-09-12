package com.burgerking.coupon.exception;

import com.burgerking.common.exception.ErrorCode;

public enum CouponErrorCode implements ErrorCode {
    COUPON_NOT_FOUND("쿠폰을 찾을 수 없습니다."),
    COUPON_ALREADY_ISSUED("이미 발급받은 쿠폰입니다."),
    COUPON_SOLD_OUT("쿠폰이 모두 소진되었습니다."),
    COUPON_EXPIRED("만료된 쿠폰입니다."),
    COUPON_NOT_ACTIVE("사용 가능한 상태가 아닌 쿠폰입니다."),
    COUPON_ALREADY_USED("이미 사용된 쿠폰입니다."),
    INVALID_COUPON_CODE("유효하지 않은 쿠폰 코드입니다.");

    private final String message;

    CouponErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}