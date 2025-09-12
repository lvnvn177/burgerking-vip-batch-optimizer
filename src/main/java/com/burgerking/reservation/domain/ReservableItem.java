package com.burgerking.reservation.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.burgerking.reservation.domain.enums.ReservableItemType;

@Entity
@Table(name = "reservable_items") // 테이블 명은 '예약 가능한 항목들'로 명명했습니다.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservableItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예약 가능 항목의 고유 ID

    @Column(nullable = false, length = 100)
    private String name; // 항목 이름 (예: "테이블 A", "회의실 1", "VIP 룸")

    @Column(length = 255)
    private String description; // 항목에 대한 상세 설명

    @Column(nullable = false)
    private Integer capacity; // 최대 수용 인원 (예: 4인 테이블, 10인 회의실)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // DB에 문자열로 저장
    private ReservableItemType itemType; // String에서 enum으로 변경 항목 유형 (예: "TABLE", "ROOM", "SERVICE") - enum으로 관리할 수도 있습니다.

    @Column(length = 100)
    private String location; // 항목의 위치 정보 (예: "1층 홀", "5층")

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성 일시

    @Column
    private LocalDateTime updatedAt; // 마지막 업데이트 일시

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}