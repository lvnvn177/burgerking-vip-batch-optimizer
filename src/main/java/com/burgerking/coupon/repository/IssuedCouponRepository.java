package com.burgerking.coupon.repository;

import com.burgerking.coupon.domain.CouponIssuance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface IssuedCouponRepository extends JpaRepository<CouponIssuance, Long> {
    
    // 특정 사용자가 특정 쿠폰을 이미 발급받았는지 확인하는 쿼리 (코드 기반으로 변경)
    Optional<CouponIssuance> findByUserIdAndCoupon_Code(Long userId, String couponCode);

    // 특정 사용자가 특정 쿠폰을 이미 발급받았는지 여부만 확인 (존재 여부) (코드 기반으로 변경)
    boolean existsByUserIdAndCoupon_Code(Long userId, String couponCode);

    // 고유 쿠폰 코드로 발급 내역 찾기 ex) 쿠폰 사용 시 유효성 검사
    Optional<CouponIssuance> findByCouponCode(String couponCode);

    // 사용자 ID로 발급 내역 찾기
    List<CouponIssuance> findByUserId(Long userId);
    
    // 여러 사용자 ID와 쿠폰 코드로 발급 건수 조회 (동시성 테스트용)
    long countByUserIdInAndCoupon_Code(Iterable<Long> userIds, String couponCode);
}