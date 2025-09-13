package com.burgerking.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 날짜 및 시간 관련 유틸리티 메서드를 제공하는 클래스입니다.
 */
public final class DateTimeUtils {

    private DateTimeUtils() {
        // 유틸리티 클래스는 인스턴스화하지 않습니다.
    }

    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);

    /**
     * LocalDateTime 객체를 기본 포맷(yyyy-MM-dd HH:mm:ss)의 문자열로 변환합니다.
     *
     * @param dateTime 변환할 LocalDateTime 객체
     * @return 포맷팅된 문자열
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DEFAULT_DATE_TIME_FORMATTER);
    }

    /**
     * LocalDateTime 객체를 지정된 포맷의 문자열로 변환합니다.
     *
     * @param dateTime 변환할 LocalDateTime 객체
     * @param pattern  적용할 날짜/시간 포맷
     * @return 포맷팅된 문자열
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 기본 포맷의 문자열을 LocalDateTime 객체로 파싱합니다.
     *
     * @param dateTimeString 파싱할 날짜/시간 문자열 (e.g., "2023-10-27 10:00:00")
     * @return 파싱된 LocalDateTime 객체
     */
    public static LocalDateTime parse(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DEFAULT_DATE_TIME_FORMATTER);
    }

    /**
     * 현재 시간을 나타내는 LocalDateTime 객체를 반환합니다.
     *
     * @return 현재 시간의 LocalDateTime 객체
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
