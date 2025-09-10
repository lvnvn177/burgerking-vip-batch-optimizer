package com.burgerking.coupon.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponIssueRequest {
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;

    @NotNull(message = "쿠폰 ID는 필수입니다")
    private Long couponId;
}
