package com.burgerking.reservation.service;

import com.burgerking.reservation.domain.enums.OrderStatus;
import com.burgerking.reservation.web.dto.OrderRequest;
import com.burgerking.reservation.web.dto.OrderResponse;
import com.burgerking.reservation.web.dto.OrderStatusUpdateRequest;

import java.time.LocalDateTime;
import java.util.List;

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