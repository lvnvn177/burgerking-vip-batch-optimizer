package com.burgerking.coupon.service;

import com.burgerking.coupon.domain.Coupon;
import com.burgerking.coupon.domain.CouponStock;
import com.burgerking.coupon.domain.enums.CouponType;
import com.burgerking.coupon.repository.CouponRepository;
import com.burgerking.coupon.repository.CouponStockRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;



@DataJpaTest
public class CouponStockRepositoryTest {
    
    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponStockRepository couponStockRepository;

    @Test
    @DisplayName("쿠폰 재고 조회 테스트")
    void findByCouponIdTest() {
        // Given
        Coupon coupon = createTestCoupon();
        couponRepository.save(coupon);

        CouponStock stock = CouponStock.builder()
            .coupon(coupon)
            .totalQuantity(100)
            .remainingQuantity(100)
            .build();
        couponStockRepository.save(stock);

        // When
        Optional<CouponStock> foundStock = couponStockRepository.findByCoupon_CouponCode(coupon.getCouponCode());

        // Then
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getRemainingQuantity()).isEqualTo(100);
    }

    @Test
    @DisplayName("쿠폰 재고 원자력 감소 테스트")
    void decreaseStockAtomicTest() {
        // Given
        Coupon coupon = createTestCoupon();
        couponRepository.save(coupon);

        CouponStock stock = CouponStock.builder()
            .coupon(coupon)
            .totalQuantity(100)
            .remainingQuantity(50)
            .build();
        couponStockRepository.save(stock);

        // When
        int updatedRows = couponStockRepository.decreaseStockAtomic(coupon.getCouponCode());

        // Then
        assertThat(updatedRows).isEqualTo(1);

        CouponStock updatedStock = couponStockRepository.findById((stock.getId())).get();
        assertThat(updatedStock.getRemainingQuantity()).isEqualTo(49);
    }

    @Test
    @DisplayName("쿠폰 재고 부족 시 원자적 감소 실패 테스트")
    void decreaseStockAtomicFailWhenNoStockTest() {
        // Given
        Coupon coupon = createTestCoupon();
        couponRepository.save(coupon);

        CouponStock stock = CouponStock.builder()
            .coupon(coupon)
            .totalQuantity(100)
            .remainingQuantity(0)
            .build();
        couponStockRepository.save(stock);

        // When
        int updatedRows = couponStockRepository.decreaseStockAtomic(coupon.getCouponCode());

        // Then
        assertThat(updatedRows).isEqualTo(0); // 업데이트된 행이 없어야 함 

        CouponStock notUpdatedStock = couponStockRepository.findById(stock.getId()).get();
        assertThat(notUpdatedStock.getRemainingQuantity()).isEqualTo(0); // 여전히 0이어야 함 
    }

    private Coupon createTestCoupon() {
        return Coupon.builder()
            .couponCode("TEST_COUPON")
            .name("테스트 쿠폰")
            .description("테스트용 쿠폰입니다")
            .couponType(CouponType.FIXED_AMOUNT)
            .discountAmount(BigDecimal.valueOf(5000))
            .isPercentage(false)
            .minimumOrderAmount(BigDecimal.valueOf(10000))
            .startDate(LocalDateTime.now().minusDays(1))
            .endDate(LocalDateTime.now().plusDays(30))
            .build();
    }


}
