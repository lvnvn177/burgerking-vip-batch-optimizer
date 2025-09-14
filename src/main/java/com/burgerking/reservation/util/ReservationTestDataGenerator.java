package com.burgerking.reservation.util;

import com.burgerking.reservation.domain.Menu;
import com.burgerking.reservation.domain.Order;
import com.burgerking.reservation.domain.OrderItem;
import com.burgerking.reservation.domain.Store;
import com.burgerking.reservation.domain.enums.OrderStatus;
import com.burgerking.reservation.repository.MenuRepository;
import com.burgerking.reservation.repository.OrderItemRepository;
import com.burgerking.reservation.repository.ReservationOrderRepository;
import com.burgerking.reservation.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationTestDataGenerator {

    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final ReservationOrderRepository reservationOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final Random random = new Random();

    @Transactional
    public List<Store> generateStoresAndMenus(int numberOfStores, int maxMenusPerStore, int initialMenuQuantity) {
        List<Store> stores = new ArrayList<>();
        for (int i = 0; i < numberOfStores; i++) {
            Store store = Store.builder()
                    .name("Store " + (i + 1))
                    .isOpen(true)
                    .build(); // createdAt, updatedAt은 @PrePersist/@PreUpdate로 자동 설정
            stores.add(store);
        }
        storeRepository.saveAll(stores);

        List<Menu> menus = new ArrayList<>();
        for (Store store : stores) {
            int numMenus = random.nextInt(maxMenusPerStore) + 1;
            for (int i = 0; i < numMenus; i++) {
                Menu menu = Menu.builder()
                        .name("Menu " + store.getId() + "-" + (i + 1))
                        .price(BigDecimal.valueOf(random.nextInt(10) * 1000 + 5000)) // 5000원 ~ 14000원
                        .store(store)
                        .available(true)
                        .availableQuantity(initialMenuQuantity)
                        .build(); // createdAt, updatedAt은 @PrePersist/@PreUpdate로 자동 설정
                menus.add(menu);
            }
        }
        menuRepository.saveAll(menus);
        System.out.println(numberOfStores + " stores and " + menus.size() + " menus generated for reservation testing.");
        return stores;
    }

    @Transactional
    public void generateOrdersForConcurrencyTest(Long userId, List<Menu> targetMenus, int numberOfOrders) {
        List<Order> orders = new ArrayList<>();
        List<OrderItem> orderItems = new ArrayList<>();

        for (int i = 0; i < numberOfOrders; i++) {
            // 각 주문마다 다른 user_id를 사용하는 것이 더 현실적인 동시성 테스트 시나리오
            Long currentUserId = userId + i; 
            Menu menu = targetMenus.get(random.nextInt(targetMenus.size())); // 랜덤 메뉴 선택
            Store store = menu.getStore(); // 메뉴의 매장 사용

            Order order = Order.builder()
                    .userId(currentUserId)
                    .store(store)
                    .orderNumber("RES-" + UUID.randomUUID().toString().substring(0, 8))
                    .pickupTime(LocalDateTime.now().plusMinutes(random.nextInt(60) + 10)) // 10분 ~ 70분 후 픽업
                    .totalAmount(menu.getPrice())
                    .status(OrderStatus.PENDING)
                    .build(); // createdAt, updatedAt은 @PrePersist/@PreUpdate로 자동 설정
            orders.add(order);

            // 주문 항목 생성
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menu(menu)
                    .quantity(1) // 동시성 테스트를 위해 보통 1개씩 주문
                    .price(menu.getPrice())
                    .build();
            orderItems.add(orderItem);
        }
        reservationOrderRepository.saveAll(orders);
        orderItemRepository.saveAll(orderItems);
        System.out.println(numberOfOrders + " orders generated for concurrency testing.");
    }
}