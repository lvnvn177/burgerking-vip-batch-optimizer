// package com.burgerking.membership.service;

// import com.burgerking.common.exception.BusinessException;
// import com.burgerking.membership.domain.Membership;
// import com.burgerking.membership.domain.SumOrder;
// import com.burgerking.membership.domain.enums.MembershipGrade;
// import com.burgerking.membership.exception.MembershipException;
// import com.burgerking.membership.repository.MembershipRepository;
// import com.burgerking.membership.repository.SumOrderRepository;
// import com.burgerking.membership.repository.MembershipOrderRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.time.YearMonth;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// /**
//  * MembershipService의 단위 테스트 클래스입니다.
//  * Mockito를 사용하여 의존성을 격리하고 서비스 로직을 검증합니다.
//  */
// @ExtendWith(MockitoExtension.class)
// public class MembershipServiceTest {

//     @Mock
//     private MembershipRepository membershipRepository;

//     @Mock
//     private SumOrderRepository monthlyOrderRepository;

//     @Mock
//     private MembershipOrderRepository membershipOrderRepository;

//     @InjectMocks
//     private MembershipService membershipService;

//     private Long testUserId;
//     private Membership testMembership;
//     private List<SumOrder> testMonthlyOrders;

//     @BeforeEach
//     void setup() {
//         testUserId = 1L;

//         // 테스트용 멤버십 객체 생성
//         testMembership = Membership.builder()
//                 .userId(testUserId)
//                 .grade(MembershipGrade.BRONZE)
//                 .build();

//         // 테스트용 월별 주문 객체 리스트 생성 (3개월치)
//         SumOrder month1 = SumOrder.builder().userId(testUserId).yearMonth(YearMonth.now().minusMonths(3)).build();
//         month1.addOrder(50000); // 5만원

//         SumOrder month2 = SumOrder.builder().userId(testUserId).yearMonth(YearMonth.now().minusMonths(2)).build();
//         month2.addOrder(60000); // 6만원

//         SumOrder month3 = SumOrder.builder().userId(testUserId).yearMonth(YearMonth.now().minusMonths(1)).build();
//         month3.addOrder(70000); // 7만원

//         testMonthlyOrders = Arrays.asList(month1, month2, month3);
//     }

//     /**
//      * 기존 멤버십이 존재할 경우, 새로운 멤버십을 생성하지 않고 기존 멤버십을 반환하는지 테스트합니다.
//      */
//     @Test
//     @DisplayName("멤버십 생성 또는 조회 - 기존 멤버십 반환")
//     void createOrGetMembership_ShouldReturnExistingMembership_WhenMembershipExists() {
//         // given
//         when(membershipRepository.findByUserId(testUserId)).thenReturn(Optional.of(testMembership));

//         // when
//         Membership result = membershipService.createOrGetMembership(testUserId);

//         // then
//         assertNotNull(result);
//         assertEquals(testUserId, result.getUserId());
//         assertEquals(MembershipGrade.BRONZE, result.getGrade());
//         verify(membershipRepository).findByUserId(testUserId);
//         verify(membershipRepository, never()).save(any(Membership.class));
//     }

//     /**
//      * 해당 월에 첫 주문이 발생했을 때, 새로운 MonthlyOrder가 생성되는지 테스트합니다.
//      */
//     @Test
//     @DisplayName("새로운 주문 처리 - 신규 월별 주문 생성")
//     void processNewOrder_ShouldCreateNewMonthlyOrder_WhenNoExistingMonthlyOrderForCurrentMonth() {
//         // given
//         String orderNumber = "ORD123456";
//         Integer orderAmount = 50000;
//         YearMonth currenYearMonth = YearMonth.now();

//         when(monthlyOrderRepository.findByUserIdAndYearMonth(testUserId, currenYearMonth))
//             .thenReturn(Optional.empty());

//         // when
//         membershipService.processNewOrder(testUserId, orderNumber, orderAmount);

//         // then
//         verify(membershipOrderRepository).save(any());
//         verify(monthlyOrderRepository).findByUserIdAndYearMonth(testUserId, currenYearMonth);
//         verify(monthlyOrderRepository).save(any(SumOrder.class));
//     }

//     /**
//      * 3개월 주문 합산 금액에 따라 등급이 SILVER로 정상 승급되는지 테스트합니다.
//      */
//     @Test
//     @DisplayName("멤버십 등급 평가 - SILVER 등급 승급")
//     void evaluateAndRenewAllMembershipGrades_ShouldUpgradeToSilver_WhenTotalAmountMeetsCriteria() {
//         // given
//         // 3개월 합계: 18만원 -> 실버 등급 조건 충족 
//         when(membershipRepository.findAll()).thenReturn(Arrays.asList(testMembership));

//         YearMonth endMonth = YearMonth.now().minusMonths(1);
//         YearMonth startMonth = endMonth.minusMonths(2);
//         when(monthlyOrderRepository.findByUserIdAndYearMonthBetweenOrderByYearMonthAsc(
//             testUserId, startMonth, endMonth))
//             .thenReturn(testMonthlyOrders);

//         // when
//         membershipService.evaluateAndRenewAllMembershipGrades();

//         // then
//         verify(membershipRepository).save(any(Membership.class));
//     }

//     /**
//      * 존재하지 않는 사용자의 멤버십 조회를 시도할 때, 예외가 발생하는지 테스트합니다.
//      */
//     @Test
//     @DisplayName("멤버십 조회 - 존재하지 않는 사용자")
//     void getMembershipByUserId_ShouldThrowException_WhenMembershipNotFound() {
//         // given
//         Long nonExistentUserId = 999L;
//         when(membershipRepository.findByUserId(nonExistentUserId)).thenReturn(Optional.empty());

//         // when & then 
//         BusinessException exception = assertThrows(MembershipException.class, () -> {
//             membershipService.getMembershipByUserId(nonExistentUserId);
//         });

//         // 예외 메시지 검증
//         assertTrue(exception.getMessage().contains("멤버십을 찾을 수 없습니다"));
//     }
// }
