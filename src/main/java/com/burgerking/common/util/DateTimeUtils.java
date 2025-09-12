package com.burgerking.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    
    private DateTimeUtils() {
        // 인스턴스화 방지
    }

    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);

    /**
     * LocalDateTime을 기본 포맷(yyyy-MM-dd HH:mm:ss)의 문자열로 변환합니다.
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DEFAULT_DATE_TIME_FORMATTER);
    }

     /**
     * LocalDateTime을 지정된 포맷의 문자열로 변환합니다.
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

     /**
     * 문자열을 LocalDateTime으로 변환합니다 (기본 포맷).
     */
    public static LocalDateTime parse(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DEFAULT_DATE_TIME_FORMATTER);
    }

    /**
     * 현재 시간을 LocalDateTime으로 반환합니다.
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
