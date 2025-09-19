package com.burgerking.integration;

import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.domain.SumOrder;
import com.burgerking.membership.domain.enums.MembershipGrade;
import com.burgerking.membership.repository.MembershipRepository;
import com.burgerking.membership.repository.SumOrderRepository;
import com.burgerking.membership.repository.OrderRepository; // Changed from MembershipOrderRepository
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 멤버십 관련 API의 통합 테스트 클래스입니다.
 * 실제 데이터베이스 연동을 통해 API의 전체 흐름을 테스트합니다.
 */
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
    private SumOrderRepository sumOrderRepository; // Changed from monthlyOrderRepository

    @Autowired
    private OrderRepository orderRepository; // Changed from MembershipOrderRepository

    private Long testUserId;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        cleanupTestData();
    }

    @AfterEach
    void tearDown() {
        cleanupTestData();
    }

    /**
     * 각 테스트 실행 전에 데이터베이스를 초기화합니다.
     */
    private void cleanupTestData() {
        orderRepository.deleteAllInBatch(); // Changed from membershipOrderRepository
        sumOrderRepository.deleteAllInBatch(); // Changed from monthlyOrderRepository
        membershipRepository.deleteAllInBatch();
    }

    /**
     * 신규 사용자에 대한 멤버십이 정상적으로 생성되는지 테스트합니다.
     */
    @Test
    @DisplayName("신규 사용자 멤버십 생성 통합 테스트")
    void createNewMembership() throws Exception {
        // when
        mockMvc.perform(get("/api/membership/{userId}", testUserId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(testUserId))
            .andExpect(jsonPath("$.grade").value(MembershipGrade.BRONZE.name()));

        // DB에서 직접 조회하여 검증
        Optional<Membership> savedMembership = membershipRepository.findByUserId(testUserId);

        assertTrue(savedMembership.isPresent());
        assertEquals(MembershipGrade.BRONZE, savedMembership.get().getGrade());
    }

    /**
     * 신규 주문이 발생했을 때, 월별 주문 정보가 정상적으로 집계되는지 테스트합니다.
     */
    @Test
    @DisplayName("주문 처리 통합 테스트")
    void processOrder() throws Exception {
        // given
        // 먼저 멤버십 생성
        mockMvc.perform(get("/api/membership/{userId}", testUserId)) // Changed from /api/membership/memberships/{userId}
            .andExpect(status().isOk());
        
        // 주문 요청 객체 생성
        OrderProcessRequest orderRequest = new OrderProcessRequest(
                testUserId, 
                "ORDER123456", 
                Integer.valueOf(150000) // 15만원 주문
        );

        // when
        mockMvc.perform(post("/api/membership/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andReturn();
        
        // then
        // 현재 주문 데이터 검증
        SumOrder sumOrder = sumOrderRepository.findByUserId(testUserId); // Changed from findByUserIdAndYearMonth

        assertNotNull(sumOrder); // Changed from assertTrue(monthlyOrder.isPresent());
        assertEquals(150000, sumOrder.getTotalAmount());
        assertEquals(1, sumOrder.getOrderCount());
    }

    /**
     * 전체 주문 내역을 기반으로 멤버십 등급 평가가 정상적으로 이루어지는지 테스트합니다.
     * (BRONZE -> GOLD)
     */
    @Test
    @DisplayName("멤버십 등급 평가 통합 테스트")
    void evaluateAndRenewMembershipGrades() throws Exception {
        // given
        // 먼저 멤버십 생성
        mockMvc.perform(get("/api/membership/{userId}", testUserId)) // Changed from /api/membership/memberships/{userId}
                .andExpect(status().isOk());
        
        // 3번의 주문 데이터 생성 (1~3일)
        // 테스트 편의를 위해 직접 SumOrder를 DB에 삽입합니다.
        // 3번의 주문 데이터를 하나의 SumOrder 객체에 누적
        SumOrder sumOrder = SumOrder.builder()
                .userId(testUserId)
                .totalAmount(0)
                .orderCount(0)
                .createdAt(LocalDateTime.now().minusDays(3))
                .updatedAt(LocalDateTime.now().minusDays(3))
                .build();
        
        sumOrder.addOrder(100000);
        sumOrder.addOrder(150000);
        sumOrder.addOrder(200000);
        
        sumOrderRepository.save(sumOrder);

        // 총 45만원 -> GOLD 등급 기준 충족 (가정)

        // when 
        // 비최적화 배치 실행 (테스트를 위해)
        mockMvc.perform(post("/api/membership/adjust-batch-non-optimized")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        // then
        Optional<Membership> updateMembership = membershipRepository.findByUserId(testUserId);

        assertTrue(updateMembership.isPresent());
        assertEquals(MembershipGrade.GOLD, updateMembership.get().getGrade());        
    }
}
