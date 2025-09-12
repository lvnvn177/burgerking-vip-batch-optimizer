package com.burgerking.reservation.web.dto;

import com.burgerking.reservation.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {
    
    @NotNull(message = "주문 상태는 필수입니다")
    private OrderStatus status;
}