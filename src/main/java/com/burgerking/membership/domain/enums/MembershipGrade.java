package com.burgerking.membership.domain.enums;

import lombok.Getter;

@Getter
public enum MembershipGrade {
    BRONZE("브론즈", 0),
    SILVER("실버", 100000),
    GOLD("골드", 300000),
    PLATINUM("플래티넘", 600000),
    VIP("VIP", 1000000);

    private final String displayName;
    private final int requiredAmount; // 3개월 누적 금액 기준

    MembershipGrade(String displayName, int requiredAmount) {
        this.displayName = displayName;
        this.requiredAmount = requiredAmount;
    }
    
    /**
     * 3개월 누적 주문 금액에 따라 적절한 등급 반환
     * @param last3MonthAmount 최근 3개월 누적 주문 금액
     * @return 해당되는 멤버십 등급
     */
    public static MembershipGrade evaluateGrade(int last3MonthAmount) {
        if (last3MonthAmount >= VIP.requiredAmount) {
            return VIP;
        } else if (last3MonthAmount >= PLATINUM.requiredAmount) {
            return PLATINUM;
        } else if (last3MonthAmount >= GOLD.requiredAmount) {
            return GOLD;
        } else if (last3MonthAmount >= SILVER.requiredAmount) {
            return SILVER;
        } else {
            return BRONZE;
        }
    }
}