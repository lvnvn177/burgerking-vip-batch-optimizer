package com.burgerking.reservation.repository;

import com.burgerking.reservation.domain.Order;
import com.burgerking.reservation.domain.Store;
import com.burgerking.reservation.domain.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 주문 관련 Repository
 *
 * 조회
 * 주문 ID / 주문을 요청한 고객 ID / 주문을 요청받은 매장 ID / 특정 주문 상태
 * 픽업한 특정 시간대 / (매장 ID, 픽업 시간대)
*/
@Repository("reservationOrderRepository")
public interface ReservationOrderRepository extends JpaRepository<Order, Long> {
    
    // 주문 번호로 주문 찾기
    Optional<Order> findByOrderNumber(String orderNumber);
    
    // 사용자별 주문 찾기
    List<Order> findByUserId(Long userId);
    
    // 매장별 주문 찾기
    List<Order> findByStore(Store store);
    
    // 주문 상태별 찾기
    List<Order> findByStatus(OrderStatus status);
    
    // 특정 매장의 특정 상태 주문 찾기
    List<Order> findByStoreAndStatus(Store store, OrderStatus status);
    
    // 특정 시간대의 픽업 주문 찾기
    @Query("SELECT o FROM Order o WHERE o.pickupTime BETWEEN :startTime AND :endTime")
    List<Order> findByPickupTimeBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    // 특정 매장의 특정 시간대 픽업 주문 수 조회
    @Query("SELECT COUNT(o) FROM Order o WHERE o.store.id = :storeId AND o.pickupTime BETWEEN :startTime AND :endTime AND o.status NOT IN ('CANCELED')")
    long countActiveOrdersByStoreAndPickupTime(
            @Param("storeId") Long storeId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}