package com.burgerking.reservation.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservableItemType {
    TABLE("테이블", "식당 테이블"),
    ROOM("룸", "독립된 공간"),
    VIP_ROOM("VIP룸", "VIP 전용 공간"),
    OUTDOOR("야외석", "야외 테이블"),
    BAR("바", "바 카운터 좌석"),
    PARTY_SPACE("파티공간", "단체 행사용 공간");

    private final String displayName; // 화면에 표시될 이름
    private final String description; // 타입에 대한 설명
}