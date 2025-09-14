package com.burgerking.membership.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 처리 요청 DTO")
public class OrderProcessRequest {
    @NotNull(message = "사용자 ID는 필수입니다.")
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    /**
     * 주문 번호
     */
    @NotNull(message = "주문 번호는 필수입니다.")
    @Schema(description = "주문 번호", example = "ORD20231026-0001")
    private String orderNumber;

    /**
     * 주문 금액
     */
    @Min(value = 0, message = "주문 금액은 0 이상이어야 합니다.")
    @NotNull(message = "주문 금액은 필수입니다.")
    @Schema(description = "주문 금액", example = "15000")
    private Integer orderAmount;
}