package com.burgerking.membership.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "sum_orders", indexes = {
    @Index(name = "idx_sum_orders_user_id", columnList = "user_Id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SumOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 누적 주문 집계 ID

    @Column(name = "user_id", nullable = false)
    private Long userId;            // 사용자 ID

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;    // 총 주문 금액

    @Column(name = "order_count", nullable = false)
    private Integer orderCount;     // 총 주문 횟수

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 생성일

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정일

    @Builder
    public SumOrder(Long userId) {
        this.userId = userId;
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

    public SumOrder orElseGet(Object object) {
        throw new UnsupportedOperationException("Unimplemented method 'orElseGet'");
    }
}