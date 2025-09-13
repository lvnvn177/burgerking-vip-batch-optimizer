package com.burgerking.membership.repository;

import com.burgerking.membership.domain.Membership;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    /**
     * userId로 멤버십 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return Optional<Membership>
     */
    Optional<Membership> findByUserId(Long userId);
}
