package com.burgerking.common.util;

public class CommonUtils {
    
    private CommonUtils() {
        // 인스턴스화 방지
    }

    /**
     * 객체가 null이 아닌지 확인합니다.
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * 문자열이 비어있거나 null인지 확인합니다.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 두 객체가 동일한지 비교합니다 (null 안전).
     */
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
