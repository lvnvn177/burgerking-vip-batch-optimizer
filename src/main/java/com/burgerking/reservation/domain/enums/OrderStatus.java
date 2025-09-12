package com.burgerking.reservation.domain.enums;

public enum OrderStatus {
    PENDING,    // 대기중
    CONFIRMED,  // 확인됨
    PREPARING,  // 준비중
    READY,      // 준비완료
    COMPLETED,  // 완료
    CANCELED    // 취소됨
}