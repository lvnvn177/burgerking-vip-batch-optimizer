package com.burgerking.membership.vip_batch_optimizer.config;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;



import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.burgerking.membership.vip_batch_optimizer.domain.Member;
import com.burgerking.membership.vip_batch_optimizer.batch.processor.MembershipLevelProcessor;
import com.burgerking.membership.vip_batch_optimizer.batch.writer.MemberUpdateItemWriter;


import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory; 

     /**
     * 멤버십 등급 갱신 Job 정의
     * - 하나의 Step (membershipUpdateStep)으로 구성됩니다.
     */

     @Bean
     public Job membershipUpdateJob(Step membershipUpdateStep) {
        return new JobBuilder("membershipUpdateJob", jobRepository)
        .start(membershipUpdateStep)
        .build();
     }

       /**
     * 멤버십 등급 갱신 Step 정의
     * - Member 엔티티를 읽어와서 등급을 결정하고 업데이트하는 Step입니다.
     * - 청크(Chunk) 기반으로 동작하며, 1000개 단위로 트랜잭션이 커밋됩니다.
     */

     @Bean
    public Step membershipUpdateStep(
            JpaPagingItemReader<Member> memberReader, // Member를 페이징하여 읽는 Reader
            MembershipLevelProcessor membershipLevelProcessor, // 등급 결정 Processor
            MemberUpdateItemWriter memberUpdateItemWriter) { // Member 정보를 업데이트하는 Writer

        return new StepBuilder("membershipUpdateStep", jobRepository) // Step 이름과 JobRepository 사용
                .<Member, Member>chunk(1000, transactionManager) // Member 타입을 1000개씩 청크 단위로 처리, 트랜잭션 관리
                .reader(memberReader) // 읽기 담당 ItemReader 지정
                .processor(membershipLevelProcessor) // 처리 담당 ItemProcessor 지정
                .writer(memberUpdateItemWriter) // 쓰기 담당 ItemWriter 지정
                .build(); // Step 빌드
    }

     /**
     * ItemReader: DB에서 Member 엔티티를 페이징하여 읽어옵니다.
     * - JpaPagingItemReader는 대량의 데이터를 효율적으로 읽어올 때 사용됩니다.
     * - @Query 어노테이션의 `SELECT m FROM Member m` 쿼리는 모든 Member를 조회하지만,
     *   `JpaPagingItemReader`가 자동으로 `ORDER BY m.id`와 `LIMIT` 절을 추가하여 페이징 쿼리를 생성합니다.
     */

     @Bean
     public JpaPagingItemReader<Member> memberReader() {
        JpaPagingItemReader<Member> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("select m from Member m");
        reader.setPageSize(1000);
        reader.setName("memberPagingReader");
        return reader;
     }

}
