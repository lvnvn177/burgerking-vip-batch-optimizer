package com.burgerking.coupon.service;

import com.burgerking.coupon.domain.Coupon;
import com.burgerking.coupon.domain.CouponIssuance;
import com.burgerking.coupon.domain.CouponStock;
import com.burgerking.coupon.domain.enums.CouponStatus;
import com.burgerking.coupon.repository.CouponIssuanceRepository;
import com.burgerking.coupon.repository.CouponRepository;
import com.burgerking.coupon.repository.CouponStockRepository;
import com.burgerking.coupon.web.dto.CouponRequest;
import com.burgerking.coupon.web.dto.CouponResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceNonOptimized implements CouponService{
    
    private final CouponRepository couponRepository;
    private final CouponStockRepository couponStockRepository;
    private final CouponIssuanceRepository couponIssuanceRepository;

       /**
     * 쿠폰 발급 메서드 (비최적화 버전)
     * 동시성 문제 발생 가능: 여러 사용자가 동시에 요청할 경우 재고 확인과 차감 사이에 Race Condition 발생
     */
    @Override
    @Transactional
    public CouponResponse issueCoupon(Long couponId, Long userId) {
        // 1. 쿠폰 존재 확인
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow( () -> new IllegalArgumentException("존재하지 않는 쿠폰입니다: " + couponId));

        // 2. 사용자가 이미 해당 쿠폰을 발급받았는지 확인
        if (couponIssuanceRepository.existsByUserIdAndCouponId(userId, couponId)) {
            throw new IllegalArgumentException("이미 발급받은 쿠폰입니다.");
        }

        // 3. 쿠폰 재고 확인
        CouponStock couponStock = couponStockRepository.findByCouponId(couponId)
            .orElseThrow( () -> new IllegalArgumentException("쿠폰 재고 정보가 없습니다."));
        
        
        // 4. 재고 확인 및 감소 (여기서 동시성 문제 발생)
        if (couponStock.getRemainingQuantity() <= 0) {
            throw new IllegalArgumentException("쿠폰이 모두 소진되었습니다.");
        }

        // 5. 재고 감소 (이 시점에서 다른 트랜잭션이 동시에 접근하면 문제 발생)
        couponStock.setRemainingQuantity(couponStock.getRemainingQuantity() - 1);
        couponStockRepository.save(couponStock);

        // 6. 쿠폰 발급 내역 생성
        CouponIssuance couponIssuance = CouponIssuance.builder()
            .userId(userId)
            .coupon(coupon)
            .couponCode(generateUniqueCouponCode())
            .status(CouponStatus.ACTIVE)
            .issuedAt(LocalDateTime.now())
            .expiresAt(coupon.getEndDate())
            .build();

        CouponIssuance savedIssuance = couponIssuanceRepository.save(couponIssuance);

        return CouponResponse.builder()
            .id(savedIssuance.getId())
            .couponId(coupon.getId())
            .couponName(coupon.getName())
            .couponCode(savedIssuance.getCouponCode())
            .status(savedIssuance.getStatus().name())
            .expiresAt(savedIssuance.getExpiresAt())
            .build();
    }


    @Override
    @Transactional
    public boolean useCoupon(String couponCode, String orderReference) {
        CouponIssuance issuance = couponIssuanceRepository.findByCouponCode(couponCode)
            .orElseThrow( () -> new IllegalArgumentException("존재하지 않는 쿠폰 코드입니다:" + couponCode));

        return issuance.use(orderReference);
    }

    @Override
    @Transactional
    public boolean cancelCoupon(String couponCode) {
        CouponIssuance issuance = couponIssuanceRepository.findByCouponCode(couponCode)
            .orElseThrow( () -> new IllegalArgumentException("존재하지 않는 쿠폰 코드입니다:" + couponCode));

        return issuance.cancel();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CouponResponse> getCouponById(Long couponId) {
        return couponRepository.findById(couponId)
            .map(coupon -> CouponResponse.builder()
                .id(null) // 발급 Id는 없음 (쿠폰 정보만 조회)
                .couponId(coupon.getId())
                .couponName(coupon.getName())
                .description(coupon.getDescription())
                .discountAmount(coupon.getDiscountAmount())
                .isPercentage(coupon.isPercentage())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getUserCoupons(Long userId) {
        List<CouponIssuance> issuances = couponIssuanceRepository.findByUserId(userId);
        
        return issuances.stream()
                .map(issuance -> CouponResponse.builder()
                        .id(issuance.getId())
                        .couponId(issuance.getCoupon().getId())
                        .couponName(issuance.getCoupon().getName())
                        .couponCode(issuance.getCouponCode())
                        .status(issuance.getStatus().name())
                        .issuedAt(issuance.getIssuedAt())
                        .expiresAt(issuance.getExpiresAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponRequest couponRequest) {
        // 1. 쿠폰 엔티티 생성
        Coupon coupon = Coupon.builder()
            .name(couponRequest.getName())
            .description(couponRequest.getDescription())
            .couponType(couponRequest.getCouponType())
            .discountAmount(couponRequest.getDiscountAmount())
            .isPercentage(couponRequest.isPercentage())
            .minimumOrderAmount(couponRequest.getMinmimumOrderAmount())
            .startDate(couponRequest.getStartDate())
            .endDate(couponRequest.getEndDate())
            .build();

        Coupon savedCoupon = couponRepository.save(coupon);

        // 2. 쿠폰 재고 정보 생성
        CouponStock couponStock = CouponStock.builder()
            .coupon(savedCoupon)
            .totalQuantity(couponRequest.getTotalQuantity())
            .remainingQuantity(couponRequest.getTotalQuantity())
            .build();
        
        couponStockRepository.save(couponStock);

        return CouponResponse.builder()
            .couponId(savedCoupon.getId())
            .couponName(savedCoupon.getName())
            .description(savedCoupon.getDescription())
            .startDate(savedCoupon.getStartDate())
            .endDate(savedCoupon.getEndDate())
            .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public int getRemainingQuantity(Long couponId) {
        CouponStock couponStock = couponStockRepository.findByCouponId(couponId)
            .orElseThrow( () -> new IllegalArgumentException("쿠폰 재고 정보가 없습니다."));

            return couponStock.getRemainingQuantity();
    }

    // 고유한 쿠폰 코드 생성 (UUID 기반)
    private String generateUniqueCouponCode() {
        return "CP_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    @Override
    @Transactional
    public CouponResponse issueCouponWithPessimisticLock(Long couponId, Long userId) {
        // 비최적화 서비스에서는 기본 발급 메소드를 호출
        return issueCoupon(couponId, userId);
    }

    @Override
    @Transactional
    public CouponResponse issueCouponWithOptimisticLock(Long couponId, Long userId) {
        // 비최적화 서비스에서는 기본 발급 메소드를 호출
        return issueCoupon(couponId, userId);
    }

    @Override
    @Transactional
    public CouponResponse issueCouponWithAtomicOperation(Long couponId, Long userId) {
        // 비최적화 서비스에서는 기본 발급 메소드를 호출
        return issueCoupon(couponId, userId);
    }

    @Override
    @Transactional
    public CouponResponse issueCouponWithRedisLock(Long couponId, Long userId) {
        // 비최적화 서비스에서는 기본 발급 메소드를 호출
        return issueCoupon(couponId, userId);
    }
}
