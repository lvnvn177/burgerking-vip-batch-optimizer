package com.burgerking.membership.vip_batch_optimizer.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.burgerking.membership.vip_batch_optimizer.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT m FROM Member m WHERE m.lastLevelUpdatedDate < :date OR m.lastLevelUpdatedDate IS NULL")
    List<Member> findMembersForLevelUpdate(@Param("date") LocalDateTime date);
    
    @Query(value = "SELECT m.id FROM Member m", nativeQuery = true)
    List<Long> findAllMemberIds();
}