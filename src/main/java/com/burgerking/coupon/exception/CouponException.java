package com.burgerking.coupon.exception;

import com.burgerking.common.exception.BusinessException;

public class CouponException extends BusinessException {
    
    private CouponException(CouponErrorCode errorCode) {
        super(errorCode);
    }
    
    private CouponException(CouponErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }

    // 정적 팩토리 메소드들
    public static CouponException couponNotFound() {
        return new CouponException(CouponErrorCode.COUPON_NOT_FOUND);
    }

    public static CouponException couponAlreadyIssued() {
        return new CouponException(CouponErrorCode.COUPON_ALREADY_ISSUED);
    }

    public static CouponException couponSoldOut() {
        return new CouponException(CouponErrorCode.COUPON_SOLD_OUT);
    }

    public static CouponException couponExpired() {
        return new CouponException(CouponErrorCode.COUPON_EXPIRED);
    }

    public static CouponException couponNotActive() {
        return new CouponException(CouponErrorCode.COUPON_NOT_ACTIVE);
    }

    public static CouponException couponAlreadyUsed() {
        return new CouponException(CouponErrorCode.COUPON_ALREADY_USED);
    }

    public static CouponException invalidCouponCode() {
        return new CouponException(CouponErrorCode.INVALID_COUPON_CODE);
    }
}