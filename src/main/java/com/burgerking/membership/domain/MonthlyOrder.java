package com.burgerking.membership.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "monthly_orders", indexes = {
    @Index(name = "idx_monthly_orders_user_id_year_month", columnList = "userId,yearMonth")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 월별 주문 집계 ID

    @Column(nullable = false)
    private Long userId;            // 사용자 ID

    @Column(nullable = false)
    private YearMonth yearMonth;    // 집계 년월

    @Column(nullable = false)
    private Integer totalAmount;    // 월간 총 주문 금액

    @Column(nullable = false)
    private Integer orderCount;     // 월간 총 주문 횟수

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성일

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정일

    @Builder
    public MonthlyOrder(Long userId, YearMonth yearMonth) {
        this.userId = userId;
        this.yearMonth = yearMonth;
        this.totalAmount = 0;
        this.orderCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 주문을 추가하고 금액을 누적합니다.
     * @param amount 주문 금액
     */
    public void addOrder(int amount) {
        this.totalAmount += amount;
        this.orderCount++;
        this.updatedAt = LocalDateTime.now();
    }
}