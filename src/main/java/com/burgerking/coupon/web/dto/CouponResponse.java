package com.burgerking.coupon.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {
    
    private Long id; // 쿠폰 발급 ID (CouponIssuance ID)
    private Long couponId; // 쿠폰 ID (Coupon ID)
    private String couponName; // 쿠폰 이름
    private String description; // 쿠폰 설명
    private String couponCode; // 고유 쿠폰 코드
    private String status; // 쿠폰 상태 (ACTIVE, USED, EXPIRED 등)
    private BigDecimal discountAmount; // 할인 금액 또는 비율
    private boolean isPercentage; // 퍼센트 할인 여부
    private LocalDateTime issuedAt; // 발급 시간
    private LocalDateTime expiresAt; // 만료 시간
    private LocalDateTime startDate; // 쿠폰 유효 시작일 
    private LocalDateTime endDate; // 쿠폰 유효 종료일 
}
