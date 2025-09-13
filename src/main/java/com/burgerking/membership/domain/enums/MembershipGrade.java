package com.burgerking.membership.domain.enums;

import lombok.Getter;

@Getter
public enum MembershipGrade {
    BRONZE("브론즈", 0),          // 브론즈 등급
    SILVER("실버", 100000),      // 실버 등급
    GOLD("골드", 300000),        // 골드 등급
    PLATINUM("플래티넘", 600000),  // 플래티넘 등급
    VIP("VIP", 1000000);        // VIP 등급

    private final String displayName;       // 등급 표시 이름
    private final int requiredAmount;       // 등급 달성에 필요한 3개월 누적 주문 금액

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