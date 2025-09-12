package com.burgerking.membership.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProcessRequest {
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "주문 번호는 필수입니다.")
    private String orderNumber; // Long에서 String으로 변경

    @Min(value = 0, message = "주문 금액은 0 이상이어야 합니다.")
    @NotNull(message = "주문 금액은 필수입니다.")
    private Integer orderAmount;
}