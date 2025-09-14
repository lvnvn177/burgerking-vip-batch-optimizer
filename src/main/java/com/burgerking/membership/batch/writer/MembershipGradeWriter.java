package com.burgerking.membership.batch.writer;

import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.repository.MembershipRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

/**
 * 멤버십 정보를 저장하는 ItemWriter
 * 등급이 갱신된 멤버십 정보를 데이터베이스에 저장합니다.
 */
@Slf4j
public class MembershipGradeWriter implements ItemWriter<Membership> {
    
    private final MembershipRepository membershipRepository;

    public MembershipGradeWriter(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Override
    public void write(@SuppressWarnings("null") Chunk<? extends Membership> chunk) throws Exception {
        log.info("멤버십 등급 갱신 배치 - {} 건 처리", chunk.size());

        for (Membership membership : chunk) {
            log.info("멤버십 등급 갱신: 사용자 ID={}, 이전 등급={}, 새 등급={}",
                    membership.getUserId(),
                    membership.getGrade(),
                    membership.getGrade());
            
            membershipRepository.save(membership);
        }

        log.info("멤버십 등급 갱신 완료");
    }
}
