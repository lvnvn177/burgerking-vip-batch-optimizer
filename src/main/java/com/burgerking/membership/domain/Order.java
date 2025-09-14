package com.burgerking.membership.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "MembershipOrder")
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // 주문 ID

    @Column(nullable = false)
    private Long userId;                // 사용자 ID

    @Column(nullable = false)
    private String orderNumber;         // 주문 번호

    @Column(nullable = false)
    private Integer orderAmount;        // 주문 금액

    @Column(nullable = false)
    private LocalDateTime orderDate;    // 주문 일시

    @Column(nullable = false)
    private LocalDateTime createdAt;    // 생성일
    
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