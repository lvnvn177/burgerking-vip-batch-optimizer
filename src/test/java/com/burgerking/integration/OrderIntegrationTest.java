package com.burgerking.integration;

import com.burgerking.reservation.domain.Menu;
import com.burgerking.reservation.domain.Store;
import com.burgerking.reservation.domain.enums.OrderStatus;
import com.burgerking.reservation.repository.MenuRepository;
import com.burgerking.reservation.repository.OrderRepository;
import com.burgerking.reservation.repository.StoreRepository;
import com.burgerking.reservation.web.dto.OrderRequest;
import com.burgerking.reservation.web.dto.OrderResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OrderIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MenuRepository menuRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    private Store testStore;
    private Menu testMenu;

    @BeforeEach
    void setUp() {
        // 테스트용 매장 생성
        testStore = Store.builder()
                .isOpen(true)
                .build();
        testStore = storeRepository.save(testStore);

        // 테스트용 메뉴 생성
        testMenu = Menu.builder()
                .name("와퍼")
                .price(new BigDecimal("7000"))
                .store(testStore)
                .available(true)
                .build();
        testMenu = menuRepository.save(testMenu);
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        menuRepository.deleteAll();
        storeRepository.deleteAll();
    }

    @Test
    void createAndGetOrder_ShouldSucceed() {
        // 주문 요청 생성
        List<OrderRequest.OrderItemRequest> items = new ArrayList<>();
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setMenuId(testMenu.getId());
        item.setQuantity(2);
        items.add(item);

        OrderRequest orderRequest = OrderRequest.builder()
                .userId(1L)
                .storeId(testStore.getId())
                .pickupTime(LocalDateTime.now().plusHours(1))
                .items(items)
                .build();

        // 주문 생성 요청
        ResponseEntity<OrderResponse> createResponse = restTemplate.postForEntity(
                "/api/orders", orderRequest, OrderResponse.class);

        // 응답 검증
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        
        OrderResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody); // responseBody가 null이 아닌지 확인
        assertNotNull(responseBody.getOrderNumber());
        assertEquals(OrderStatus.PENDING, responseBody.getStatus());
        assertEquals(new BigDecimal("14000"), responseBody.getTotalAmount());

        // 생성된 주문 조회
        Long orderId = responseBody.getId();
        ResponseEntity<OrderResponse> getResponse = restTemplate.getForEntity(
                "/api/orders/" + orderId, OrderResponse.class);

        // 조회 결과 검증
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        
        OrderResponse getResponseBody = getResponse.getBody();
        assertNotNull(getResponseBody); // getResponseBody가 null이 아닌지 확인
        assertEquals(orderId, getResponseBody.getId());
        assertEquals(responseBody.getOrderNumber(), getResponseBody.getOrderNumber());
    }
}