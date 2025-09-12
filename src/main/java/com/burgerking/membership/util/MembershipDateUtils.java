package com.burgerking.membership.util;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * 멤버십 시스템에서 사용하는 날짜/시간 유틸리티 클래스
 * 주로 월별 주문 데이터와 관련된 날짜 변환 기능을 제공합니다.
 */
public class MembershipDateUtils {

    private MembershipDateUtils() {
        // 유틸리티 클래스 인스턴스화 방지
    }

    // 기본 날짜 포맷
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    // 월 표시 포맷 (예: 2025-09)
    public static final String YEAR_MONTH_FORMAT = "yyyy-MM";
    
    /**
     * LocalDateTime을 기본 날짜/시간 형식의 문자열로 변환
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT));
    }
    
    /**
     * YearMonth를 yyyy-MM 형식의 문자열로 변환
     */
    public static String formatYearMonth(YearMonth yearMonth) {
        if (yearMonth == null) return null;
        return yearMonth.format(DateTimeFormatter.ofPattern(YEAR_MONTH_FORMAT));
    }
    
    /**
     * 문자열을 YearMonth로 변환
     */
    public static YearMonth parseYearMonth(String yearMonthStr) {
        if (yearMonthStr == null || yearMonthStr.isEmpty()) return null;
        return YearMonth.parse(yearMonthStr, DateTimeFormatter.ofPattern(YEAR_MONTH_FORMAT));
    }
    
    /**
     * 현재 년월(YearMonth) 반환
     */
    public static YearMonth getCurrentYearMonth() {
        return YearMonth.now();
    }
    
    /**
     * 지정된 개월 수만큼 이전의 YearMonth 반환
     */
    public static YearMonth getMonthsAgo(int months) {
        return YearMonth.now().minusMonths(months);
    }
    
    /**
     * 멤버십 등급 평가 기간의 시작월 계산 (현재 기준 3개월 전)
     */
    public static YearMonth getEvaluationStartMonth() {
        return YearMonth.now().minusMonths(3);
    }
    
    /**
     * 멤버십 등급 평가 기간의 종료월 계산 (현재 기준 직전 월)
     */
    public static YearMonth getEvaluationEndMonth() {
        return YearMonth.now().minusMonths(1);
    }
}