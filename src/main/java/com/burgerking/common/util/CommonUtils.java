package com.burgerking.common.util;

/**
 * 애플리케이션 전반에서 사용되는 공통 유틸리티 메서드를 제공하는 클래스입니다.
 */
public final class CommonUtils {

    private CommonUtils() {
        // 유틸리티 클래스는 인스턴스화하지 않습니다.
    }

    /**
     * 객체가 null이 아닌지 확인합니다.
     *
     * @param obj 확인할 객체
     * @return null이 아니면 true, null이면 false
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * 문자열이 비어있거나(empty) null인지 확인합니다.
     *
     * @param str 확인할 문자열
     * @return 비어있거나 null이면 true, 그렇지 않으면 false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 두 객체가 동일한지 null-safe하게 비교합니다.
     *
     * @param a 비교할 첫 번째 객체
     * @param b 비교할 두 번째 객체
     * @return 두 객체가 동일하면 true, 그렇지 않으면 false
     */
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
