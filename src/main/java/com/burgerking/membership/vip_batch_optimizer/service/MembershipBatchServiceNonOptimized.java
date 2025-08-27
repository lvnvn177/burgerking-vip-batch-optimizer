package com.burgerking.membership.vip_batch_optimizer.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


import org.springframework.stereotype.Service;

import com.burgerking.membership.vip_batch_optimizer.domain.Member;
import com.burgerking.membership.vip_batch_optimizer.domain.Order;
import com.burgerking.membership.vip_batch_optimizer.domain.Member.MembershipLevel;
import com.burgerking.membership.vip_batch_optimizer.repository.MemberRepository;
import com.burgerking.membership.vip_batch_optimizer.repository.OrderRepository;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembershipBatchServiceNonOptimized {
    
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void updateMembershipLevels()
    {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfLastMonth =
        now.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfLastMonth = 
        now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).minusSeconds(1);

        // 모든 회원 조회 - 비효율
        List<Member> allMembers = memberRepository.findAll();

        for (Member member : allMembers) {
            // 각 회원마다 개별 쿼리 실행 - N+1 문제 발생!
            List<Order> memberOrders = orderRepository.findOrdersByMemberInPeriod(
                member.getId(), startOfLastMonth, endOfLastMonth);
            
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (Order order : memberOrders) {
                totalAmount = totalAmount.add(order.getAmount());
            }

            // 등급 결정
            MembershipLevel newLevel = determineNewLevel(totalAmount,
            member.getMembershipLevel());

            // 각 회원마다 개별 업데이트 - 비효율적 
            member.setMembershipLevel(newLevel);
            member.setLastLevelUpdatedDate(now);
            memberRepository.save(member);
        }
    }

    private MembershipLevel determineNewLevel(BigDecimal totalAmount, MembershipLevel
    currentLevel) {
        if (totalAmount.compareTo(new BigDecimal("300000")) >=0) {
            return MembershipLevel.VIP;
        } else if (totalAmount.compareTo(new BigDecimal("200000")) >=0) {
            return MembershipLevel.PLATINUM;
        } else if (totalAmount.compareTo(new BigDecimal("100000")) >=0) {
            return MembershipLevel.GOLD;
        } else {
            // 주문 금액이 10만원 미만이면 REGULAR로 하향 
            return MembershipLevel.REGULAR;
        }
    }
}
