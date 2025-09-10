package com.burgerking.coupon.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponUseRequest {
    
    @NotBlank(message = "쿠폰 코드는 필수입니다")
    private String couponCode;

    @NotBlank(message = "주문 참조 정보는 필수입니다")
    private String orderReference;
}


