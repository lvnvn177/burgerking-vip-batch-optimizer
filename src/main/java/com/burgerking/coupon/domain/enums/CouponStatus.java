package com.burgerking.coupon.domain.enums;

/**
 * 쿠폰의 현재 상태를 나타내는 열거형
 */

public enum CouponStatus {
     /**
     * 활성화 상태 - 사용 가능한 쿠폰
     */
     ACTIVE("사용 가능"),

     /**
     * 사용됨 - 이미 사용한 쿠폰
     */
     USED("사용 완료"),

     /**
     * 만료됨 - 유효 기간이 지난 쿠폰
     */
    EXPIRED("기간 만료"),
    
    /**
     * 취소됨 - 발급 취소된 쿠폰
     */
    CANCELLED("발급 취소"),
    
    /**
     * 대기 중 - 아직 활성화되지 않은 쿠폰 (예약 발급 등)
     */
    PENDING("대기 중");


     private final String description;

     CouponStatus(String description) {
        this.description = description;
     }

     public String getDescripion() {
        return description;
     }
}
