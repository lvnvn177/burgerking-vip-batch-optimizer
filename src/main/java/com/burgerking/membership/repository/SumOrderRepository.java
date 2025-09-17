package com.burgerking.membership.repository;


import com.burgerking.membership.domain.SumOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SumOrderRepository extends JpaRepository<SumOrder, Long> {
    /**
     * userId의 누적 주문 정보를 조회합니다.
     *
     * @param userId    사용자 ID
     * @param yearMonth 조회할 년월
     * @return Optional<SumOrder>
     */
    SumOrder findByUserId(Long userId);
}
