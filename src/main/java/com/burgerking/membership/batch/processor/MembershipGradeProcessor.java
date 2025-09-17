package com.burgerking.membership.batch.processor;

import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.domain.SumOrder;
import com.burgerking.membership.domain.enums.MembershipGrade;
import com.burgerking.membership.repository.SumOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;


/**
 * 멤버십 등급 평가를 위한 ItemProcessor
 * 사용자별 주문 내역을 기반으로 멤버십 등급을 계산합니다.
 */
@Slf4j
@RequiredArgsConstructor
public class MembershipGradeProcessor implements ItemProcessor<Membership, Membership> {

    private final SumOrderRepository sumOrderRepository;


    @Override
    public Membership process(@SuppressWarnings("null") Membership membership) throws Exception {
        // 해당 사용자의 누적 주문 내역 조회
        SumOrder sumOrder = sumOrderRepository.findByUserId(
                membership.getUserId());
        
        Integer orderAmount = sumOrder.getTotalAmount();

        

        log.info("사용자 ID: {}, 주문 누적 금액: {}", membership.getUserId(), sumOrder);

        // 누적 금액에 따른 등급 계산
        MembershipGrade newGrade = MembershipGrade.evaluateGrade(orderAmount);

        // 멤버십 등급 갱신
        LocalDateTime evaluationTime = LocalDateTime.now();
        membership.updateGrade(newGrade, evaluationTime);

        return membership;
    }
}
