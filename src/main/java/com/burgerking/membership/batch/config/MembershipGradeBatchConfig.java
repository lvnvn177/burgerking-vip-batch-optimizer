package com.burgerking.membership.batch.config;

import com.burgerking.membership.batch.processor.MembershipGradeProcessor;
import com.burgerking.membership.batch.writer.MembershipGradeWriter;
import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.domain.MonthlyOrder;
import com.burgerking.membership.repository.MembershipRepository;
import com.burgerking.membership.repository.MonthlyOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;

// StepBuilder 관련 import
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class MembershipGradeBatchConfig {


    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final MembershipRepository membershipRepository;
    private final MonthlyOrderRepository monthlyOrderRepository;

    private static final int CHUNK_SIZE = 100;

    @Bean
    public Job membershipGradeJob(Step membershipGradeStep) {
      
        return new JobBuilder("membershipGradeJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(membershipGradeStep)
                .build();
    }

    @Bean
    public Step membershipGradeStep() {
        // 등급 평가 기간 설정 (직전 3개월)
        YearMonth endMonth = YearMonth.now().minusMonths(1);
        YearMonth startMonth = endMonth.minusMonths(2);
        
        // 해당 기간의 모든 월별 주문 데이터 조회
        List<MonthlyOrder> allMonthlyOrders = monthlyOrderRepository.findByYearMonthBetween(startMonth, endMonth);
        
     
        return new StepBuilder("membershipGradeStep", jobRepository)
                .<Membership, Membership>chunk(CHUNK_SIZE, transactionManager)
                .reader(membershipItemReader())
                .processor(new MembershipGradeProcessor(allMonthlyOrders, startMonth, endMonth))
                .writer(new MembershipGradeWriter(membershipRepository))
                .build();
    }

    @Bean
    public JpaPagingItemReader<Membership> membershipItemReader() {
        return new JpaPagingItemReaderBuilder<Membership>()
                .name("membershipItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT m FROM Membership m")
                .build();
    }
}