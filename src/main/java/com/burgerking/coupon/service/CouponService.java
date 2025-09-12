package com.burgerking.coupon.service;

import com.burgerking.common.lock.DistributedLockOperation;
import com.burgerking.coupon.web.dto.CouponRequest;
import com.burgerking.coupon.web.dto.CouponResponse;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface CouponService {
    
    // 쿠폰 발급 (사용자에게 쿠폰 발급)
    @Transactional  
    @DistributedLockOperation(key = "coupon:issue:#couponCode", timeoutMs = 3000)
    CouponResponse issueCoupon(String couponCode, Long userId);

    CouponResponse issueCouponWithPessimisticLock(String couponCode, Long userId);
    CouponResponse issueCouponWithOptimisticLock(String couponCode, Long userId);
    CouponResponse issueCouponWithAtomicOperation(String couponCode, Long userId);
    CouponResponse issueCouponWithRedisLock(String couponCode, Long userId);

    // 쿠폰 사용
    boolean useCoupon(String couponCode, String orderReference);

    // 쿠폰 취소 (사용 취소)
    boolean cancelCoupon(String couponCode);

    // 쿠폰 정보 조회
    Optional<CouponResponse> getCouponByCode(String couponCode);

    // 사용자의 쿠폰 목록 조회
    List<CouponResponse> getUserCoupons(Long userId);

    // 쿠폰 생성 (관리자 기능)
    CouponResponse createCoupon(CouponRequest couponRequest);

    // 쿠폰 재고 확인
    int getRemainingQuantity(String couponCode);

    Optional<CouponResponse> getCouponById(Long couponId);
}
