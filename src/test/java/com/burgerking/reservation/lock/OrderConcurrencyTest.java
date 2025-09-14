package com.burgerking.reservation.lock;

import com.burgerking.reservation.domain.Menu;
import com.burgerking.reservation.domain.Store;
import com.burgerking.reservation.repository.MenuRepository;
import com.burgerking.reservation.repository.ReservationOrderRepository;
import com.burgerking.reservation.repository.StoreRepository;
import com.burgerking.reservation.service.OrderService;
import com.burgerking.reservation.web.dto.OrderRequest;
import com.burgerking.reservation.web.dto.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 주문 생성 기능의 동시성 문제를 테스트하는 클래스입니다.
 * 분산 락을 사용하여 여러 스레드가 동시에 주문을 생성할 때의 동작을 검증합니다.
 */
@SpringBootTest
@ActiveProfiles("test")
public class OrderConcurrencyTest {

    @Autowired
    private OrderService orderService;

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
        reservationOrderRepository.deleteAllInBatch();
        menuRepository.deleteAllInBatch();
        storeRepository.deleteAllInBatch();

        testStore = Store.builder().isOpen(true).build();
        storeRepository.save(testStore);

        testMenu = Menu.builder()
                .name("리미티드 버거")
                .price(new BigDecimal("10000"))
                .store(testStore)
                .available(true)
                .build();
        menuRepository.save(testMenu);
    }

    /**
     * 여러 스레드에서 동시에 주문 생성을 요청했을 때,
     * 모든 요청이 성공적으로 처리되는지 (재고가 무한한 경우) 검증합니다.
     *
     * @throws InterruptedException 스레드 대기 중 발생할 수 있는 예외
     */
    @Test
    void concurrentOrderCreation_ShouldHandleConcurrency() throws InterruptedException {
        int numberOfThreads = 20; // 동시에 20개의 주문 시도
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        
        // 모든 스레드가 동시에 시작할 수 있도록 준비
        for (int i = 0; i < numberOfThreads; i++) {
            final long userId = i + 1;
            executorService.submit(() -> {
                try {
                    // 주문 요청 생성
                    List<OrderRequest.OrderItemRequest> items = new ArrayList<>();
                    OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
                    item.setMenuId(testMenu.getId());
                    item.setQuantity(1);
                    items.add(item);

                    OrderRequest orderRequest = OrderRequest.builder()
                            .userId(userId)
                            .storeId(testStore.getId())
                            .pickupTime(LocalDateTime.now().plusHours(1))
                            .items(items)
                            .build();

                    // 주문 생성 시도
                    try {
                        OrderResponse response = orderService.createOrder(orderRequest);
                        if (response != null && response.getId() != null) {
                            successCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        // 동시성 문제로 주문 생성 실패
                        System.out.println("주문 생성 실패: " + e.getMessage());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드 완료 대기
        latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        // 결과 검증
        // 메뉴는 무제한이므로 모든 주문이 성공해야 함
        // 만약 재고 제한이 있다면 성공 횟수는 재고 수량과 같아야 함
        assertEquals(numberOfThreads, successCount.get());
        
        // 생성된 주문 수 확인
        long orderCount = reservationOrderRepository.count();
        assertEquals(numberOfThreads, orderCount);
    }
}