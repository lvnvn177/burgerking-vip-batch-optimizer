package com.burgerking.membership.integration;

import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.domain.MonthlyOrder;
import com.burgerking.membership.domain.enums.MembershipGrade;
import com.burgerking.membership.repository.MembershipRepository;
import com.burgerking.membership.repository.MonthlyOrderRepository;
import com.burgerking.membership.repository.OrderRepository;
import com.burgerking.membership.web.dto.OrderProcessRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MembershipIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MonthlyOrderRepository monthlyOrderRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        testUserId = 1L;

        // 테스트 전 데이터 정리
        cleanupTestData();
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 데이터 정리
        cleanupTestData();
    }

    private void cleanupTestData() {
        orderRepository.deleteAll();
        monthlyOrderRepository.deleteAll();
        membershipRepository.deleteAll();
    }

       @Test
    @DisplayName("신규 사용자 멤버십 생성 통합 테스트")
    void createNewMembership() throws Exception {
        // when
        mockMvc.perform(get("/api/memberships/{userId}", testUserId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(testUserId))
            .andExpect(jsonPath("$.grade").value(MembershipGrade.BRONZE.name()));

        // DB에서 직접 조회하여 검증
        Optional<Membership> savedMembership = membershipRepository.findByUserId(testUserId);

        assertTrue(savedMembership.isPresent());
        assertEquals(MembershipGrade.BRONZE, savedMembership.get().getGrade());
    }

    @Test
    @DisplayName("주문 처리 통합 테스트")
    void processOrder() throws Exception {
        // given
        // 먼저 멤버십 생성
        mockMvc.perform(get("/api/memberships/{userId}", testUserId))
            .andExpect(status().isOk());
        
        // 주문 요청 객체 생성
        OrderProcessRequest orderRequest = new OrderProcessRequest(
                testUserId, 
                "ORDER123456", 
                Integer.valueOf(150000) // 15만원 주문
        );

        // when
        mockMvc.perform(post("/api/memberships/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andReturn();   
        
        // then
        // 현재 월의 주문 데이터 검증
        YearMonth currenYearMonth = YearMonth.now();
        Optional<MonthlyOrder> monthlyOrder = monthlyOrderRepository.findByUserIdAndYearMonth(testUserId, currenYearMonth);

        assertTrue(monthlyOrder.isPresent());
        assertEquals(150000, monthlyOrder.get().getTotalAmount());
        assertEquals(1, monthlyOrder.get().getOrderCount());
    }

    @Test
    @DisplayName("멤버십 등급 평가 통합 테스트")
    void evaluateAndRenewMembershipGrades() throws Exception {
        // given
        // 먼저 멤버십 생성
        mockMvc.perform(get("/api/memberships/{userId}", testUserId))
                .andExpect(status().isOk());
        
        // 3개월치 주문 데이터 생성 (직전 3개월)
        YearMonth month1 = YearMonth.now().minusMonths(3);
        YearMonth month2 = YearMonth.now().minusMonths(2);
        YearMonth month3 = YearMonth.now().minusMonths(1);

        // 월별 주문 객체 생성 및 저장
        MonthlyOrder order1 = MonthlyOrder.builder().userId(testUserId).yearMonth(month1).build();
        order1.addOrder(100000);    // 10만원
        monthlyOrderRepository.save(order1);

        MonthlyOrder order2 = MonthlyOrder.builder().userId(testUserId).yearMonth(month2).build();
        order2.addOrder(150000);    // 15만원
        monthlyOrderRepository.save(order2);

        MonthlyOrder order3 = MonthlyOrder.builder().userId(testUserId).yearMonth(month3).build();
        order3.addOrder(200000);    // 20만원
        monthlyOrderRepository.save(order3);

        // 총 45만원 -> GOLD 등급 기준 충족

        // when 
        mockMvc.perform(post("/api/memberships/evaluate")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        
        // then
        Optional<Membership> updateMembership = membershipRepository.findByUserId(testUserId);

        assertTrue(updateMembership.isPresent());
        assertEquals(MembershipGrade.GOLD, updateMembership.get().getGrade());        
    }
}
