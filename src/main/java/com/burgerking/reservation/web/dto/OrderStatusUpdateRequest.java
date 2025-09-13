package com.burgerking.reservation.web.dto;

import com.burgerking.reservation.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/** 
 * 주문 상태 관련 Request
 * 
 * Field
 * 현재 주문 상태 
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {
    
    @NotNull(message = "주문 상태는 필수입니다")
    private OrderStatus status;
}