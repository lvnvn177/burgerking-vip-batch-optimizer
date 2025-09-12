package com.burgerking.membership.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String orderNumber;
    
    @Column(nullable = false)
    private Integer orderAmount;
    
    @Column(nullable = false)
    private LocalDateTime orderDate;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Builder
    public Order(Long userId, String orderNumber, Integer orderAmount) {
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.orderAmount = orderAmount;
        this.orderDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * 주문 정보를 이용해 해당 월의 MonthlyOrder 데이터를 업데이트합니다.
     * @param monthlyOrder 업데이트할 월별 주문 집계 정보
     */
    public void updateMonthlyOrder(MonthlyOrder monthlyOrder) {
        monthlyOrder.addOrder(this.orderAmount);
    }
}