package com.burgerking.membership.service;

import com.burgerking.common.exception.BusinessException;
import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.domain.MonthlyOrder;
import com.burgerking.membership.domain.enums.MembershipGrade;
import com.burgerking.membership.exception.MembershipException;
import com.burgerking.membership.repository.MembershipRepository;
import com.burgerking.membership.repository.MonthlyOrderRepository;
import com.burgerking.membership.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {
    
    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private MonthlyOrderRepository monthlyOrderRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private MembershipService membershipService;

    private Long testUserId;
    private Membership testMembership;
    private List<MonthlyOrder> testMonthlyOrders;

    @BeforeEach
    void setup() {
        testUserId = 1L;

        // 테스트용 멤버쉽 객체 생성
        testMembership = Membership.builder()
            .userId(testUserId)
            .grade(MembershipGrade.BRONZE)
            .build();
        
        // 테스트용 월별 주문 객체 생성
        MonthlyOrder month1 = MonthlyOrder.builder()
            .userId(testUserId)
            .yearMonth(YearMonth.now().minusMonths(3))
            .build();
        month1.addOrder(50000); // 5만원 주문 

        MonthlyOrder month2 = MonthlyOrder.builder()
            .userId(testUserId)
            .yearMonth(YearMonth.now().minusMonths(2))
            .build();
        month2.addOrder(60000); // 6만원 주문 

        MonthlyOrder month3 = MonthlyOrder.builder()
            .userId(testUserId)
            .yearMonth(YearMonth.now().minusMonths(1))
            .build();
        month3.addOrder(70000); // 7만원 주문 

        testMonthlyOrders = Arrays.asList(month1, month2, month3);
    }

    @Test
    @DisplayName("멤버십 생성 또는 조회 테스트")
    void createOrGetMembership_ShouldReturnExistingMembership_WhenMembershipExists() {
        // given
        when(membershipRepository.findByUserId(testUserId)).thenReturn(Optional.of(testMembership));

        // when
        Membership result = membershipService.createOrGetMembership(testUserId);

        // then
        assertNotNull(result);
        assertEquals(testUserId, result.getUserId());
        assertEquals(MembershipGrade.BRONZE, result.getGrade());
        verify(membershipRepository).findByUserId(testUserId);
        verify(membershipRepository, never()).save(any(Membership.class));
    }

    @Test
    @DisplayName("새로운 주문 처리 테스트")
    void processNewOrder_ShouldCreateNewMonthlyOrder_WhenNoExistingMonthlyOrderForCurrentMonth() {
        // given
        String orderNumber = "ORD123456";
        Integer orderAmount = 50000;
        YearMonth currenYearMonth = YearMonth.now();

        when(monthlyOrderRepository.findByUserIdAndYearMonth(testUserId, currenYearMonth))
            .thenReturn(Optional.empty());

        // when
        membershipService.processNewOrder(testUserId, orderNumber, orderAmount);

        // then
        verify(orderRepository).save(any());
        verify(monthlyOrderRepository).findByUserIdAndYearMonth(testUserId, currenYearMonth);
        verify(monthlyOrderRepository).save(any(MonthlyOrder.class));
    }

    @Test
    @DisplayName("멤버십 등급 평가 테스트 - 실버 등급 승급")
    void evaluateAndRenewAllMembershipGrades_ShouldUpgradeToSilver_WhenTotalAmountMeetsCriteria() {
        // given
        // 3개월 합계: 18만원 -> 실버 등급 조건 충족 
        when(membershipRepository.findAll()).thenReturn(Arrays.asList(testMembership));

        YearMonth endMonth = YearMonth.now().minusMonths(1);
        YearMonth startMonth = endMonth.minusMonths(2);
        when(monthlyOrderRepository.findByUserIdAndYearMonthBetweemOrderByYearMonthAsc(
            testUserId, startMonth, endMonth))
            .thenReturn(testMonthlyOrders);

        // when
        membershipService.evaluateAndRenewAllMembershipGrades();

        // then
        verify(membershipRepository).save(any(Membership.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자 멤버십 조회 시 예외 발생 테스트")
    void getMembershipByUserId_ShouldThrowException_WhenMembershipNotFound() {
        // given
        Long nonExistentUserId = 999L;
        when(membershipRepository.findByUserId(nonExistentUserId)).thenReturn(Optional.empty());

        // when & then 
        BusinessException exception = assertThrows(MembershipException.class, () -> {
            membershipService.getMembershipByUserId(nonExistentUserId);
        });

        // 예외 메시지 검증
        assertTrue(exception.getMessage().contains("멤버십을 찾을 수 없습니다"));
    }
}
