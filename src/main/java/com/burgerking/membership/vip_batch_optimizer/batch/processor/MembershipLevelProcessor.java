package com.burgerking.membership.vip_batch_optimizer.batch.processor;

import com.burgerking.membership.vip_batch_optimizer.domain.Member;
import com.burgerking.membership.vip_batch_optimizer.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MembershipLevelProcessor implements ItemProcessor<Member, Member> {

    private final OrderRepository orderRepository;

    @Override
    public Member process(Member member) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfLastMonth = now.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfLastMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).minusSeconds(1);

        BigDecimal totalAmount = calculateTotalAmount(member.getId(), startOfLastMonth, endOfLastMonth);
        
        Member.MembershipLevel newLevel = determineNewLevel(totalAmount, member.getMembershipLevel());
        
        member.setMembershipLevel(newLevel);
        member.setLastLevelUpdatedDate(now);
        
        return member;
    }

    private BigDecimal calculateTotalAmount(Long memberId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> result = orderRepository.findTotalAmountByMemberInPeriod(startDate, endDate);
        for(Object[] row : result) {
            if (((Long) row[0]).equals(memberId)) {
                return (BigDecimal) row[1];
            }
        }
        return BigDecimal.ZERO;
    }

    private Member.MembershipLevel determineNewLevel(BigDecimal totalAmount, Member.MembershipLevel currentLevel) {
        if (totalAmount.compareTo(new BigDecimal("300000")) >= 0) {
            return Member.MembershipLevel.VIP;
        } else if (totalAmount.compareTo(new BigDecimal("200000")) >= 0) {
            return Member.MembershipLevel.PLATINUM;
        } else if (totalAmount.compareTo(new BigDecimal("100000")) >= 0) {
            return Member.MembershipLevel.GOLD;
        } else {
            return Member.MembershipLevel.REGULAR;
        }
    }
}