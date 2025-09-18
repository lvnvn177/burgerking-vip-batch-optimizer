package com.burgerking.membership.service;


import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.domain.SumOrder;
import com.burgerking.membership.domain.Order;
import com.burgerking.membership.domain.enums.MembershipGrade;
import com.burgerking.membership.repository.MembershipRepository;
import com.burgerking.membership.repository.SumOrderRepository;
import com.burgerking.membership.repository.OrderRepository;
import com.burgerking.membership.util.MembershipTestDataGenerator;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipService {
    
    private final MembershipRepository membershipRepository;
    private final SumOrderRepository sumOrderRepository;
    private final OrderRepository membershipOrderRepository; // Order 엔티티 저장
    private final org.springframework.batch.core.launch.JobLauncher jobLauncher;
    private final org.springframework.batch.core.Job membershipGradeJob;
    private final MembershipTestDataGenerator membershipTestDataGenerator;

    private final EntityManager entityManager;

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
     * - 해당 누적 SumOrder를 업데이트하거나 생성합니다.
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
        membershipOrderRepository.save(newOrder);


        // 2. SumOrder 업데이트 또는 생성
  

        SumOrder sumOrder = sumOrderRepository.findByUserId(userId);

        sumOrder.addOrder(orderAmount); // 누적 주문 금액 및 횟수 누적 
        sumOrderRepository.save(sumOrder); // 변경된 SumOrder 저장 
    }

    /**
     * 특정 사용자의 현재 멤버십 정보를 조회합니다.
     * @param userId 사용자 ID
     * @return 멤버십 엔티티 (Optional)
     */
    public Optional<Membership> getMembershipByUserId(Long userId) {
        return membershipRepository.findByUserId(userId);
    }

    /**
     * (테스트용) 비최적화된 멤버십 등급 조정 배치를 실행합니다.
     * N+1 문제를 시뮬레이션합니다.
     */
    @Transactional
    public void runNonOptimizedBatch() {
        System.out.println("Running non-optimized batch job...");
        LocalDateTime evaluationTime = LocalDateTime.now();

        List<Membership> allMemberships = membershipRepository.findAll();

        for (Membership membership : allMemberships) {
          
            SumOrder sumOrders = sumOrderRepository.findByUserId(
                membership.getUserId());
            
            int totalAmount = sumOrders.getTotalAmount();

            MembershipGrade newGrade = MembershipGrade.evaluateGrade(totalAmount);
            
            membership.updateGrade(newGrade, evaluationTime);
            membershipRepository.save(membership);
        }
        System.out.println("Non-optimized batch job finished.");
    }

    /**
     * (테스트용) 최적화된 멤버십 등급 조정 배치를 실행합니다.
     * Spring Batch를 사용하여 처리합니다.
     */
    public void runOptimizedBatch() {
        try {
            org.springframework.batch.core.JobParameters jobParameters = new org.springframework.batch.core.JobParametersBuilder()
                    .addString("time", LocalDateTime.now().toString())
                    .toJobParameters();
            jobLauncher.run(membershipGradeJob, jobParameters);
            System.out.println("Optimized batch job launched successfully.");
        } catch (Exception e) {
            System.err.println("Error launching optimized batch job: " + e.getMessage());
            throw new RuntimeException("Failed to launch optimized batch job", e);
        }
    }
    
    /**
     * (테스트용) 고객 및 주문 더미 데이터를 생성합니다.
     * @param numberOfMembers 생성할 고객 수
     * @param maxOrdersPerMember 한 고객당 최대 주문 수
     */
    @Transactional
    public void generateTestData(int numberOfMembers, int maxOrdersPerMember) {
        membershipTestDataGenerator.generateMembersAndOrders(numberOfMembers, maxOrdersPerMember);
        entityManager.flush();
    }
}
