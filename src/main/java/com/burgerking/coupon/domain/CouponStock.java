package com.burgerking.coupon.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "coupon_stocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponStock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false, unique = true)
    private Coupon coupon; // 연관된 쿠폰

    @Column(nullable = false)
    private Integer totalQuantity; // 총 발행 수량

    @Column(nullable = false)
    private Integer remainingQuantity; // 남은 수량

    @Version
    private Long version; // 낙관적 락을 위한 버전 필드

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    // 쿠폰 재고 감소 메서드 (원자적 연산을 위한 비즈니스 로직)
    public boolean decreaseStock() {
        if (remainingQuantity <= 0) {
            return false; // 재고 부족 
        }
        remainingQuantity--;
        return true;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
