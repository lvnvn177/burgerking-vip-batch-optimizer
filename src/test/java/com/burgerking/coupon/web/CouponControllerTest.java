package com.burgerking.coupon.web;

import com.burgerking.coupon.domain.Coupon;
import com.burgerking.coupon.domain.CouponStock;
import com.burgerking.coupon.domain.enums.CouponType;
import com.burgerking.coupon.repository.CouponRepository;
import com.burgerking.coupon.repository.CouponStockRepository;
import com.burgerking.coupon.repository.IssuedCouponRepository;
import com.burgerking.coupon.web.dto.CouponIssueRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CouponControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponStockRepository couponStockRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;
    
    @BeforeEach
    void setUp() {
        // 테스트 전 데이터 정리
        issuedCouponRepository.deleteAll();;
        couponRepository.deleteAll();;

        
    }


    private void createTestCoupon() {
        Coupon testCoupon = Coupon.builder()
            .name("테스트 쿠폰")
            .couponCode("TEST001")
            .discountAmount(new BigDecimal("1000"))
            .isPercentage(false)
            .minimumOrderAmount(new BigDecimal("0"))
            .couponType(CouponType.FIXED_AMOUNT)
            .startDate(LocalDateTime.now().minusDays(1))
            .endDate(LocalDateTime.now().plusDays(7))
            .build();
        
        // 쿠폰 저장
        Coupon savedCoupon = couponRepository.save(testCoupon);
        
        // 쿠폰 재고 생성 및 저장
        CouponStock couponStock = CouponStock.builder()
            .coupon(savedCoupon)
            .totalQuantity(100) 
            .remainingQuantity(100) // 초기 남은 수량도 동일하게 설정
            .build();
        
        couponStockRepository.save(couponStock);
    }

    @Test
    @DisplayName("쿠폰 발급 동시성 테스트")
    void issueCouponConcurrencyTest() throws Exception {
        createTestCoupon();
        String couponCode = "TEST001";
        int numberOfThreads = 150;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            long userId = 1000L + i;
            executorService.submit(() -> {
                try {
                    CouponIssueRequest request = new CouponIssueRequest(userId, couponCode);

                    mockMvc.perform(post("/api/coupons/issue")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                            .andReturn();
                } catch (Exception e) {
                    // 예외 처리
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        // Then
        Optional<CouponStock> stock = couponStockRepository.findByCoupon_CouponCode(couponCode);
        assertTrue(stock.isPresent());
        assertEquals(0, stock.get().getRemainingQuantity());
        long issuedCount = issuedCouponRepository.count();
        assertEquals(100, issuedCount);
    }


}
