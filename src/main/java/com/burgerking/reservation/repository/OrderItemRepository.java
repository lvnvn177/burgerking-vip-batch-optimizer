package com.burgerking.reservation.repository;

import com.burgerking.reservation.domain.Order;
import com.burgerking.reservation.domain.OrderItem;
import com.burgerking.reservation.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // 주문별 주문 항목 찾기
    List<OrderItem> findByOrder(Order order);
    
    // 특정 메뉴의 주문 항목 찾기
    List<OrderItem> findByMenu(Menu menu);
    
    // 특정 메뉴의 주문 항목 수 조회
    long countByMenu(Menu menu);
}