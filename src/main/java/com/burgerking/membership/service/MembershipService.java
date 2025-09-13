package com.burgerking.membership.service;


import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.domain.MonthlyOrder;
import com.burgerking.membership.domain.Order;
import com.burgerking.membership.domain.enums.MembershipGrade;
import com.burgerking.membership.repository.MembershipRepository;
import com.burgerking.membership.repository.MonthlyOrderRepository;
import com.burgerking.membership.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipService {
    
    private final MembershipRepository membershipRepository;
    private final MonthlyOrderRepository monthlyOrderRepository;
    private final OrderRepository orderRepository; // Order 엔티티 저장 

    /**
     * 새로운 사용자 멤버십을 생성하거나 기존 멤버십을 조회합니다.
     * @param userId 사용자 ID
     * @return 멤버십 엔티티
     */
    @Transactional
    public Membership createOrGetMembership(Long userId) {
        return membershipRepository.findByUserId(userId)
            .orElseGet( () -> {
                Membership newMembership = Membership.builder()
                    .userId(userId)
                    .grade(MembershipGrade.BRONZE) // 초기 등급 설정
                    .build();
                return membershipRepository.save(newMembership);
            });
    }


    /**
     * 주문이 발생했을 때 멤버십 시스템 관련 데이터를 업데이트합니다.
     * - Order 엔티티를 저장합니다.
     * - 해당 월의 MonthlyOrder를 업데이트하거나 생성합니다.
     * @param userId 사용자 ID
     * @param orderNumber 주문 번호
     * @param orderAmount 주문 금액
     */
    @Transactional
    public void processNewOrder(Long userId, String orderNumber, Integer orderAmount) {
        // 1. Order 엔티티 저장 (주문 내역 기록)
        Order newOrder = Order.builder()
            .userId(userId)
            .orderNumber(orderNumber)
            .orderAmount(orderAmount)
            .build();
        orderRepository.save(newOrder);


        // 2. 해당 월의 MonthlyOrder 업데이트 또는 생성
        YearMonth currentYearMonth = YearMonth.now(); // 현재 연월

        MonthlyOrder monthlyOrder = monthlyOrderRepository.findByUserIdAndYearMonth(userId, currentYearMonth)
            .orElseGet( () -> MonthlyOrder.builder()
                .userId(userId)
                .yearMonth(currentYearMonth)
                .build());
        
        monthlyOrder.addOrder(orderAmount); // 월별 주문 금액 및 횟수 누적 
        monthlyOrderRepository.save(monthlyOrder); // 변경된 MonthlyOrder 저장 
    }

    /**
     * 매월 1일 오전 9시에 호출될 스케줄러 메서드 (혹은 수동 호출).
     * 모든 사용자의 멤버십 등급을 재평가하고 갱신합니다.
     */
    @Transactional
    public void evaluateAndRenewAllMembershipGrades() {
        LocalDateTime evaluationTime = LocalDateTime.now(); // 등급 평가 시점

        // 모든 멤버쉽을 순회하며 등급을 재평가
        List<Membership> allMemberships = membershipRepository.findAll();

        for (Membership membership : allMemberships) {
            // 직전 3개월의 월별 주문 금액을 조회하여 합산
            YearMonth endMonth = YearMonth.now().minusMonths(1); // 직전 달
            YearMonth startMonth = endMonth.minusMonths(2); // 직전 달로부터 3개월 전

            List<MonthlyOrder> last3MonthsOrders = monthlyOrderRepository.findByUserIdAndYearMonthBetweenOrderByYearMonthAsc(
                membership.getUserId(), startMonth, endMonth);
            
            int total3MonthAmount = last3MonthsOrders.stream()
                .mapToInt(MonthlyOrder::getTotalAmount)
                .sum();

            // 누적 금액을 기반으로 새로운 등급 계산
            MembershipGrade newGrade = MembershipGrade.evaluateGrade(total3MonthAmount);
            
            
            // 멤버쉽 등급 갱신 (변경이 없어도 lastEvaluationDate 등은 업데이트)
            membership.updateGrade(newGrade, evaluationTime);
            membershipRepository.save(membership); // 변경된 멤버쉽 저장
        }
    }

    /**
     * 특정 사용자의 현재 멤버십 정보를 조회합니다.
     * @param userId 사용자 ID
     * @return 멤버십 엔티티 (Optional)
     */
    public Optional<Membership> getMembershipByUserId(Long userId) {
        return membershipRepository.findByUserId(userId);
    }
}
