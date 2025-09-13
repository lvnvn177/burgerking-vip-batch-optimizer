package com.burgerking.membership.repository;


import com.burgerking.membership.domain.MonthlyOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyOrderRepository extends JpaRepository<MonthlyOrder, Long> {
    /**
     * userId와 특정 연월에 해당하는 월별 주문 정보를 조회합니다.
     *
     * @param userId    사용자 ID
     * @param yearMonth 조회할 년월
     * @return Optional<MonthlyOrder>
     */
    Optional<MonthlyOrder> findByUserIdAndYearMonth(Long userId, YearMonth yearMonth);

    /**
     * 특정 기간 사이의 모든 월별 주문 정보를 조회합니다.
     *
     * @param startMonth 시작 년월
     * @param endMonth   종료 년월
     * @return List<MonthlyOrder>
     */
    List<MonthlyOrder> findByYearMonthBetween(YearMonth startMonth, YearMonth endMonth);

    /**
     * 특정 사용자의 특정 기간 사이의 월별 주문 정보를 년월 오름차순으로 조회합니다. (3개월 누적 계산용)
     * 예를 들어, 현재 9월이면 6, 7, 8월 데이터를 가져옵니다.
     *
     * @param userId     사용자 ID
     * @param startMonth 시작 년월
     * @param endMonth   종료 년월
     * @return List<MonthlyOrder>
     */
    List<MonthlyOrder> findByUserIdAndYearMonthBetweenOrderByYearMonthAsc(Long userId, YearMonth startMonth, YearMonth endMonth);

}
