package com.burgerking.membership.batch.processor;

import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.domain.MonthlyOrder;
import com.burgerking.membership.domain.enums.MembershipGrade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

/**
 * 멤버십 등급 평가를 위한 ItemProcessor
 * 사용자별 주문 내역을 기반으로 멤버십 등급을 계산합니다.
 */
@Slf4j
public class MembershipGradeProcessor implements ItemProcessor<Membership, Membership> {
    
    private final List<MonthlyOrder> monthlyOrderList;
    private final YearMonth startMonth;
    private final YearMonth endMonth;

    public MembershipGradeProcessor(List<MonthlyOrder> monthlyOrderList, YearMonth startMonth, YearMonth endMonth) {
        this.monthlyOrderList = monthlyOrderList;
        this.startMonth = startMonth;
        this.endMonth = endMonth;
    }

    @Override
    public Membership process(@SuppressWarnings("null") Membership membership) throws Exception {
        // 해당 사용자의 월별 주문 내역 필터링
        List<MonthlyOrder> userMonthlyOrders = monthlyOrderList.stream()
                .filter(order -> order.getUserId().equals(membership.getUserId()))
                .filter(order -> !order.getYearMonth().isBefore(startMonth) && !order.getYearMonth().isAfter(endMonth))
                .toList();

        // 3개월 누적 금액 계산 
        int total3MonthAmount = userMonthlyOrders.stream()
                .mapToInt(MonthlyOrder::getTotalAmount)
                .sum();
        
        log.info("사용자 ID: {}, 3개월 누적 금액: {}", membership.getUserId(), total3MonthAmount);

        // 누적 금액에 따른 등급 계산
        MembershipGrade newGrade = MembershipGrade.evaluateGrade(total3MonthAmount);

        // 멤버십 등급 갱신
        LocalDateTime evaluationTime = LocalDateTime.now();
        membership.updateGrade(newGrade, evaluationTime);

        return membership;
    }
}
