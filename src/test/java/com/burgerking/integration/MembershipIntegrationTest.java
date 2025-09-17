// package com.burgerking.integration;

// import com.burgerking.membership.domain.Membership;
// import com.burgerking.membership.domain.SumOrder;
// import com.burgerking.membership.domain.enums.MembershipGrade;
// import com.burgerking.membership.repository.MembershipRepository;
// import com.burgerking.membership.repository.SumOrderRepository;
// import com.burgerking.membership.repository.MembershipOrderRepository;
// import com.burgerking.membership.web.dto.OrderProcessRequest;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.YearMonth;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// /**
//  * 멤버십 관련 API의 통합 테스트 클래스입니다.
//  * 실제 데이터베이스 연동을 통해 API의 전체 흐름을 테스트합니다.
//  */
// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// @Transactional
// public class MembershipIntegrationTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @Autowired
//     private MembershipRepository membershipRepository;

//     @Autowired
//     private SumOrderRepository monthlyOrderRepository;

//     @Autowired
//     private MembershipOrderRepository membershipOrderRepository;

//     private Long testUserId;

//     @BeforeEach
//     void setUp() {
//         testUserId = 1L;
//         cleanupTestData();
//     }

//     @AfterEach
//     void tearDown() {
//         cleanupTestData();
//     }

//     /**
//      * 각 테스트 실행 전에 데이터베이스를 초기화합니다.
//      */
//     private void cleanupTestData() {
//         membershipOrderRepository.deleteAllInBatch();
//         monthlyOrderRepository.deleteAllInBatch();
//         membershipRepository.deleteAllInBatch();
//     }

//     /**
//      * 신규 사용자에 대한 멤버십이 정상적으로 생성되는지 테스트합니다.
//      */
//     @Test
//     @DisplayName("신규 사용자 멤버십 생성 통합 테스트")
//     void createNewMembership() throws Exception {
//         // when
//         mockMvc.perform(get("/api/membership/memberships/{userId}", testUserId)
//             .contentType(MediaType.APPLICATION_JSON))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$.userId").value(testUserId))
//             .andExpect(jsonPath("$.grade").value(MembershipGrade.BRONZE.name()));

//         // DB에서 직접 조회하여 검증
//         Optional<Membership> savedMembership = membershipRepository.findByUserId(testUserId);

//         assertTrue(savedMembership.isPresent());
//         assertEquals(MembershipGrade.BRONZE, savedMembership.get().getGrade());
//     }

//     /**
//      * 신규 주문이 발생했을 때, 월별 주문 정보가 정상적으로 집계되는지 테스트합니다.
//      */
//     // @Test
//     // @DisplayName("주문 처리 통합 테스트")
//     // void processOrder() throws Exception {
//     //     // given
//     //     // 먼저 멤버십 생성
//     //     mockMvc.perform(get("/api/membership/memberships/{userId}", testUserId))
//     //         .andExpect(status().isOk());
        
//     //     // 주문 요청 객체 생성
//     //     OrderProcessRequest orderRequest = new OrderProcessRequest(
//     //             testUserId, 
//     //             "ORDER123456", 
//     //             Integer.valueOf(150000) // 15만원 주문
//     //     );

//     //     // when
//     //     mockMvc.perform(post("/api/membership/memberships/orders")
//     //             .contentType(MediaType.APPLICATION_JSON)
//     //             .content(objectMapper.writeValueAsString(orderRequest)))
//     //             .andExpect(status().isOk())
//     //             .andReturn();
        
//     //     // then
//     //     // 현재 월의 주문 데이터 검증
//     //     YearMonth currenYearMonth = YearMonth.now();
//     //     Optional<SumOrder> monthlyOrder = monthlyOrderRepository.findByUserIdAndYearMonth(testUserId, currenYearMonth);

//     //     assertTrue(monthlyOrder.isPresent());
//     //     assertEquals(150000, monthlyOrder.get().getTotalAmount());
//     //     assertEquals(1, monthlyOrder.get().getOrderCount());
//     // }

//     /**
//      * 3개월간의 주문 내역을 기반으로 멤버십 등급 평가가 정상적으로 이루어지는지 테스트합니다.
//      * (BRONZE -> GOLD)
//      */
//     @Test
//     @DisplayName("멤버십 등급 평가 통합 테스트")
//     void evaluateAndRenewMembershipGrades() throws Exception {
//         // given
//         // 먼저 멤버십 생성
//         mockMvc.perform(get("/api/membership/memberships/{userId}", testUserId))
//                 .andExpect(status().isOk());
        
//         // // 3개월치 주문 데이터 생성 (직전 3개월)
//         // YearMonth month1 = YearMonth.now().minusMonths(3);
//         // YearMonth month2 = YearMonth.now().minusMonths(2);
//         // YearMonth month3 = YearMonth.now().minusMonths(1);

//         // 월별 주문 객체 생성 및 저장
//         // SumOrder order1 = SumOrder.builder().userId(testUserId).yearMonth(month1).build();
//         // order1.addOrder(100000);    // 10만원
//         // monthlyOrderRepository.save(order1);

//         // SumOrder order2 = SumOrder.builder().userId(testUserId).yearMonth(month2).build();
//         // order2.addOrder(150000);    // 15만원
//         // monthlyOrderRepository.save(order2);

//         // SumOrder order3 = SumOrder.builder().userId(testUserId).yearMonth(month3).build();
//         // order3.addOrder(200000);    // 20만원
//         // monthlyOrderRepository.save(order3);

//         // 총 45만원 -> GOLD 등급 기준 충족

//         // when 
//         mockMvc.perform(post("/api/membership/memberships/evaluate")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk());

        
//         // then
//         Optional<Membership> updateMembership = membershipRepository.findByUserId(testUserId);

//         assertTrue(updateMembership.isPresent());
//         assertEquals(MembershipGrade.GOLD, updateMembership.get().getGrade());        
//     }
// }
