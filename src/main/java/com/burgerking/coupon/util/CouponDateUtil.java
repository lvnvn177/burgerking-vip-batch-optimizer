package com.burgerking.coupon.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 쿠폰 관련 날짜 처리를 위한 유틸리티 클래스
 */
public class CouponDateUtil {
    
    private CouponDateUtil() {
        // 유틸리티 클래스 인스턴스화 방지 
    }

    /**
     * 쿠폰이 현재 유효한지 확인합니다.
     * @param startDate 쿠폰 사용 시작일
     * @param endDate 쿠폰 사용 종료일
     * @return 유효 여부
     */
    public static boolean isValid(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    /**
     * 쿠폰의 남은 유효 기간(일)을 계산합니다.
     * @param endDate 쿠폰 사용 종료일
     * @return 남은 유효 기간(일)
     */
    public static long getRemaingDays(LocalDateTime endDateTime) {
        return ChronoUnit.DAYS.between(LocalDateTime.now(), endDateTime);
    }

    /**
     * 현재 시점부터 지정된 일수만큼 이후의 날짜를 계산합니다.
     * @param days 추가할 일수
     * @return 계산된 만료일
     */
    public static LocalDateTime calculateExpiryDate(int days) {
        return LocalDateTime.now().plusDays(days);
    }

     /**
     * 쿠폰이 곧 만료되는지 확인합니다 (3일 이내).
     * @param endDate 쿠폰 사용 종료일
     * @return 곧 만료 여부
     */
    public static boolean isExpiringSoon(LocalDateTime endDate) {
        long remainingDays = getRemaingDays(endDate);
        return remainingDays >= 0 & remainingDays <= 3;
    }

      /**
     * 쿠폰이 이미 만료되었는지 확인합니다.
     * @param endDate 쿠폰 사용 종료일
     * @return 만료 여부
     */
    public static boolean isExpired(LocalDateTime endDate) {
        return LocalDateTime.now().isAfter(endDate);
    } 

    /**
     * 쿠폰 사용이 가능한 상태인지 확인합니다.
     * @param startDate 쿠폰 사용 시작일
     * @param endDate 쿠폰 사용 종료일
     * @return 사용 가능 여부
     */
    public static boolean isUsable(LocalDateTime startDate, LocalDateTime endDate) {
        return isValid(startDate, endDate) && !isExpired(endDate);
    }
}
