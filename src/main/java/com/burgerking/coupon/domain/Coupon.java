package com.burgerking.coupon.domain;

// import com.burgerking.coupon.domain.enums.CouponStatus;
import com.burgerking.coupon.domain.enums.CouponType;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;  // 쿠폰 이름 ex) 골든 패티 쿠폰 

    @Column(nullable = false, unique = true, length = 50)
    private String couponCode; // 쿠폰 코드

    @Column(length = 500)
    private String description; // 쿠폰 설명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType couponType; // 쿠폰 유형 (할인, 무료 메뉴, 1+1 등)

    @Column(nullable = false)
    private BigDecimal discountAmount; // 할인 금액 또는 비율

    @Column(nullable = false)
    private boolean isPercentage; // 퍼센트 할인 여부

    @Column(nullable = false)
    private BigDecimal minimumOrderAmount; // 최소 주문 금액

    @Column(nullable = false)
    private LocalDateTime startDate; // 쿠폰 유효 시작일

    @Column(nullable = false)
    private LocalDateTime endDate; // 쿠폰 유효 종료일

    @Column(nullable = false)
    private LocalDateTime createdAt; // 쿠폰 생성 시간

    @Column
    private LocalDateTime updateAt; // 쿠폰 정보 수정 시간

    @OneToOne(mappedBy = "coupon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CouponStock couponStock;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CouponIssuance> couponIssuances = new ArrayList<>(); // 쿠폰 발급 내역 (1:N 관계)

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}
