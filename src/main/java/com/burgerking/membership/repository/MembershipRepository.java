package com.burgerking.membership.repository;

import com.burgerking.membership.domain.Membership;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    // userId로 멤버쉽 정보를 조회하는 메서드
    Optional<Membership> findByUserId(Long userId);
}
