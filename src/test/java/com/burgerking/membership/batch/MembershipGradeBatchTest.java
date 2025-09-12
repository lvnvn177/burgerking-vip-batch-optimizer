package com.burgerking.membership.batch;

import com.burgerking.membership.batch.config.MembershipGradeBatchConfig;
import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.domain.MonthlyOrder;
import com.burgerking.membership.domain.enums.MembershipGrade;
import com.burgerking.membership.repository.MembershipRepository;
import com.burgerking.membership.repository.MonthlyOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;


import java.time.YearMonth;
import java.util.Arrays;


import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
@ContextConfiguration(classes =  {MembershipGradeBatchConfig.class})
public class MembershipGradeBatchTest {
    

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MonthlyOrderRepository monthlyOrderRepository;

    @BeforeEach
    void setUp() {
        // 이전 테스트 데이터 정리
        jobRepositoryTestUtils.removeJobExecutions();
        membershipRepository.deleteAll();;
        monthlyOrderRepository.deleteAll();;

        // 테스트 데이터 생성
        setupTestData();
    }

    private void setupTestData() {
        // 멤버십 데이터 생성
        Membership member1 = Membership.builder()
            .userId(1L)
            .grade(MembershipGrade.BRONZE)
            .build();
        
        Membership member2 = Membership.builder()
            .userId(2L)
            .grade(MembershipGrade.BRONZE)
            .build();

        Membership member3 = Membership.builder()
            .userId(3L)
            .grade(MembershipGrade.BRONZE)
            .build();

        membershipRepository.saveAll(Arrays.asList(member1, member2, member3));

        // 월별 주문 데이터 생성 
        YearMonth month1 = YearMonth.now().minusMonths(3);
        YearMonth month2 = YearMonth.now().minusMonths(2);
        YearMonth month3 = YearMonth.now().minusMonths(1);

        // 첫 번째 사용자: 3개월 합산 10만원 -> BRONZE 유지
       MonthlyOrder order1Member1 = MonthlyOrder.builder()
                .userId(1L)
                .yearMonth(month1)
                .build();
        order1Member1.addOrder(30000);

        MonthlyOrder order2Member1 = MonthlyOrder.builder()
                .userId(1L)
                .yearMonth(month2)
                .build();
        order2Member1.addOrder(30000);

        MonthlyOrder order3Member1 = MonthlyOrder.builder()
                .userId(1L)
                .yearMonth(month3)
                .build();
        order3Member1.addOrder(40000);

        // 두 번째 사용자: 3개월 합산 20만원 -> SILVER 승급
        MonthlyOrder order1Member2 = MonthlyOrder.builder()
                .userId(2L)
                .yearMonth(month1)
                .build();
        order1Member2.addOrder(50000);

        MonthlyOrder order2Member2 = MonthlyOrder.builder()
                .userId(2L)
                .yearMonth(month2)
                .build();
        order2Member2.addOrder(60000);

        MonthlyOrder order3Member2 = MonthlyOrder.builder()
                .userId(2L)
                .yearMonth(month3)
                .build();
        order3Member2.addOrder(90000);

        // 세 번째 사용자: 3개월 합산 40만원 -> GOLD 승급
        MonthlyOrder order1Member3 = MonthlyOrder.builder()
                .userId(3L)
                .yearMonth(month1)
                .build();
        order1Member3.addOrder(100000);

        MonthlyOrder order2Member3 = MonthlyOrder.builder()
                .userId(3L)
                .yearMonth(month2)
                .build();
        order2Member3.addOrder(150000);

        MonthlyOrder order3Member3 = MonthlyOrder.builder()
                .userId(3L)
                .yearMonth(month3)
                .build();
        order3Member3.addOrder(150000);

        monthlyOrderRepository.saveAll(Arrays.asList(
                order1Member1, order2Member1, order3Member1,
                order1Member2, order2Member2, order3Member2,
                order1Member3, order2Member3, order3Member3
        ));
    }

    @Test
    @DisplayName("멤버십 등급 갱신 배치 테스트")
    void testMembershipGradeBatch() throws Exception {
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // then
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        // 각 사용자의 등급이 올바르게 갱신되었는지 확인
        Membership member1 = membershipRepository.findByUserId(1L).orElseThrow();
        Membership member2 = membershipRepository.findByUserId(2L).orElseThrow();
        Membership member3 = membershipRepository.findByUserId(3L).orElseThrow();

        assertEquals(MembershipGrade.BRONZE, member1.getGrade()); // 10만원 -> BRONZE 유지
        assertEquals(MembershipGrade.SILVER, member2.getGrade()); // 20만원 -> SILVER 승급
        assertEquals(MembershipGrade.GOLD, member3.getGrade());   // 40만원 -> GOLD 승급
    }

    @Test
    @DisplayName("멤버십 등급 평가 기간 테스트")
    void testMembershipGradeEvaluationPeriod() throws Exception {
        // 현재 기준으로 4개월 전 주문 데이터 추가 (평가 기간에 포함되지 않음)
        YearMonth oldMonth = YearMonth.now().minusMonths(4);

        MonthlyOrder oldOrder = MonthlyOrder.builder()
            .userId(1L)
            .yearMonth(oldMonth)
            .build();
        oldOrder.addOrder(500000); // 많은 금액을 추가해도 평가 기간에 포함되지 않음
        monthlyOrderRepository.save(oldOrder); // 추가된 월별 주문 저장


        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // then
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        // 사용자 1의 등급을 다시 조회
        Membership member1 = membershipRepository.findByUserId(1L).orElseThrow();

        // 4개월 전의 큰 금액은 등급 평가에 영향을 주지 않으므로 BRONZE 등급을 유지해야 함
        assertEquals(MembershipGrade.BRONZE, member1.getGrade());

        // 추가 검증: 다른 사용자 등급도 이전 테스트와 동일하게 유지되는지 (영향받지 않는지)
        Membership member2 = membershipRepository.findByUserId(2L).orElseThrow();
        Membership member3 = membershipRepository.findByUserId(3L).orElseThrow();
        assertEquals(MembershipGrade.SILVER, member2.getGrade());
        assertEquals(MembershipGrade.GOLD, member3.getGrade());
    }
    
}
