package com.burgerking.reservation.service;

import com.burgerking.reservation.domain.Menu;
import com.burgerking.reservation.domain.Order;
import com.burgerking.reservation.domain.OrderItem;
import com.burgerking.reservation.domain.Store;
import com.burgerking.reservation.domain.enums.OrderStatus;
import com.burgerking.reservation.repository.MenuRepository;
import com.burgerking.reservation.repository.OrderItemRepository;
import com.burgerking.reservation.repository.OrderRepository;
import com.burgerking.reservation.repository.StoreRepository;
import com.burgerking.reservation.service.impl.OrderServiceImpl;
import com.burgerking.reservation.web.dto.OrderRequest;
import com.burgerking.reservation.web.dto.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Store testStore;
    private Menu testMenu;
    private Order testOrder;
    private OrderItem testOrderItem;
    private OrderRequest testOrderRequest;

    @BeforeEach
    void setUp() {
        testStore = Store.builder()
                .id(1L)
                .isOpen(true)
                .build();

        testMenu = Menu.builder()
                .id(1L)
                .name("햄버거")
                .price(new BigDecimal("5000"))
                .store(testStore)
                .available(true)
                .build();

        testOrder = Order.builder()
                .id(1L)
                .userId(1L)
                .store(testStore)
                .orderNumber("BK-12345678")
                .pickupTime(LocalDateTime.now().plusHours(1))
                .totalAmount(new BigDecimal("5000"))
                .status(OrderStatus.PENDING)
                .build();

        testOrderItem = OrderItem.builder()
                .id(1L)
                .order(testOrder)
                .menu(testMenu)
                .quantity(1)
                .price(testMenu.getPrice())
                .build();

        List<OrderRequest.OrderItemRequest> items = new ArrayList<>();
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setMenuId(1L);
        item.setQuantity(1);
        items.add(item);

        testOrderRequest = OrderRequest.builder()
                .userId(1L)
                .storeId(1L)
                .pickupTime(LocalDateTime.now().plusHours(1))
                .items(items)
                .build();
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(testStore));
        when(menuRepository.findById(1L)).thenReturn(Optional.of(testMenu));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.saveAll(anyList())).thenReturn(Arrays.asList(testOrderItem));
        when(orderItemRepository.findByOrder(testOrder)).thenReturn(Arrays.asList(testOrderItem));

        OrderResponse result = orderService.createOrder(testOrderRequest);

        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        assertEquals(testOrder.getOrderNumber(), result.getOrderNumber());
        assertEquals(testOrder.getUserId(), result.getUserId());
        assertEquals(testOrder.getStore().getId(), result.getStoreId());
        assertEquals(testOrder.getTotalAmount(), result.getTotalAmount());
        assertEquals(testOrder.getStatus(), result.getStatus());
        
        // 주문 항목 검증
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals(testOrderItem.getMenu().getId(), result.getItems().get(0).getMenuId());
        assertEquals(testOrderItem.getMenu().getName(), result.getItems().get(0).getMenuName());
        assertEquals(testOrderItem.getQuantity(), result.getItems().get(0).getQuantity());
        assertEquals(testOrderItem.getPrice(), result.getItems().get(0).getPrice());
        
        // 저장소 호출 검증
        verify(storeRepository, times(1)).findById(1L);
        verify(menuRepository, times(1)).findById(1L);
        verify(orderRepository, times(2)).save(any(Order.class)); // 총액 업데이트 때문에 두 번 호출
        verify(orderItemRepository, times(1)).saveAll(anyList());
        verify(orderItemRepository, times(1)).findByOrder(testOrder);
    }

    @Test
    void createOrder_WithClosedStore_ShouldThrowException() {
        // 매장이 영업 중이 아닌 경우
        testStore.setOpen(false);
        when(storeRepository.findById(1L)).thenReturn(Optional.of(testStore));

        assertThrows(RuntimeException.class, () -> orderService.createOrder(testOrderRequest));
        verify(storeRepository, times(1)).findById(1L);
        verify(menuRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WithUnavailableMenu_ShouldThrowException() {
        // 메뉴가 판매 불가능한 경우
        testMenu.setAvailable(false);
        when(storeRepository.findById(1L)).thenReturn(Optional.of(testStore));
        when(menuRepository.findById(1L)).thenReturn(Optional.of(testMenu));

        assertThrows(RuntimeException.class, () -> orderService.createOrder(testOrderRequest));
        verify(storeRepository, times(1)).findById(1L);
        verify(menuRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class)); // 주문은 생성되지만 항목 추가 중 예외 발생
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findByOrder(testOrder)).thenReturn(Arrays.asList(testOrderItem));

        OrderResponse result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        assertEquals(testOrder.getOrderNumber(), result.getOrderNumber());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderItemRepository, times(1)).findByOrder(testOrder);
    }

    @Test
    void getOrderByOrderNumber_ShouldReturnOrder() {
        when(orderRepository.findByOrderNumber("BK-12345678")).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findByOrder(testOrder)).thenReturn(Arrays.asList(testOrderItem));

        OrderResponse result = orderService.getOrderByOrderNumber("BK-12345678");

        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        assertEquals("BK-12345678", result.getOrderNumber());
        verify(orderRepository, times(1)).findByOrderNumber("BK-12345678");
        verify(orderItemRepository, times(1)).findByOrder(testOrder);
    }
}