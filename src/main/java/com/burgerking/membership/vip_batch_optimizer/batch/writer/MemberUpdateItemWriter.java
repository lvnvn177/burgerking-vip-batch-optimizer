package com.burgerking.membership.vip_batch_optimizer.batch.writer;

import com.burgerking.membership.vip_batch_optimizer.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberUpdateItemWriter implements ItemWriter<Member> {

    private final EntityManagerFactory entityManagerFactory;

    public MemberUpdateItemWriter(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void write(Chunk<? extends Member> membersChunk) throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        try {
            List<? extends Member> members = membersChunk.getItems();

            for (Member member : members) {
                // JPA merge를 통한 업데이트 - 영속성 컨텍스트에 등록하고 변경 사항을 DB에 반영
                entityManager.merge(member);
            }
            
            // 모든 변경사항을 하나의 트랜잭션으로 커밋 (chunk 단위)
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }
}