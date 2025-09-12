package com.burgerking.coupon.service;


import com.burgerking.coupon.web.CouponController;
import com.burgerking.coupon.web.dto.CouponIssueRequest;
import com.burgerking.coupon.web.dto.CouponResponse;
import com.burgerking.coupon.exception.CouponException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 

@WebMvcTest(CouponController.class)
public class CouponControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰 정보 조회 테스트")
    void getCouponTest() throws Exception {
        // Given
        CouponResponse response = CouponResponse.builder()
            .couponId(1L)
            .couponCode("TEST_COUPON")
            .couponName("테스트 쿠폰")
            .description("테스트용 쿠폰입니다")
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusDays(30))
            .build();

        given(couponService.getCouponByCode("TEST_COUPON")).willReturn(Optional.of(response));

        // When & Them
        mockMvc.perform(get("/api/coupons/TEST_COUPON")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponName").value("테스트 쿠폰"))
                .andExpect(jsonPath("$.description").value("테스트용 쿠폰입니다"));
    }

    @Test
    @DisplayName("존재하지 않는 쿠폰 조회시 404 응답 테스트")
    void getCouponNotFoundTest() throws Exception {
        // Given
        given(couponService.getCouponByCode("NOT_EXIST")).willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/coupons/NOT_EXIST")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("쿠폰 발급 성공 테스트")
    void issueCouponSuccessTest() throws Exception {
        // Given
        CouponIssueRequest request = new CouponIssueRequest(100L, "TEST123");
        CouponResponse response = CouponResponse.builder()
                .id(1L)
                .couponId(1L)
                .couponName("테스트 쿠폰")
                .couponCode("TEST123")
                .status("ACTIVE")
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();

        given(couponService.issueCoupon("TEST123", 100L)).willReturn(response);

        // When & Then
        mockMvc.perform(post("/api/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponCode").value("TEST123"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 재고 없음 테스트")
    void issueCouponNoStockTest() throws Exception {
        // Given
        CouponIssueRequest request = new CouponIssueRequest(100L, "SOLD_OUT");

        given(couponService.issueCoupon("SOLD_OUT", 100L))
                .willThrow(new IllegalStateException("쿠폰이 모두 소진되었습니다."));


        // When & Then
        mockMvc.perform(post("/api/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자 쿠폰 목록 조회 테스트")
    void getUserCouponsTest() throws Exception {
        // Given
        CouponResponse response1 = CouponResponse.builder()
            .id(1L)
            .couponId(1L)
            .couponName("테스트 쿠폰 1")
            .couponCode("TEST123")
            .status("ACTIVE")
            .build();


        CouponResponse response2 = CouponResponse.builder()
            .id(2L)
            .couponId(2L)
            .couponName("테스트 쿠폰 2")
            .couponCode("TEST456")
            .status("ACTIVE")
            .build();

        
        given(couponService.getUserCoupons(100L)).willReturn(Arrays.asList(response1, response2));


            // When & Then
        mockMvc.perform(get("/api/coupons/user/100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].couponName").value("테스트 쿠폰 1"))
                .andExpect(jsonPath("$[1].couponName").value("테스트 쿠폰 2"));
    }

}
