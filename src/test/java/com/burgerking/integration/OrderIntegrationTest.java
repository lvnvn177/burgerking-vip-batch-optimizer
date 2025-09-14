package com.burgerking.integration;

import com.burgerking.reservation.domain.Menu;
import com.burgerking.reservation.domain.Store;
import com.burgerking.reservation.domain.enums.OrderStatus;
import com.burgerking.reservation.repository.MenuRepository;
import com.burgerking.reservation.repository.ReservationOrderRepository;
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

/**
 * 주문 관련 API의 통합 테스트 클래스입니다.
 * TestRestTemplate을 사용하여 실제 HTTP 요청을 보내고 전체 흐름을 테스트합니다.
 */
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
    private ReservationOrderRepository reservationOrderRepository;

    private Store testStore;
    private Menu testMenu;

    @BeforeEach
    void setUp() {
        // 테스트에 필요한 매장과 메뉴 데이터를 미리 생성합니다.
        testStore = Store.builder().isOpen(true).build();
        storeRepository.save(testStore);

        testMenu = Menu.builder()
                .name("와퍼")
                .price(new BigDecimal("7000"))
                .store(testStore)
                .available(true)
                .build();
        menuRepository.save(testMenu);
    }

    @AfterEach
    void tearDown() {
        // 각 테스트 실행 후 데이터베이스를 초기화합니다.
        reservationOrderRepository.deleteAll();
        menuRepository.deleteAll();
        storeRepository.deleteAll();
    }

    /**
     * 주문 생성 후 해당 주문을 정상적으로 조회할 수 있는지 테스트합니다.
     */
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
                "/api/reservation/orders", orderRequest, OrderResponse.class);

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
                "/api/reservation/orders/" + orderId, OrderResponse.class);

        // 조회 결과 검증
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        
        OrderResponse getResponseBody = getResponse.getBody();
        assertNotNull(getResponseBody); // getResponseBody가 null이 아닌지 확인
        assertEquals(orderId, getResponseBody.getId());
        assertEquals(responseBody.getOrderNumber(), getResponseBody.getOrderNumber());
    }
}