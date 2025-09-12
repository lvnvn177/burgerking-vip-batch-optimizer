package com.burgerking.coupon.web.exception;

import org.springframework.http.HttpStatus;

public class CouponException extends BusinessException {
    
    public CouponException(String message, String errorCode, HttpStatus statusCode) {
        super(message, errorCode, statusCode);
    }

    // 쿠폰 소진 예외
    public static CouponException couponSoldout() {
        return new CouponException(
            "쿠폰이 모두 소진되었습니다.", 
            "COUPON_SOLD_OUT", 
            HttpStatus.BAD_REQUEST
        );
    }

    // 이미 발급됩 쿠폰 예외
    public static CouponException alreadyIssued(Long userId, Long couponId) {
        return new CouponException(
            String.format("이미 발급받은 쿠폰입니다. (사용자: %d, 쿠폰: %d)"),
             "COUPON_ALREADY_ISSUED",
             HttpStatus.BAD_REQUEST
        );
    }


    // 쿠폰 미존재 예외
    public static CouponException notFound(Long couponId) {
        return new CouponException(
            String.format("존재하지 않는 쿠폰입니다: %d", couponId),
             "COUPON_NOT_FOUND",
              HttpStatus.NOT_FOUND
        );
    }

    // 쿠폰 재고 정보 미존재 예외
    public static CouponException stockNotFound(Long couponId) {
        return new CouponException(
            String.format("쿠폰 재고 정보가 없습니다: %d", couponId),
             "COUPON_STOCK_NOT_FOUND",
            HttpStatus.NOT_FOUND
        );
    }

    // 쿠폰 코드 미존재 예외
    public static CouponException codeNotFound(String couponCode) {
        return new CouponException(
            String.format("존재하지 않는 쿠폰 코드입니다: %s", couponCode),
            "COUPON_CODE_NOT_FOUND",
            HttpStatus.NOT_FOUND
        );
    }

    // 이밎 사용된 쿠폰 예외
    public static CouponException alreadyUsed(String couponCode) {
        return new CouponException(
            String.format("이미 사용된 쿠폰입니다: %s"),
            "COUPON_ALREADY_USED",
            HttpStatus.BAD_REQUEST
        );
    }

    // 만료된 쿠폰 예외
    public static CouponException expired(String couponCode) {
        return new CouponException(
            String.format("만료된 쿠폰입니다: %s"),
            "COUPON_EXPIRED",
            HttpStatus.BAD_REQUEST
        );
    }
}
