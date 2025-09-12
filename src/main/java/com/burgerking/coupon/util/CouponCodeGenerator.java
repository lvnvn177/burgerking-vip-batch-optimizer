package com.burgerking.coupon.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 쿠폰 코드 생성을 위한 유틸리티 클래스
 */
@Component
public class CouponCodeGenerator {
    
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    /**
     * UUID 기반의 쿠폰 코드를 생성합니다.
     * @return UUID 기반 쿠폰 코드
     */
    public String generateUuidCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    /**
     * 날짜와 랜덤 문자열이 조합된 쿠폰 코드를 생성합니다.
     * 형식: {날짜(yyMMdd)}-{랜덤 알파벳 6자리}
     * @return 날짜 기반 쿠폰 코드
     */
    public String generateDateBasedCode() {
        String datePrefix = LocalDateTime.now().format(DATE_FORMATTER);
        String randomSuffix = generateRandomString(6);
        return datePrefix + "-" + randomSuffix;
    }

    /**
     * 쿠폰 타입에 따른 접두사와 랜덤 문자열이 조합된 쿠폰 코드를 생성합니다.
     * @param prefix 쿠폰 타입 접두사 (예: "DIS" for discount, "FREE" for free item)
     * @param length 랜덤 문자열 길이
     * @return 접두사 기반 쿠폰 코드
     */
    public String generatePrefixedCode(String prefix, int length) {
        return prefix + "-" + generateRandomString(length);
    }

      /**
     * 지정된 길이의 랜덤 영숫자 문자열을 생성합니다.
     * @param length 생성할 문자열 길이
     * @return 랜덤 영숫자 문자열
     */
    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    } 
}
