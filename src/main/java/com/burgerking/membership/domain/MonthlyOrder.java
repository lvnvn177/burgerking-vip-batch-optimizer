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
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private YearMonth yearMonth;
    
    @Column(nullable = false)
    private Integer totalAmount;
    
    @Column(nullable = false)
    private Integer orderCount;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;

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