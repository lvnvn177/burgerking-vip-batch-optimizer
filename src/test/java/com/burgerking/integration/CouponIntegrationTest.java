package com.burgerking.integration;

import com.burgerking.coupon.domain.Coupon;
import com.burgerking.coupon.domain.CouponIssuance;
import com.burgerking.coupon.domain.CouponStock;
import com.burgerking.coupon.domain.enums.CouponStatus;
import com.burgerking.coupon.domain.enums.CouponType;
import com.burgerking.coupon.repository.CouponIssuanceRepository;
import com.burgerking.coupon.repository.CouponRepository;
import com.burgerking.coupon.repository.CouponStockRepository;
import com.burgerking.coupon.web.dto.CouponIssueRequest;
import com.burgerking.coupon.web.dto.CouponRequest;
import com.burgerking.coupon.web.dto.CouponUseRequest;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CouponIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired 
    private CouponRepository couponRepository;

    @Autowired
    private CouponStockRepository couponStockRepository;

    @Autowired
    private CouponIssuanceRepository couponIssuanceRepository;

    private String testCouponCode;
    private final Long TEST_USER_ID = 100L;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화 - 각 테스트 실행 전에 새로운 쿠폰 생성
        couponRepository.deleteAll();
        couponStockRepository.deleteAll();
        couponIssuanceRepository.deleteAll();

        // 테스트 쿠폰 생성
        Coupon coupon = Coupon.builder()
                .couponCode("INTEGRATION_TEST")
                .name("통합 테스트 쿠폰")
                .description("통합 테스트용 쿠폰입니다")
                .couponType(CouponType.FIXED_AMOUNT)
                .discountAmount(BigDecimal.valueOf(5000))
                .isPercentage(false)
                .minimumOrderAmount(BigDecimal.valueOf(10000))
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .build();

        Coupon savedCoupon = couponRepository.save(coupon);
        testCouponCode = savedCoupon.getCouponCode();

        // 쿠폰 재고 생성
        CouponStock couponStock = CouponStock.builder()
                .coupon(savedCoupon)
                .totalQuantity(100)
                .remainingQuantity(10) // 적은 수량으로 설정하여 재고 소진 테스트에 용이
                .build();

        couponStockRepository.save(couponStock);
    }

       @Test
    @DisplayName("쿠폰 생성 통합 테스트")
    void createCouponTest() throws Exception {
        // Given
        CouponRequest request = CouponRequest.builder()
            .couponCode("NEW_COUPON")
            .name("새 통합 테스트 쿠폰")
            .description("새로 생성된 통합 테스트 쿠폰입니다")
            .couponType(CouponType.FIXED_AMOUNT)
            .discountAmount(BigDecimal.valueOf(3000))
            .isPercentage(false)
            .minimumOrderAmount(BigDecimal.valueOf(5000))
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusDays(15))
            .totalQuantity(200)
            .build();


        // When & Then
        MvcResult result = mockMvc.perform(post("/api/coupons")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists()) // 새로 생성된 쿠폰의 ID가 응답에 포함되어 있는지 확인
            .andExpect(jsonPath("$.couponName").value("새 통합 테스트 쿠폰"))
            .andExpect(jsonPath("$.description").value("새로 생성된 통합 테스트 쿠폰입니다"))
            .andReturn();

        // MvcResult에서 응답 본문을 파싱하여 생성된 쿠폰의 ID를 가져옵니다.
        String responseContent = result.getResponse().getContentAsString();
        // CouponResponse DTO에 'id' 필드가 있다고 가정합니다.
        String newCouponCode = objectMapper.readTree(responseContent).get("couponCode").asText();

        // DB에 정상적으로 저장되었는지 확인 (새로운 쿠폰 코드로 조회)
        Optional<Coupon> createdCouponOpt = couponRepository.findByCouponCode(newCouponCode);
        assertThat(createdCouponOpt).isPresent();
        assertThat(createdCouponOpt.get().getDescription()).isEqualTo("새로 생성된 통합 테스트 쿠폰입니다");

        // 재고도 함께 생성되었는지 확인
        Optional<CouponStock> stock = couponStockRepository.findByCoupon_CouponCode(newCouponCode);
        assertThat(stock).isPresent();
        assertThat(stock.get().getTotalQuantity()).isEqualTo(200);
        assertThat(stock.get().getRemainingQuantity()).isEqualTo(200);
    }

    @Test
    @DisplayName("쿠폰 조회 통합 테스트")
    void getCouponTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/coupons/{couponCode}", testCouponCode)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponName").value("통합 테스트 쿠폰"))
                .andExpect(jsonPath("$.couponCode").value(testCouponCode))
                .andExpect(jsonPath("$.description").value("통합 테스트용 쿠폰입니다"));
    }
    

    @Test
    @DisplayName("쿠폰 발급 성공 통합 테스트")
    void issueCouponSuccessTest() throws Exception {
        // Given
        CouponIssueRequest request = new CouponIssueRequest(TEST_USER_ID, testCouponCode);

        // When & Then
        mockMvc.perform(post("/api/coupons/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 같은 사용자가 같은 쿠폰 재발급 시도
        mockMvc.perform(post("/api/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("쿠폰 재고 소진 시 발급 실패 테스트")
    void issueCouponSoldOutTest() throws Exception {
        // Given - 재고를 모두 소진시킴
        CouponStock stock = couponStockRepository.findByCoupon_CouponCode(testCouponCode).orElseThrow();
        stock.setRemainingQuantity(0);
        couponStockRepository.save(stock);

        // When & Then
        CouponIssueRequest request = new CouponIssueRequest(TEST_USER_ID, testCouponCode);
        mockMvc.perform(post("/api/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("쿠폰 사용 통합 테스트")
    void useCouponTest() throws Exception {
        // Given - 먼저 쿠폰을 발급받음
        CouponIssueRequest issueRequest = new CouponIssueRequest(TEST_USER_ID, testCouponCode);
        mockMvc.perform(post("/api/coupons/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(issueRequest)))
                .andExpect(status().isOk());

        // 발급된 쿠폰 코드 확인
        List<CouponIssuance> issuances = couponIssuanceRepository.findByUserId(TEST_USER_ID);
        String couponCode = issuances.get(0).getCouponCode();

        // When & Then - 쿠폰 사용
        CouponUseRequest useRequest = new CouponUseRequest(couponCode, "ORDER-12345");
        mockMvc.perform(post("/api/coupons/use")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(useRequest)))
                .andExpect(status().isOk());

        // DB에서 쿠폰 상태가 변경되었는지 확인
        CouponIssuance updatedIssuance = couponIssuanceRepository.findByUserId(TEST_USER_ID).get(0);
        assertThat(updatedIssuance.getStatus()).isEqualTo(CouponStatus.USED);
        assertThat(updatedIssuance.getOrderReference()).isEqualTo("ORDER-12345");
    }

    @Test
    @DisplayName("사용자 쿠폰 목록 조회 통합 테스트")
    void getUserCouponsTest() throws Exception {
        // Given - 먼저 쿠폰을 발급받음
        CouponIssueRequest request = new CouponIssueRequest(TEST_USER_ID, testCouponCode);
        mockMvc.perform(post("/api/coupons/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());


        // When & Then
        mockMvc.perform(get("/api/coupons/user/{userId}", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].couponCode").value(testCouponCode))
                .andExpect(jsonPath("$[0].couponName").value("통합 테스트 쿠폰"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }
}
