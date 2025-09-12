package com.burgerking.membership.repository;

import com.burgerking.membership.domain.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // 특정 기간 동안의 사용자 주문 내역 조회 (필요 시)
    List<Order> findByUserIdAndOrderDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
