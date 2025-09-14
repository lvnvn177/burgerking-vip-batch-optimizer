package com.burgerking.reservation.service.impl;

import com.burgerking.reservation.domain.Menu;
import com.burgerking.reservation.domain.Order;
import com.burgerking.reservation.domain.OrderItem;
import com.burgerking.reservation.domain.Store;
import com.burgerking.reservation.domain.enums.OrderStatus;
import com.burgerking.reservation.repository.MenuRepository;
import com.burgerking.reservation.repository.OrderItemRepository;
import com.burgerking.reservation.repository.OrderRepository;
import com.burgerking.reservation.repository.StoreRepository;
import com.burgerking.reservation.service.OrderService;
import com.burgerking.reservation.web.dto.OrderRequest;
import com.burgerking.reservation.web.dto.OrderResponse;
import com.burgerking.reservation.web.dto.OrderStatusUpdateRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/** 
 * 주문 관련 구현체
 * 
 * 조회
 * 주문 ID / 주문 번호 / 주문한 고객의 ID / 주문을 요청 받은 매장 ID / 특정 주문 상태 / 픽업 시간대 
 * 
 * 생성 
 * 주문 Request 
 * 
 * 수정
 * 수정하고자 하는 주문 ID 및 수정된 주문 상태 Request
 * 
 * 삭제
 * 주문 ID
 * 
*/
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            StoreRepository storeRepository,
            MenuRepository menuRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.storeRepository = storeRepository;
        this.menuRepository = menuRepository;
    }

    @Override
    @Transactional
    public OrderResponse createOrderNonOptimized(OrderRequest orderRequest) {
        return createOrder(orderRequest); // Non-optimized version simply calls the base createOrder
    }

    @Override
    @Transactional
    public OrderResponse createOrderOptimized(OrderRequest orderRequest) {
        // This will be the optimized version with pessimistic locking.
        // For now, it also calls the base createOrder.
        // Actual locking logic will be added later by modifying the menuRepository.
        return createOrder(orderRequest);
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        // 1. 매장 조회
        Store store = storeRepository.findById(orderRequest.getStoreId())
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다: " + orderRequest.getStoreId()));

        // 2. 매장이 영업 중인지 확인
        if (!store.isOpen()) {
            throw new RuntimeException("매장이 영업 중이 아닙니다.");
        }

        // 3. 주문 생성
        Order order = Order.builder()
                .userId(orderRequest.getUserId())
                .store(store)
                .orderNumber(generateOrderNumber())
                .pickupTime(orderRequest.getPickupTime())
                .totalAmount(BigDecimal.ZERO) // 초기값
                .status(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);

        // 4. 주문 항목 생성 및 총액 계산
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderRequest.OrderItemRequest itemRequest : orderRequest.getItems()) {
            Menu menu = menuRepository.findByIdWithPessimisticLock(itemRequest.getMenuId())
                    .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다: " + itemRequest.getMenuId()));

            // 메뉴가 판매 가능한지 확인
            if (!menu.isAvailable()) {
                throw new RuntimeException("현재 판매 불가능한 메뉴입니다: " + menu.getName());
            }

            // 재고 확인
            if (menu.getAvailableQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("재고가 부족합니다: " + menu.getName());
            }

            // 재고 감소
            menu.decreaseAvailableQuantity(itemRequest.getQuantity());
            // menuRepository.save(menu); // @Transactional이므로 변경 감지 후 자동 저장

            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .menu(menu)
                    .quantity(itemRequest.getQuantity())
                    .price(menu.getPrice())
                    .build();

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(menu.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        // 주문 항목 저장
        orderItemRepository.saveAll(orderItems);

        // 총액 업데이트
        savedOrder.setTotalAmount(totalAmount);
        savedOrder = orderRepository.save(savedOrder);

        // 5. 응답 생성
        return convertToOrderResponse(savedOrder, orderItems);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {    
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + id));
        
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        return convertToOrderResponse(order, orderItems);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {   
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + orderNumber));
        
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        return convertToOrderResponse(order, orderItems);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) { 
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(order -> {
                    List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
                    return convertToOrderResponse(order, orderItems);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStore(Long storeId) { 
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다: " + storeId));
        
        List<Order> orders = orderRepository.findByStore(store);
        return orders.stream()
                .map(order -> {
                    List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
                    return convertToOrderResponse(order, orderItems);
                })
                .collect(Collectors.toList());
    }

        @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) { 
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream()
                .map(order -> {
                    List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
                    return convertToOrderResponse(order, orderItems);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByPickupTimeBetween(LocalDateTime start, LocalDateTime end) { 
        List<Order> orders = orderRepository.findByPickupTimeBetween(start, end);
        return orders.stream()
                .map(order -> {
                    List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
                    return convertToOrderResponse(order, orderItems);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest statusRequest) {   
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + id));
        
        // 주문 상태 변경
        order.setStatus(statusRequest.getStatus());
        Order updatedOrder = orderRepository.save(order);
        
        List<OrderItem> orderItems = orderItemRepository.findByOrder(updatedOrder);
        return convertToOrderResponse(updatedOrder, orderItems);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {  
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + id));
        
        // 이미 취소된 주문인지 확인
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new RuntimeException("이미 취소된 주문입니다.");
        }
        
        // 준비 완료 또는 완료된 주문은 취소할 수 없음
        if (order.getStatus() == OrderStatus.READY || order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("준비 완료 또는 완료된 주문은 취소할 수 없습니다.");
        }
        
        // 주문 상태 취소로 변경
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }
    
    // 주문번호 생성 메서드
    private String generateOrderNumber() {
        return "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // Order 엔티티를 OrderResponse DTO로 변환하는 메서드
    private OrderResponse convertToOrderResponse(Order order, List<OrderItem> orderItems) {
        List<OrderResponse.OrderItemResponse> itemResponses = orderItems.stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .id(item.getId())
                        .menuId(item.getMenu().getId())
                        .menuName(item.getMenu().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());
        
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .storeId(order.getStore().getId())
                .pickupTime(order.getPickupTime())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }
}