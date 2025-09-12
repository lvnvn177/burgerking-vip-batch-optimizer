package com.burgerking.membership.domain.enums;

import lombok.Getter;

@Getter
public enum MembershipGrade {
    BRONZE("브론즈", 0, 0.01),
    SILVER("실버", 100000, 0.03),
    GOLD("골드", 300000, 0.05),
    PLATINUM("플래티넘", 600000, 0.07),
    VIP("VIP", 1000000, 0.10);

    private final String displayName;
    private final int requiredAmount; // 3개월 누적 금액 기준
    private final double pointRate;

    MembershipGrade(String displayName, int requiredAmount, double pointRate) {
        this.displayName = displayName;
        this.requiredAmount = requiredAmount;
        this.pointRate = pointRate;
    }
    
    // 3개월 누적 주문 금액에 따라 적절한 등급 반환
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
    
    // 주문 금액에 대한 적립 포인트 계산
    public int calculatePoints(int orderAmount) {
        return (int)(orderAmount * this.pointRate);
    }
}