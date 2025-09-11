package com.burgerking.coupon.web.dto;

import com.burgerking.coupon.domain.enums.CouponType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequest {
    
    @NotBlank(message = "쿠폰 이름은 필수입니다")
    private String name;

    private String description;

    @NotNull(message = "쿠폰 유형은 필수입니다")
    private CouponType couponType;

    @NotNull(message = "할인 금액 또는 비율은 필수입니다")
    private BigDecimal discountAmount;

    private boolean isPercentage;

    @NotNull(message = "최소 주문 금액은 필수입니다")
    private BigDecimal minmimumOrderAmount;

    @NotNull(message = "쿠폰 시작일은 필수입니다")
    private LocalDateTime startDate;

    @NotNull(message = "쿠폰 종료일은 필수입니다")
    private LocalDateTime endDate;

    @Min(value = 1, message = "총 발행 수량은 1개 이상이어야 합니다")
    private Integer totalQuantity;

    private BigDecimal minimumOrderAmount;

}
