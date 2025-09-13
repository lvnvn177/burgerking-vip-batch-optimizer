package com.burgerking.reservation.repository;

import com.burgerking.reservation.domain.Order;
import com.burgerking.reservation.domain.OrderItem;
import com.burgerking.reservation.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 
 * 주문 항목 관련 Repository
 * 
 * 조회
 * 주문 항목을 포함하고 있는 주문 / 특정 메뉴를 포함하고 있는 주문 / 특정 메뉴를 포함하고 있는 주문 항목 수 
*/
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // 주문별 주문 항목 찾기
    List<OrderItem> findByOrder(Order order);
    
    // 특정 메뉴의 주문 항목 찾기
    List<OrderItem> findByMenu(Menu menu);
    
    // 특정 메뉴의 주문 항목 수 조회
    long countByMenu(Menu menu);
}