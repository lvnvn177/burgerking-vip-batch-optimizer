package com.burgerking.coupon.domain;

import com.burgerking.coupon.domain.enums.CouponStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;


import java.time.LocalDateTime;



@Entity
@Table(name = "coupon_issuances", 
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "coupon_id"}))
        // 사용자 별 쿠폰 중복 발급 방지 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponIssuance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // 사용자 ID (실제 사용자 엔티티는 별도 서비스에 있다고 가정)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon; // 발급된 쿠폰 

    @Column(nullable = false, unique = true, length = 50)
    private String couponCode; // 고유한 쿠폰 코드

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status; // 쿠폰 상태 (사용 가능, 사용됨, 만료됨 등)

    @Column(nullable = false)
    private LocalDateTime issuedAt; // 발급 시간

    @Column
    private LocalDateTime usedAt; // 사용 시간

    @Column(nullable = false)
    private LocalDateTime expiresAt; // 만료 시간

    @Column
    private String orderReference; // 쿠폰 사용된 주문 참조 정보 

    @PrePersist
    protected void onCreate() {
        issuedAt = LocalDateTime.now();
        status = CouponStatus.ACTIVE; // 기본 상태는 활성화
    }

    // 쿠폰 사용 처리 메소드
    public boolean use(String orderRef) {
        if (status != CouponStatus.ACTIVE) {
            return false; // 이미 사용되었거나 만료됨 
        }

        // 현재 시간이 만료 시간을 지났는지 확인
        if (LocalDateTime.now().isAfter(expiresAt)) {
            status = CouponStatus.EXPIRED;
            return false; // 만료된 쿠폰
        }

        // 쿠폰 사용 처리
        status = CouponStatus.USED;
        usedAt = LocalDateTime.now();
        orderReference = orderRef;
        return true;
    }

    // 쿠폰 만료 처리 메서드
    public void expire() {
        if (status == CouponStatus.ACTIVE) {
            status = CouponStatus.EXPIRED;
        }
    }

    // 쿠폰 취소 처리 메서드 (사용 후 취소 등의 상황)
    public boolean cancel() {
        if (status != CouponStatus.USED) {
            return false; // 사용된 상태가 아니면 취소 불가
        }

        // 사용 취소 처리
        status = CouponStatus.ACTIVE;
        usedAt = null;
        orderReference = null;
        return true;
    }
}
