package com.burgerking.membership.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "monthly_orders")
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
    
    public void addOrder(int amount) {
        this.totalAmount += amount;
        this.orderCount++;
        this.updatedAt = LocalDateTime.now();
    }
}