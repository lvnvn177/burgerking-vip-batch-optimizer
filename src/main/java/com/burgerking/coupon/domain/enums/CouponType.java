package com.burgerking.coupon.domain.enums;



/**
 * 쿠폰의 유형을 나타내는 열거형
 */
public enum CouponType {
    
      /**
     * 금액 할인 - 특정 금액을 차감 (예: 3,000원 할인)
     */
    FIXED_AMOUNT("금액 할인"),
    
    /**
     * 비율 할인 - 주문 금액의 특정 비율 차감 (예: 10% 할인)
     */
    PERCENTAGE("비율 할인"),
    
    /**
     * 무료 메뉴 - 특정 메뉴 무료 제공 (예: 감자튀김 무료)
     */
    FREE_MENU("무료 메뉴"),
    
    /**
     * 1+1 혜택 - 동일 상품 추가 제공
     */
    BUY_ONE_GET_ONE("1+1 혜택"),
    
    /**
     * 배송비 무료 - 배달 주문 시 배송비 면제
     */
    FREE_DELIVERY("배송비 무료"),
    
    /**
     * 특별 혜택 - 골든 패티 쿠폰 등 한정 프로모션
     */
    SPECIAL_PROMOTION("특별 혜택");

    private final String description;

    CouponType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
