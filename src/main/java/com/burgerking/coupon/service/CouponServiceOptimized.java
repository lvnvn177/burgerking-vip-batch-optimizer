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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceOptimized implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponStockRepository couponStockRepository;
    private final CouponIssuanceRepository couponIssuanceRepository;
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String COUPON_LOCK_PREFIX = "coupon:lock:";
    private static final long LOCK_TIMEOUT = 3000; // 3초
    private static final int MAX_RETRY_COUNT = 3;
    
    /**
     * 쿠폰 발급 메서드 (최적화 버전) - 비관적 락 활용
     * 비관적 락을 사용하여 동시성 문제를 해결합니다.
     */
    @Override
    @Transactional
    public CouponResponse issueCouponWithPessimisticLock(String couponCode, Long userId) {
        // 1. 쿠폰 존재 확인
        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다: " + couponCode));

        // 2. 사용자가 이미 해당 쿠폰을 발급받았는지 확인
        if (couponIssuanceRepository.existsByUserIdAndCoupon_CouponCode(userId, couponCode)) {
            throw new IllegalStateException("이미 발급받은 쿠폰입니다.");
        }

        // 3. 쿠폰 재고 확인 (비관적 락 적용)
        CouponStock couponStock = couponStockRepository.findByCouponCodeWithPessimisticLock(couponCode)
                .orElseThrow(() -> new IllegalStateException("쿠폰 재고 정보가 없습니다."));

        // 4. 재고 확인 및 감소
        if (couponStock.getRemainingQuantity() <= 0) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }

        // 5. 재고 감소
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
    
    /**
     * 쿠폰 발급 메서드 (최적화 버전) - 낙관적 락 활용
     * 낙관적 락을 사용하여 동시성 문제를 해결합니다.
     * 충돌 발생 시 재시도 로직을 포함합니다.
     */
    @Override
    @Transactional
    @Retryable(
        value = {OptimisticLockingFailureException.class}, 
        maxAttempts = MAX_RETRY_COUNT, 
        backoff = @Backoff(delay = 100, maxDelay = 500, multiplier = 1.5)
    )
    public CouponResponse issueCouponWithOptimisticLock(String couponCode, Long userId) {
        // 1. 쿠폰 존재 확인
        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다: " + couponCode));

        // 2. 사용자가 이미 해당 쿠폰을 발급받았는지 확인
        if (couponIssuanceRepository.existsByUserIdAndCoupon_CouponCode(userId, couponCode)) {
            throw new IllegalStateException("이미 발급받은 쿠폰입니다.");
        }

        // 3. 쿠폰 재고 확인 (낙관적 락은 version 필드를 활용)
        CouponStock couponStock = couponStockRepository.findByCoupon_CouponCode(couponCode)
                .orElseThrow(() -> new IllegalStateException("쿠폰 재고 정보가 없습니다."));

        // 4. 재고 확인 및 감소
        if (couponStock.getRemainingQuantity() <= 0) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }

        // 5. 재고 감소 (이 시점에서 다른 트랜잭션이 변경하면 OptimisticLockingFailureException 발생)
        couponStock.setRemainingQuantity(couponStock.getRemainingQuantity() - 1);
        couponStockRepository.save(couponStock); // 버전 불일치 시 예외 발생

        // 6. 쿠폰 발급 내역 생성
        CouponIssuance couponIssuance = CouponIssuance.builder()
                .userId(userId)
                .coupon(coupon)
                .couponCode(generateUniqueCouponCode())
                .status(CouponStatus.ACTIVE)
                .issuedAt(LocalDateTime.now())
                .expiresAt(coupon.getEndDate())
                .build();

        try {
            CouponIssuance savedIssuance = couponIssuanceRepository.save(couponIssuance);

            return CouponResponse.builder()
                    .id(savedIssuance.getId())
                    .couponId(coupon.getId())
                    .couponName(coupon.getName())
                    .couponCode(savedIssuance.getCouponCode())
                    .status(savedIssuance.getStatus().name())
                    .expiresAt(savedIssuance.getExpiresAt())
                    .build();
        } catch (DataIntegrityViolationException e) {
            // 중복 발급 시도 등으로 인한 제약조건 위반 예외 처리
            log.warn("쿠폰 발급 중 데이터 무결성 위반 발생: {}", e.getMessage());
            throw new IllegalStateException("쿠폰 발급에 실패했습니다. 이미 발급되었거나 시스템 오류입니다.");
        }
    }
    
    /**
     * 쿠폰 발급 메서드 (최적화 버전) - 원자적 DB 연산 활용
     * 단일 쿼리로 재고 확인과 감소를 원자적으로 수행합니다.
     */
    @Override
    @Transactional
    public CouponResponse issueCouponWithAtomicOperation(String couponCode, Long userId) {
        // 1. 쿠폰 존재 확인
        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다: " + couponCode));

        // 2. 사용자가 이미 해당 쿠폰을 발급받았는지 확인
        if (couponIssuanceRepository.existsByUserIdAndCoupon_CouponCode(userId, couponCode)) {
            throw new IllegalStateException("이미 발급받은 쿠폰입니다.");
        }

        // 3. 원자적 재고 감소 (단일 쿼리로 수행)
        int updatedRows = couponStockRepository.decreaseStockAtomic(couponCode);
        if (updatedRows == 0) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }

        // 4. 쿠폰 발급 내역 생성
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
    
    /**
     * 쿠폰 발급 메서드 (최적화 버전) - Redis 분산 락 활용
     * Redis를 활용한 분산 락으로 동시성 문제를 해결합니다.
     */
    @Override
    @Transactional
    public CouponResponse issueCouponWithRedisLock(String couponCode, Long userId) {
        String lockKey = COUPON_LOCK_PREFIX + couponCode;
        String lockValue = UUID.randomUUID().toString();

        // 1. Redis 락 획득 시도
        boolean lockAcquired = acquireLock(lockKey, lockValue, LOCK_TIMEOUT);
        if (!lockAcquired) {
            throw new IllegalStateException("쿠폰 발급 요청이 많아 처리할 수 없습니다. 잠시 후 다시 시도해주세요.");
        }

        try {
            // 2. 쿠폰 존재 확인
            Coupon coupon = couponRepository.findByCouponCode(couponCode)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다: " + couponCode));

            // 3. 사용자가 이미 해당 쿠폰을 발급받았는지 확인
            if (couponIssuanceRepository.existsByUserIdAndCoupon_CouponCode(userId, couponCode)) {
                throw new IllegalStateException("이미 발급받은 쿠폰입니다.");
            }

            // 4. 쿠폰 재고 확인
            CouponStock couponStock = couponStockRepository.findByCoupon_CouponCode(couponCode)
                    .orElseThrow(() -> new IllegalStateException("쿠폰 재고 정보가 없습니다."));

            // 5. 재고 확인 및 감소
            if (couponStock.getRemainingQuantity() <= 0) {
                throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
            }

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
        } finally {
            // 락 해제
            releaseLock(lockKey, lockValue);
        }
    }
    
        // 기본 issueCoupon 메서드는 원자적 연산 방식 사용
    @Override
    @Transactional
    public CouponResponse issueCoupon(String couponCode, Long userId) {
        // 기본적으로 원자적 연산 방식을 사용합니다
        return issueCouponWithAtomicOperation(couponCode, userId);
    }
    
    @Override
    @Transactional
    public boolean useCoupon(String couponCode, String orderReference) {
        CouponIssuance issuance = couponIssuanceRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 코드입니다: " + couponCode));
        
        return issuance.use(orderReference);
    }
    
    @Override
    @Transactional
    public boolean cancelCoupon(String couponCode) {
        CouponIssuance issuance = couponIssuanceRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 코드입니다: " + couponCode));
        
        return issuance.cancel();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CouponResponse> getCouponByCode(String couponCode) {
        return couponRepository.findByCouponCode(couponCode)
                .map(coupon -> CouponResponse.builder()
                        .id(null) // 발급 ID는 없음 (쿠폰 정보만 조회)
                        .couponId(coupon.getId())
                        .couponCode(coupon.getCouponCode())
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
                .couponCode(couponRequest.getCouponCode()) // Add this line
                .name(couponRequest.getName())
                .description(couponRequest.getDescription())
                .couponType(couponRequest.getCouponType())
                .discountAmount(couponRequest.getDiscountAmount())
                .isPercentage(couponRequest.isPercentage())
                .minimumOrderAmount(couponRequest.getMinimumOrderAmount())
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
                .couponCode(savedCoupon.getCouponCode())
                .couponName(savedCoupon.getName())
                .description(savedCoupon.getDescription())
                .startDate(savedCoupon.getStartDate())
                .endDate(savedCoupon.getEndDate())
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public int getRemainingQuantity(String couponCode) {
        CouponStock couponStock = couponStockRepository.findByCoupon_CouponCode(couponCode)
                .orElseThrow(() -> new IllegalStateException("쿠폰 재고 정보가 없습니다."));
        return couponStock.getRemainingQuantity();
    }

    @Override
    public Optional<CouponResponse> getCouponById(Long couponId) {
        return Optional.empty();
    }
    
    // Redis 분산 락 획득
    private boolean acquireLock(String lockKey, String lockValue, long timeoutMillis) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, timeoutMillis, TimeUnit.MILLISECONDS);
        return success != null && success;
    }
    
    // Redis 분산 락 해제
    private void releaseLock(String lockKey, String lockValue) {
        String currentValue = redisTemplate.opsForValue().get(lockKey);
        if (lockValue.equals(currentValue)) {
            redisTemplate.delete(lockKey);
        }
    }
    
    // 고유한 쿠폰 코드 생성 (UUID 기반)
    private String generateUniqueCouponCode() {
        return "CP_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}