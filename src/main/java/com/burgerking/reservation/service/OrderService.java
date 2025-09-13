package com.burgerking.reservation.service;

import com.burgerking.reservation.domain.enums.OrderStatus;
import com.burgerking.reservation.web.dto.OrderRequest;
import com.burgerking.reservation.web.dto.OrderResponse;
import com.burgerking.reservation.web.dto.OrderStatusUpdateRequest;

import java.time.LocalDateTime;
import java.util.List;

/** 
 * 주문 관련 인터페이스
 * 
 * 조회
 * 주문 ID / 주문 번호 / 주문한 고객의 ID / 주문 요청을 받은 매장 ID 
 * 특정 주문 상태 / 특정 시간 / 픽업된 특정 시간대 
 * 
 * 생성 
 * 주문 Request
 * 
 * 수정
 * 수정하고자 하는 주문 ID 및 수정된 주문 상태 Request
 * 
 * 삭제
 * 주문 ID
*/

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);   
    OrderResponse getOrderById(Long id);    
    OrderResponse getOrderByOrderNumber(String orderNumber); 
    List<OrderResponse> getOrdersByUserId(Long userId);
    List<OrderResponse> getOrdersByStore(Long storeId); 
    List<OrderResponse> getOrdersByStatus(OrderStatus status); 
    List<OrderResponse> getOrdersByPickupTimeBetween(LocalDateTime start, LocalDateTime end);   
    OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest statusRequest);   
    void cancelOrder(Long id);  
}