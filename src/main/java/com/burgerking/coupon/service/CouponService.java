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
    @DistributedLockOperation(key = "coupon:issue:#couponId", timeoutMs = 3000)
    CouponResponse issueCoupon(Long couponId, Long userId);

    CouponResponse issueCouponWithPessimisticLock(Long couponId, Long userId);
    CouponResponse issueCouponWithOptimisticLock(Long couponId, Long userId);
    CouponResponse issueCouponWithAtomicOperation(Long couponId, Long userId);
    CouponResponse issueCouponWithRedisLock(Long couponId, Long userId);

    // 쿠폰 사용
    boolean useCoupon(String couponCode, String orderReference);

    // 쿠폰 취소 (사용 취소)
    boolean cancelCoupon(String couponCode);

    // 쿠폰 정보 조회
    Optional<CouponResponse> getCouponById(Long couponId);

    // 사용자의 쿠폰 목록 조회
    List<CouponResponse> getUserCoupons(Long userId);

    // 쿠폰 생성 (관리자 기능)
    CouponResponse createCoupon(CouponRequest couponRequest);

    // 쿠폰 재고 확인
    int getRemainingQuantity(Long couponId);
}
