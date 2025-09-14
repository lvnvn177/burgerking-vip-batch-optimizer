package com.burgerking.membership.util;

import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.domain.Order;
import com.burgerking.membership.domain.enums.MembershipGrade;
import com.burgerking.membership.repository.MembershipRepository;
import com.burgerking.membership.repository.MembershipOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MembershipTestDataGenerator {

    private final MembershipRepository membershipRepository;
    private final MembershipOrderRepository membershipOrderRepository;
    private final Random random = new Random();

    @Transactional
    public void generateMembersAndOrders(int numberOfMembers, int maxOrdersPerMember) {
        List<Membership> members = new ArrayList<>();
        for (int i = 0; i < numberOfMembers; i++) {
            Long userId = 2000L + i; // 2000번대 user_id 사용
            MembershipGrade grade = MembershipGrade.BRONZE; // 초기 등급은 BRONZE
            Membership membership = Membership.builder()
                    .userId(userId)
                    .grade(grade)
                    .build(); // lastEvaluationDate, nextEvaluationDate, createdAt, updatedAt은 생성자에서 자동 설정
            members.add(membership);
        }
        membershipRepository.saveAll(members);

        List<Order> orders = new ArrayList<>();
        for (Membership member : members) {
            int numOrders = random.nextInt(maxOrdersPerMember) + 1; // 1개 이상 주문
            for (int i = 0; i < numOrders; i++) {
                orders.add(createRandomOrder(member.getUserId()));
            }
        }
        membershipOrderRepository.saveAll(orders);
        System.out.println(numberOfMembers + " members and " + orders.size() + " orders generated for membership testing.");
    }

    private Order createRandomOrder(Long userId) {
        int orderAmount = random.nextInt(50) * 1000 + 5000; // 5000원 ~ 54000원 (1000원 단위)
        return Order.builder()
                .userId(userId)
                .orderNumber(UUID.randomUUID().toString())
                .orderAmount(orderAmount)
                .build(); // orderDate, createdAt은 생성자에서 자동 설정
    }
}