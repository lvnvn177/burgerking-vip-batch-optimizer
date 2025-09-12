package com.burgerking.membership.repository;


import com.burgerking.membership.domain.MonthlyOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyOrderRepository extends JpaRepository<MonthlyOrder, Long> {
    // userId와 특정 연월에 해당하는 월별 주문 정보 조회
    Optional<MonthlyOrder> findByUserIdAndYearMonth(Long userId, YearMonth yearMonth);

    // userId와 시작/종료 연월 사이의 월별 주문 총액 리스트 조회 (3개월 누적 계산용)
    // 예를 들어, 현재 9월이면 6, 7, 8월 데이터를 가져옴
    List<MonthlyOrder> findByUserIdAndYearMonthBetweemOrderByYearMonthAsc (Long userId, YearMonth startMonth, YearMonth endMonth);

}
