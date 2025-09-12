package com.burgerking.reservation.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    PENDING("대기중", "예약이 생성되었지만 확정되지 않은 상태"),
    CONFIRMED("확정됨", "예약이 확정된 상태"),
    CANCELED("취소됨", "사용자 또는 관리자에 의해 취소된 상태"),
    COMPLETED("완료됨", "예약 서비스가 완료된 상태"),
    NO_SHOW("노쇼", "예약 시간에 방문하지 않은 상태");

    private final String displayName; // 화면에 표시될 이름
    private final String description; // 상태에 대한 설명
}