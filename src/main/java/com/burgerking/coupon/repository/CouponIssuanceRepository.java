package com.burgerking.coupon.repository;

import com.burgerking.coupon.domain.CouponIssuance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CouponIssuanceRepository extends JpaRepository<CouponIssuance, Long> {
    
    // 특정 사용자가 특정 쿠폰을 이미 발급받았는지 확인하는 쿼리
    Optional<CouponIssuance> findByUserIdAndCouponId(Long userId, Long couponId);

    // 특정 사용자가 특정 쿠폰을 이미 발급받았는지 여부만 확인 (존재 여부)
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    // 고유 쿠폰 코드로 발급 내역 찾기 ex) 쿠폰 사용 시 유효성 검사
    Optional<CouponIssuance> findByCouponCode(String couponCode);

    List<CouponIssuance> findByUserId(Long userId);
}
