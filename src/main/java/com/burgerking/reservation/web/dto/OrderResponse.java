package com.burgerking.reservation.web.dto;

import com.burgerking.reservation.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** 
 * 주문 관련 Resonse
 * 
 * Field
 * ID / 주문 번호 / 해당 주문을 요청한 고객 ID / 해당 주문을 요청받은 매장 ID
 * 배정된 픽업시간 / 전체 주문 금액 / 현재 주문 상태 / 요청 시간 / 주문 항목 
 * 
 * 주문 항목 관련 Response
 * 
 * Field
 * ID / 항목에 해당되는 메뉴 ID, 메뉴 이름, 수량, 항목 전체 가격
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long userId;
    private Long storeId;
    private LocalDateTime pickupTime;   
    private BigDecimal totalAmount; 
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private Long id;
        private Long menuId;
        private String menuName;
        private Integer quantity;    
        private BigDecimal price;   
    }
}