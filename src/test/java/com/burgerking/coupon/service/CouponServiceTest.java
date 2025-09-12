package com.burgerking.coupon.service;

import com.burgerking.coupon.domain.Coupon;
import com.burgerking.coupon.domain.CouponIssuance;
import com.burgerking.coupon.domain.CouponStock;
import com.burgerking.coupon.domain.enums.CouponStatus;
import com.burgerking.coupon.domain.enums.CouponType;
import com.burgerking.coupon.repository.CouponIssuanceRepository;
import com.burgerking.coupon.repository.CouponRepository;
import com.burgerking.coupon.repository.CouponStockRepository;
import com.burgerking.coupon.web.dto.CouponRequest;
import com.burgerking.coupon.web.dto.CouponResponse;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponStockRepository couponStockRepository;

    @Mock
    private CouponIssuanceRepository couponIssuanceRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private CouponServiceOptimized couponService;

    private Coupon testCoupon;
    private CouponRequest testCouponRequest;
    private final String TEST_COUPON_CODE = "TEST001";
    private final Long TEST_USER_ID = 100L;

    @BeforeEach
    void setUp() {
        // 테스트 쿠폰 객체 생성
        testCoupon = Coupon.builder()
                .id(1L)
                .couponCode(TEST_COUPON_CODE)
                .name("테스트 쿠폰")
                .description("테스트용 쿠폰입니다")
                .couponType(CouponType.FIXED_AMOUNT)
                .discountAmount(BigDecimal.valueOf(5000))
                .isPercentage(false)
                .minimumOrderAmount(BigDecimal.valueOf(10000))
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .build();

    
      // 쿠폰 생성 요청 DTO 생성
        testCouponRequest = CouponRequest.builder()
                .name("새 쿠폰")
                .couponCode("NEW001")
                .description("새로운 테스트 쿠폰")
                .couponType(CouponType.FIXED_AMOUNT)
                .discountAmount(BigDecimal.valueOf(3000))
                .isPercentage(false)
                .minimumOrderAmount(BigDecimal.valueOf(5000))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(15))
                .totalQuantity(200)
                .build();   
        
        // RedisTemplate Mock 설정
        given(redisTemplate.opsForValue()).willReturn(valueOperations); 
    }

    @Test
    @DisplayName("쿠폰 생성 테스트")
    void createCouponTest() {
        // Given
        given(couponRepository.save(any(Coupon.class))).willReturn(testCoupon);

        // When
        CouponResponse response = couponService.createCoupon(testCouponRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCouponName()).isEqualTo(testCoupon.getName());
        verify(couponRepository, times(1)).save(any(Coupon.class));
        verify(couponStockRepository, times(1)).save(any(CouponStock.class));
    }

    @Test
    @DisplayName("쿠폰 발급 - 기본 성공 테스트")
    void issueCouponSuccessTest() {
        // Given
        given(couponRepository.findByCouponCode(TEST_COUPON_CODE)).willReturn(Optional.of(testCoupon));
        given(couponIssuanceRepository.existsByUserIdAndCoupon_CouponCode(TEST_USER_ID, TEST_COUPON_CODE)).willReturn(false);
        given(couponStockRepository.decreaseStockAtomic(TEST_COUPON_CODE)).willReturn(1);

        CouponIssuance issuance = CouponIssuance.builder()
            .id(1L)
            .userId(TEST_USER_ID)
            .coupon(testCoupon)
            .couponCode("TEST_CODE")
            .status(CouponStatus.ACTIVE)
            .issuedAt(LocalDateTime.now())
            .expiresAt(testCoupon.getEndDate())
            .build();
        
        given(couponIssuanceRepository.save(any(CouponIssuance.class))).willReturn(issuance);

        // When
        CouponResponse response = couponService.issueCoupon(TEST_COUPON_CODE, TEST_USER_ID);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCouponCode()).isEqualTo("TEST_CODE");
        assertThat(response.getStatus()).isEqualTo(CouponStatus.ACTIVE.name());
    }

    @Test
    @DisplayName("쿠폰 발급 - 재고 없음 테스트")
    void issueCouponNoStockTest() {
        // Given
        given(couponRepository.findByCouponCode(TEST_COUPON_CODE)).willReturn(Optional.of(testCoupon));
        given(couponIssuanceRepository.existsByUserIdAndCoupon_CouponCode(TEST_USER_ID, TEST_COUPON_CODE)).willReturn(false);
        given(couponStockRepository.decreaseStockAtomic(TEST_COUPON_CODE)).willReturn(0);

        // When & Then
        assertThatThrownBy(() -> couponService.issueCoupon(TEST_COUPON_CODE, TEST_USER_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("쿠폰이 모두 소진되었습니다.");
    }

    @Test
    @DisplayName("쿠폰 발급 - 이미 발급받은 쿠폰 테스트")
    void issueCouponAlreadyIssuedTest() {
        // Given
        given(couponRepository.findByCouponCode(TEST_COUPON_CODE)).willReturn(Optional.of(testCoupon));
        given(couponIssuanceRepository.existsByUserIdAndCoupon_CouponCode(TEST_USER_ID, TEST_COUPON_CODE)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> couponService.issueCoupon(TEST_COUPON_CODE, TEST_USER_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 발급받은 쿠폰입니다.");
    }

    @Test
    @DisplayName("쿠폰 사용 테스트")
    void useCouponTest() {
        // Given
        String couponCode = "TEST_CODE";
        String orderReference = "ORDER-123";

        CouponIssuance issuance = CouponIssuance.builder()
            .id(1L)
            .userId(TEST_USER_ID)
            .coupon(testCoupon)
            .couponCode(couponCode)
            .status(CouponStatus.ACTIVE)
            .issuedAt(LocalDateTime.now())
            .expiresAt(testCoupon.getEndDate())
            .build();

        
        given(couponIssuanceRepository.findByCouponCode(couponCode)).willReturn(Optional.of(issuance));

        // When
        boolean result = couponService.useCoupon(couponCode, orderReference);

        // Then
        assertThat(result).isTrue();
        verify(couponIssuanceRepository, times(1)).save(any(CouponIssuance.class));
    }

    @Test
    @DisplayName("쿠폰 취소 테스트")
    void cancelCouponTest() {
        //Given
        String couponCode = "TEST_CODE";

        CouponIssuance issuance = CouponIssuance.builder()
            .id(1L)
            .userId(TEST_USER_ID)
            .coupon(testCoupon)
            .couponCode(couponCode)
            .status(CouponStatus.USED)
            .issuedAt(LocalDateTime.now())
            .expiresAt(testCoupon.getEndDate())
            .build();

        
        given(couponIssuanceRepository.findByCouponCode(couponCode)).willReturn(Optional.of(issuance));


        // When
        boolean result = couponService.cancelCoupon(couponCode);

        // Then
        assertThat(result).isTrue();
        verify(couponIssuanceRepository, times(1)).save(any(CouponIssuance.class));
    }
    
}