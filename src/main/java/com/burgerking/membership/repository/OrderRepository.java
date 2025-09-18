package com.burgerking.membership.repository;

import com.burgerking.membership.domain.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository("OrderRepository")
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * 특정 기간 동안의 사용자 주문 내역을 조회합니다.
     *
     * @param userId    사용자 ID
     * @param startDate 조회 시작 일시
     * @param endDate   조회 종료 일시
     * @return List<Order>
     */
    List<Order> findByUserIdAndOrderDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
