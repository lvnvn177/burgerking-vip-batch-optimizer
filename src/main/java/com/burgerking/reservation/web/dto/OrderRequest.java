package com.burgerking.reservation.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;
import java.util.List;

/** 
 * 주문 관련 Request
 * 
 * Field
 * 주문을 요청한 고객 ID / 해당 주문을 요청받은 매장 ID / 배정한 픽업 시간
 * 주문 항목 리스트 
 * 
 * 주문 항목 관련 Request
 * 
 * Field
 * 항목에 해당하는 메뉴 ID / 항목에 속한 메뉴의 수량 
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    @NotNull(message = "매장 ID는 필수입니다")
    private Long storeId;
    
    @NotNull(message = "픽업 시간은 필수입니다")
    @Future(message = "픽업 시간은 현재 이후여야 합니다")
    private LocalDateTime pickupTime;
    
    @NotNull(message = "주문 항목은 필수입니다")
    @Size(min = 1, message = "최소 1개 이상의 항목을 주문해야 합니다")
    private List<OrderItemRequest> items;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "메뉴 ID는 필수입니다")
        private Long menuId;
        
        @NotNull(message = "수량은 필수입니다")
        @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다")
        private Integer quantity;
    }
}