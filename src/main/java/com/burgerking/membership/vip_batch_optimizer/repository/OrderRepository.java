package com.burgerking.membership.vip_batch_optimizer.repository;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.burgerking.membership.vip_batch_optimizer.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
      @Query("SELECT o.member.id, SUM(o.amount) as totalAmount " +
           "FROM Order o " +
           "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
           "AND o.status = 'COMPLETED' " +
           "GROUP BY o.member.id")
    List<Object[]> findTotalAmountByMemberInPeriod(@Param("startDate") LocalDateTime
startDateTime, @Param("endDate") LocalDateTime endDate);

     @Query("SELECT o FROM Order o WHERE o.member.id = :memberId AND o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByMemberInPeriod(@Param("memberId") Long memberId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
