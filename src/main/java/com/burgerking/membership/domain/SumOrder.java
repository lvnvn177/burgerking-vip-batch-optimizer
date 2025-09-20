package com.burgerking.membership.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 *  누적 주문 데이터
 */
@Entity
@Table(name = "sum_orders", indexes = {
    @Index(name = "idx_sum_orders_user_id", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 추가
@Builder // 클래스 레벨로 빌더 이동
public class SumOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 누적 주문 집계 ID

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;            // 사용자 ID

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;    // 총 주문 금액

    @Column(name = "order_count", nullable = false)
    private Integer orderCount;     // 총 주문 횟수

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 생성일

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정일
    
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