package com.burgerking.membership.batch.config;

import com.burgerking.membership.batch.processor.MembershipGradeProcessor;
import com.burgerking.membership.batch.writer.MembershipGradeWriter;
import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.repository.MembershipRepository;
import com.burgerking.membership.repository.SumOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;

// StepBuilder 관련 import
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;



@Slf4j
@Configuration
@EnableBatchProcessing
public class MembershipGradeBatchConfig {


    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final MembershipRepository membershipRepository;
    private final SumOrderRepository sumOrderRepository;

    private static final int CHUNK_SIZE = 100;

   
    public MembershipGradeBatchConfig(
            JobRepository jobRepository,
            @Qualifier("membershipTransactionManager") PlatformTransactionManager transactionManager,
            @Qualifier("membershipEntityManagerFactory") EntityManagerFactory entityManagerFactory,
            MembershipRepository membershipRepository,
            SumOrderRepository sumOrderRepository) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.entityManagerFactory = entityManagerFactory;
        this.membershipRepository = membershipRepository;
        this.sumOrderRepository = sumOrderRepository;
    }

    /**
     * 멤버십 등급 평가 Job을 정의합니다.
     *
     * @param membershipGradeStep 멤버십 등급 평가 Step
     * @return Job
     */
    @Bean
    public Job membershipGradeJob(Step membershipGradeStep) {
        return new JobBuilder("membershipGradeJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(membershipGradeStep)
                .build();
    }

    /**
     * 멤버십 등급을 평가하는 Step을 정의합니다.
     * - Reader: 모든 멤버십 정보를 페이징하여 읽어옵니다.
     * - Processor: 각 멤버십에 대해 누적 주문 금액을 계산하고 등급을 평가합니다.
     * - Writer: 변경된 멤버십 정보를 DB에 저장합니다.
     *
     * @return Step
     */
    @Bean
    public Step membershipGradeStep() {
   
        return new StepBuilder("membershipGradeStep", jobRepository)
                .<Membership, Membership>chunk(CHUNK_SIZE, transactionManager)
                .reader(membershipItemReader())
                .processor(new MembershipGradeProcessor(sumOrderRepository))
                .writer(new MembershipGradeWriter(membershipRepository))
                .build();
    }

    /**
     * 모든 멤버십 정보를 페이징하여 읽어오는 ItemReader를 정의합니다.
     *
     * @return JpaPagingItemReader<Membership>
     */
    @Bean
    public JpaPagingItemReader<Membership> membershipItemReader() {
        return new JpaPagingItemReaderBuilder<Membership>()
                .name("membershipItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT m FROM Membership m ORDER BY m.id ASC")
                .build();
    }
}